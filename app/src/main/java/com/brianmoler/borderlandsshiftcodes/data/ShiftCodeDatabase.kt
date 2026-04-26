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
 * - Local caching of remote CSV data
 * - User redemption tracking
 * - Soft deletion for sync operations
 * - Database migrations for future schema changes
 */
@Database(
    entities = [ShiftCodeEntity::class],
    version = 3,
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
                // Add expirationTime column (defaults to empty string)
                db.execSQL("ALTER TABLE shift_codes ADD COLUMN expirationTime TEXT NOT NULL DEFAULT ''")
                
                // Add isKey column (defaults to false)
                db.execSQL("ALTER TABLE shift_codes ADD COLUMN isKey INTEGER NOT NULL DEFAULT 0")
                
                // Add isCosmetic column (defaults to false)
                db.execSQL("ALTER TABLE shift_codes ADD COLUMN isCosmetic INTEGER NOT NULL DEFAULT 0")
                
                // Add isGear column (defaults to false)
                db.execSQL("ALTER TABLE shift_codes ADD COLUMN isGear INTEGER NOT NULL DEFAULT 0")
            }
        }

        /**
         * Migration from version 2 to 3.
         * Deduplicates existing rows by code, then enforces uniqueness on code.
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Keep one row per code, preferring non-deleted, most recently updated, then highest ID.
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

                // Enforce unique code values to prevent duplicate insert races.
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
                    .fallbackToDestructiveMigration(false) // For development - remove in production
                .build()
                INSTANCE = instance
                instance
            }
        }

    }
}
