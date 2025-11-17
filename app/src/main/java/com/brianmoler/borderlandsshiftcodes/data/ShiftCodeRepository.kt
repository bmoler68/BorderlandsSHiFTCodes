package com.brianmoler.borderlandsshiftcodes.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "ShiftCodeRepository"

/**
 * Result of a sync operation with the remote data source.
 */
data class SyncResult(
    val isSuccess: Boolean,
    val codesAdded: Int = 0,
    val codesUpdated: Int = 0,
    val codesDeleted: Int = 0,
    val error: String? = null
)

/**
 * Main repository for managing SHiFT codes data operations.
 * 
 * This repository coordinates between remote data fetching and local database operations,
 * providing a unified interface for the UI layer. It handles:
 * - Fetching data from remote CSV sources
 * - Syncing remote data with local database
 * - Preserving user data (redemption status) during sync
 * - Providing offline-first data access
 */
class ShiftCodeRepository(
    private val remoteRepository: RemoteShiftCodeRepository,
    private val localRepository: LocalShiftCodeRepository
) {

    /**
     * Syncs the local database with remote CSV data.
     * This method:
     * 1. Fetches fresh data from remote CSV
     * 2. Compares with local database
     * 3. Updates, adds, or soft-deletes codes as needed
     * 4. Preserves user redemption status
     * 
     * @return SyncResult indicating the outcome of the sync operation
     */
    suspend fun syncWithRemoteData(): SyncResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting sync with remote data")
            
            // Fetch remote data
            val remoteCodes = remoteRepository.fetchShiftCodes()
            Log.d(TAG, "Fetched ${remoteCodes.size} codes from remote source")
            
            // Perform sync
            val syncResult = performSync(remoteCodes)
            Log.d(TAG, "Sync completed: $syncResult")
            
            syncResult
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            SyncResult(
                isSuccess = false,
                error = e.message ?: "Unknown sync error"
            )
        }
    }

    /**
     * Performs the actual sync operation between remote and local data.
     * @param remoteCodes List of codes from remote source
     * @return SyncResult with sync statistics
     */
    private suspend fun performSync(remoteCodes: List<ShiftCode>): SyncResult {
        val localCodes = localRepository.getAllActiveCodesSync()
        
        // First, handle duplicates by keeping only the most recent one per code
        val deduplicatedCodes = deduplicateCodes(localCodes)
        if (deduplicatedCodes.size < localCodes.size) {
            val duplicatesRemoved = localCodes.size - deduplicatedCodes.size
            Log.d(TAG, "Removed $duplicatesRemoved duplicate codes during sync")
        }
        
        val localCodeMap = deduplicatedCodes.associateBy { it.code }
        
        var codesAdded = 0
        var codesUpdated = 0
        var codesDeleted = 0
        
        // Process remote codes
        remoteCodes.forEach { remoteCode ->
            val existingEntity = localCodeMap[remoteCode.code]
            
            if (existingEntity == null) {
                // New code - insert
                val newEntity = remoteCode.toEntity()
                localRepository.insertOrUpdate(newEntity)
                codesAdded++
                Log.d(TAG, "Added new code: ${remoteCode.code}")
            } else {
                // Existing code - check if it needs updating
                if (existingEntity.hasChanged(remoteCode)) {
                    val updatedEntity = existingEntity.updateFromRemote(remoteCode)
                    localRepository.insertOrUpdate(updatedEntity)
                    codesUpdated++
                    Log.d(TAG, "Updated code: ${remoteCode.code}")
                }
                
                // Restore if it was soft-deleted
                if (existingEntity.isDeleted) {
                    localRepository.restoreDeleted(remoteCode.code)
                    Log.d(TAG, "Restored deleted code: ${remoteCode.code}")
                }
            }
        }
        
        // Soft delete codes that are no longer in remote data
        val remoteCodeStrings = remoteCodes.map { it.code }.toSet()
        val codesToDelete = deduplicatedCodes
            .filter { !remoteCodeStrings.contains(it.code) && !it.isDeleted }
            .map { it.code }
        
        if (codesToDelete.isNotEmpty()) {
            localRepository.softDeleteMultiple(codesToDelete)
            codesDeleted = codesToDelete.size
            Log.d(TAG, "Soft deleted ${codesToDelete.size} codes no longer in remote data")
        }
        
        return SyncResult(
            isSuccess = true,
            codesAdded = codesAdded,
            codesUpdated = codesUpdated,
            codesDeleted = codesDeleted
        )
    }

    /**
     * Removes duplicate codes by keeping only the most recent one per code.
     * When duplicates exist, keeps the one with the highest lastUpdated timestamp.
     * If timestamps are equal, keeps the one with the highest ID.
     * Soft-deletes the duplicates.
     * 
     * @param codes List of codes that may contain duplicates
     * @return Deduplicated list with only one entity per code
     */
    private suspend fun deduplicateCodes(codes: List<ShiftCodeEntity>): List<ShiftCodeEntity> {
        // Group by code to find duplicates
        val codesByCode = codes.groupBy { it.code }
        
        val idsToDelete = mutableListOf<Long>()
        val codesToKeep = mutableListOf<ShiftCodeEntity>()
        
        codesByCode.forEach { (code, entities) ->
            if (entities.size > 1) {
                // Find the entity to keep (most recent, or highest ID if timestamps equal)
                val entityToKeep = entities.maxWithOrNull(
                    compareBy<ShiftCodeEntity> { it.lastUpdated }
                        .thenBy { it.id }
                ) ?: entities.first()
                
                // Mark duplicates for deletion by ID
                val duplicates = entities.filter { it.id != entityToKeep.id }
                idsToDelete.addAll(duplicates.map { it.id })
                codesToKeep.add(entityToKeep)
                
                Log.d(TAG, "Found ${entities.size} duplicates for code $code, keeping entity with id ${entityToKeep.id}, deleting ${duplicates.size} duplicates")
            } else {
                // No duplicates, keep as is
                codesToKeep.add(entities.first())
            }
        }
        
        // Soft delete duplicates by ID (more precise than by code)
        if (idsToDelete.isNotEmpty()) {
            localRepository.softDeleteByIds(idsToDelete)
            Log.d(TAG, "Soft deleted ${idsToDelete.size} duplicate codes by ID")
        }
        
        return codesToKeep
    }

    /**
     * Gets filtered codes from the local database.
     * @param filterType The filter type to apply
     * @param gameFilter The game filter type to apply
     * @param rewardFilter The reward filter type to apply
     * @return Flow of filtered ShiftCodeEntity objects
     */
    fun getFilteredCodes(filterType: FilterType, gameFilter: GameFilterType, rewardFilter: RewardFilterType) = 
        localRepository.getFilteredCodes(filterType, gameFilter, rewardFilter)

    /**
     * Updates the redemption status of a specific code.
     * @param code The SHiFT code string
     * @param isRedeemed The new redemption status
     * @return true if the update was successful, false otherwise
     */
    suspend fun updateRedemptionStatus(code: String, isRedeemed: Boolean) = 
        localRepository.updateRedemptionStatus(code, isRedeemed)

    /**
     * Gets all active codes that are not expired and not non-expiring.
     * @return List of active ShiftCodeEntity objects
     */
    suspend fun getActiveCodesSync() = localRepository.getAllActiveCodesSync()
        .filter { !it.isExpired() && !it.isNonExpiring() }
}