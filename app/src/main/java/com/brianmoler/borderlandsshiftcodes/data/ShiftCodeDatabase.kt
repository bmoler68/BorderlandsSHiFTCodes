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
    version = 2,
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
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add expirationTime column (defaults to empty string)
                database.execSQL("ALTER TABLE shift_codes ADD COLUMN expirationTime TEXT NOT NULL DEFAULT ''")
                
                // Add isKey column (defaults to false)
                database.execSQL("ALTER TABLE shift_codes ADD COLUMN isKey INTEGER NOT NULL DEFAULT 0")
                
                // Add isCosmetic column (defaults to false)
                database.execSQL("ALTER TABLE shift_codes ADD COLUMN isCosmetic INTEGER NOT NULL DEFAULT 0")
                
                // Add isGear column (defaults to false)
                database.execSQL("ALTER TABLE shift_codes ADD COLUMN isGear INTEGER NOT NULL DEFAULT 0")
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
                    .fallbackToDestructiveMigration(false) // For development - remove in production
                .build()
                INSTANCE = instance
                instance
            }
        }

    }
}
