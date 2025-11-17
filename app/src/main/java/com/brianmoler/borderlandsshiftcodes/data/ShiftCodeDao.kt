package com.brianmoler.borderlandsshiftcodes.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for ShiftCodeEntity database operations.
 * 
 * This interface defines all database operations for managing SHiFT codes
 * in the local Room database, including CRUD operations, filtering,
 * and sync-related queries.
 */
@Dao
interface ShiftCodeDao {

    /**
     * Gets all active (non-deleted) SHiFT codes from the database.
     * @return Flow of all active ShiftCodeEntity objects
     */
    @Query("""
        SELECT * FROM shift_codes 
        WHERE isDeleted = 0 
        ORDER BY expiration DESC
    """)
    fun getAllActiveCodes(): Flow<List<ShiftCodeEntity>>

    /**
     * Gets all active SHiFT codes as a suspend function.
     * @return List of all active ShiftCodeEntity objects
     */
    @Query("""
        SELECT * FROM shift_codes 
        WHERE isDeleted = 0 
        ORDER BY expiration DESC
    """)
    suspend fun getAllActiveCodesSync(): List<ShiftCodeEntity>

    /**
     * Gets all unredeemed SHiFT codes.
     * @return Flow of unredeemed ShiftCodeEntity objects
     */
    @Query("""
        SELECT * FROM shift_codes 
        WHERE isRedeemed = 0 AND isDeleted = 0 
        ORDER BY expiration DESC
    """)
    fun getUnredeemedCodes(): Flow<List<ShiftCodeEntity>>


    /**
     * Gets non-expiring codes.
     * @return Flow of non-expiring ShiftCodeEntity objects
     */
    @Query("SELECT * FROM shift_codes WHERE isDeleted = 0 AND expiration = '1999-12-31' ORDER BY expiration DESC")
    fun getNonExpiringCodes(): Flow<List<ShiftCodeEntity>>



    /**
     * Inserts or replaces a SHiFT code entity.
     * @param shiftCode The ShiftCodeEntity to insert/update
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(shiftCode: ShiftCodeEntity)


    /**
     * Updates the redemption status of a specific code.
     * @param code The SHiFT code string
     * @param isRedeemed The new redemption status
     */
    @Query("UPDATE shift_codes SET isRedeemed = :isRedeemed, lastUpdated = :timestamp WHERE code = :code")
    suspend fun updateRedemptionStatus(code: String, isRedeemed: Boolean, timestamp: Long = System.currentTimeMillis())

    /**
     * Soft deletes multiple SHiFT codes by marking them as deleted.
     * @param codes List of SHiFT code strings to soft delete
     */
    @Query("UPDATE shift_codes SET isDeleted = 1, lastUpdated = :timestamp WHERE code IN (:codes)")
    suspend fun softDeleteMultiple(codes: List<String>, timestamp: Long = System.currentTimeMillis())

    /**
     * Restores a soft-deleted SHiFT code by marking it as not deleted.
     * @param code The SHiFT code string to restore
     */
    @Query("UPDATE shift_codes SET isDeleted = 0, lastUpdated = :timestamp WHERE code = :code")
    suspend fun restoreDeleted(code: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Soft deletes SHiFT codes by their IDs.
     * @param ids List of entity IDs to soft delete
     */
    @Query("UPDATE shift_codes SET isDeleted = 1, lastUpdated = :timestamp WHERE id IN (:ids)")
    suspend fun softDeleteByIds(ids: List<Long>, timestamp: Long = System.currentTimeMillis())



}

