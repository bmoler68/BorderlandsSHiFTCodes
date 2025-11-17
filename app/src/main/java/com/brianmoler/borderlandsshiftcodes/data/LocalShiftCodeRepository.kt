package com.brianmoler.borderlandsshiftcodes.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for local SHiFT code database operations.
 * 
 * This repository provides a clean interface for all local database operations,
 * including CRUD operations, filtering, and sync-related functionality.
 */
class LocalShiftCodeRepository(
    private val shiftCodeDao: ShiftCodeDao
) {
    companion object {
        private const val TAG = "LocalShiftCodeRepository"
    }

    /**
     * Gets all active SHiFT codes as a Flow.
     * @return Flow of active ShiftCodeEntity objects
     */
    private fun getAllActiveCodes(): Flow<List<ShiftCodeEntity>> {
        return shiftCodeDao.getAllActiveCodes()
    }

    /**
     * Gets all active SHiFT codes as a suspend function.
     * @return List of active ShiftCodeEntity objects
     */
    suspend fun getAllActiveCodesSync(): List<ShiftCodeEntity> {
        return shiftCodeDao.getAllActiveCodesSync()
    }

    /**
     * Gets all unredeemed SHiFT codes as a Flow.
     * @return Flow of unredeemed ShiftCodeEntity objects
     */
    private fun getUnredeemedCodes(): Flow<List<ShiftCodeEntity>> {
        return shiftCodeDao.getUnredeemedCodes()
    }


    /**
     * Gets non-expiring codes as a Flow.
     * @return Flow of non-expiring ShiftCodeEntity objects
     */
    private fun getNonExpiringCodes(): Flow<List<ShiftCodeEntity>> {
        return shiftCodeDao.getNonExpiringCodes()
    }


    /**
     * Inserts or updates a SHiFT code entity.
     * @param shiftCode The ShiftCodeEntity to insert/update
     */
    suspend fun insertOrUpdate(shiftCode: ShiftCodeEntity) {
        shiftCodeDao.insertOrUpdate(shiftCode)
    }

    /**
     * Updates the redemption status of a specific code.
     * @param code The SHiFT code string
     * @param isRedeemed The new redemption status
     * @return true if the update was successful, false otherwise
     */
    suspend fun updateRedemptionStatus(code: String, isRedeemed: Boolean): Boolean {
        return try {
            shiftCodeDao.updateRedemptionStatus(code, isRedeemed)
            Log.d(TAG, "Updated redemption status for code $code to $isRedeemed")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update redemption status for code $code", e)
            false
        }
    }

    /**
     * Soft deletes multiple SHiFT codes.
     * @param codes List of SHiFT code strings to soft delete
     */
    suspend fun softDeleteMultiple(codes: List<String>) {
        if (codes.isNotEmpty()) {
            shiftCodeDao.softDeleteMultiple(codes)
            Log.d(TAG, "Soft deleted ${codes.size} codes")
        }
    }

    /**
     * Restores a soft-deleted SHiFT code.
     * @param code The SHiFT code string to restore
     */
    suspend fun restoreDeleted(code: String) {
        shiftCodeDao.restoreDeleted(code)
        Log.d(TAG, "Restored deleted code: $code")
    }

    /**
     * Soft deletes SHiFT codes by their entity IDs.
     * @param ids List of entity IDs to soft delete
     */
    suspend fun softDeleteByIds(ids: List<Long>) {
        if (ids.isNotEmpty()) {
            shiftCodeDao.softDeleteByIds(ids)
            Log.d(TAG, "Soft deleted ${ids.size} codes by ID")
        }
    }






    /**
     * Gets codes filtered by filter type, game filter, and reward filter.
     * @param filterType The filter type to apply
     * @param gameFilter The game filter type to apply
     * @param rewardFilter The reward filter type to apply
     * @return Flow of filtered ShiftCodeEntity objects
     */
    fun getFilteredCodes(filterType: FilterType, gameFilter: GameFilterType, rewardFilter: RewardFilterType): Flow<List<ShiftCodeEntity>> {
        // For ACTIVE and EXPIRED filters, fetch all codes and filter in application code
        // to properly account for expiration date + time + timezone
        val baseFlow = when (filterType) {
            FilterType.ALL -> getAllActiveCodes()
            FilterType.ACTIVE -> getAllActiveCodes() // Filter by isExpired() in code below
            FilterType.EXPIRED -> getAllActiveCodes() // Filter by isExpired() in code below
            FilterType.NON_EXPIRING -> getNonExpiringCodes()
            FilterType.NOT_REDEEMED -> getUnredeemedCodes()
        }
        
        return baseFlow.map { codes ->
            var filteredCodes = codes
            
            // Apply expiration status filter in application code (considers date + time + timezone)
            when (filterType) {
                FilterType.ACTIVE -> {
                    filteredCodes = filteredCodes.filter { 
                        !it.isExpired() && !it.isNonExpiring() 
                    }
                }
                FilterType.EXPIRED -> {
                    filteredCodes = filteredCodes.filter { it.isExpired() }
                }
                else -> { 
                    // Other filters (ALL, NON_EXPIRING, NOT_REDEEMED) already filtered by baseFlow
                }
            }
            
            // Apply game filter
            if (gameFilter != GameFilterType.ALL_GAMES) {
                filteredCodes = filteredCodes.filter { entity ->
                    when (gameFilter) {
                        GameFilterType.BL -> entity.bl
                        GameFilterType.BL_TPS -> entity.blTps
                        GameFilterType.BL2 -> entity.bl2
                        GameFilterType.BL3 -> entity.bl3
                        GameFilterType.BL4 -> entity.bl4
                        GameFilterType.WONDERLANDS -> entity.wonderlands
                        else -> true
                    }
                }
            }
            
            // Apply reward filter
            if (rewardFilter != RewardFilterType.ALL_REWARDS) {
                filteredCodes = filteredCodes.filter { entity ->
                    when (rewardFilter) {
                        RewardFilterType.KEY -> entity.isKey
                        RewardFilterType.COSMETIC -> entity.isCosmetic
                        RewardFilterType.GEAR -> entity.isGear
                        else -> true
                    }
                }
            }
            
            filteredCodes
        }
    }

}

/**
 * Filter types for SHiFT codes (moved from ViewModel for reuse)
 */
enum class FilterType {
    ALL, ACTIVE, EXPIRED, NON_EXPIRING, NOT_REDEEMED
}

/**
 * Game filter types for SHiFT codes (moved from ViewModel for reuse)
 */
enum class GameFilterType {
    ALL_GAMES, BL, BL_TPS, BL2, BL3, BL4, WONDERLANDS
}

/**
 * Reward filter types for SHiFT codes
 */
enum class RewardFilterType {
    ALL_REWARDS, KEY, COSMETIC, GEAR
}