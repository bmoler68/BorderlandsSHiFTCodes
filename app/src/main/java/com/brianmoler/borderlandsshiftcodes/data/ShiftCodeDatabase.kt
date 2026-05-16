package com.brianmoler.borderlandsshiftcodes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room database for storing SHiFT codes locally.
 *
 * This database provides offline storage for SHiFT codes with support for:
 * - Local caching of Supabase-synced codes
 * - User redemption tracking
 * - Soft deletion for sync operations
 * - Database migrations for future schema changes
 */
@Database(
    entities = [ShiftCodeEntity::class],
    version = 4,
    exportSchema = false
)
abstract class ShiftCodeDatabase : RoomDatabase() {

    /**
     * Provides access to the ShiftCodeDao for database operations.
     * @return ShiftCodeDao instance
     */
    abstract fun shiftCodeDao(): ShiftCodeDao

    companion object {
        private const val DATABASE_NAME = "shift_codes_database"

        @Volatile
        private var INSTANCE: ShiftCodeDatabase? = null

        /**
         * Migration from version 1 to 2.
         * Adds new columns: expirationTime, isKey, isCosmetic, isGear
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE shift_codes ADD COLUMN expirationTime TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE shift_codes ADD COLUMN isKey INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE shift_codes ADD COLUMN isCosmetic INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE shift_codes ADD COLUMN isGear INTEGER NOT NULL DEFAULT 0")
            }
        }

        /**
         * Migration from version 2 to 3.
         * Deduplicates existing rows by code, then enforces uniqueness on code.
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    DELETE FROM shift_codes
                    WHERE id NOT IN (
                        SELECT winner.id
                        FROM shift_codes AS winner
                        WHERE winner.id = (
                            SELECT candidate.id
                            FROM shift_codes AS candidate
                            WHERE candidate.code = winner.code
                            ORDER BY candidate.isDeleted ASC, candidate.lastUpdated DESC, candidate.id DESC
                            LIMIT 1
                        )
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_shift_codes_code ON shift_codes(code)"
                )
            }
        }

        /**
         * Migration from version 3 to 4 (app 2.0.0).
         * Rebuilds table for Supabase-aligned expiration flags and ingest time for dashboard-parity sorting.
         */
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS shift_codes_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        code TEXT NOT NULL,
                        reward TEXT NOT NULL,
                        expirationDate TEXT,
                        expirationTime TEXT NOT NULL DEFAULT '',
                        isNonExpiring INTEGER NOT NULL DEFAULT 0,
                        isUnknownExpiration INTEGER NOT NULL DEFAULT 0,
                        bl INTEGER NOT NULL,
                        blTps INTEGER NOT NULL,
                        bl2 INTEGER NOT NULL,
                        bl3 INTEGER NOT NULL,
                        bl4 INTEGER NOT NULL,
                        wonderlands INTEGER NOT NULL,
                        isKey INTEGER NOT NULL DEFAULT 0,
                        isCosmetic INTEGER NOT NULL DEFAULT 0,
                        isGear INTEGER NOT NULL DEFAULT 0,
                        isDeleted INTEGER NOT NULL DEFAULT 0,
                        isRedeemed INTEGER NOT NULL DEFAULT 0,
                        ingestedAtUtcMillis INTEGER NOT NULL DEFAULT -9223372036854775808,
                        lastUpdated INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO shift_codes_new (
                        id, code, reward, expirationDate, expirationTime,
                        isNonExpiring, isUnknownExpiration,
                        bl, blTps, bl2, bl3, bl4, wonderlands,
                        isKey, isCosmetic, isGear,
                        isDeleted, isRedeemed, ingestedAtUtcMillis, lastUpdated
                    )
                    SELECT
                        id, code, reward,
                        CASE WHEN expiration IN ('1999-12-31', '2075-12-31') THEN NULL ELSE expiration END,
                        expirationTime,
                        CASE WHEN expiration = '1999-12-31' THEN 1 ELSE 0 END,
                        CASE WHEN expiration = '2075-12-31' THEN 1 ELSE 0 END,
                        bl, blTps, bl2, bl3, bl4, wonderlands,
                        isKey, isCosmetic, isGear,
                        isDeleted, isRedeemed, -9223372036854775808, lastUpdated
                    FROM shift_codes
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE shift_codes")
                db.execSQL("ALTER TABLE shift_codes_new RENAME TO shift_codes")
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_shift_codes_code ON shift_codes(code)"
                )
            }
        }

        /**
         * Gets the singleton instance of the ShiftCodeDatabase.
         *
         * @param context Application context
         * @return ShiftCodeDatabase instance
         */
        fun getDatabase(context: Context): ShiftCodeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShiftCodeDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
