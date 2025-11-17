package com.brianmoler.borderlandsshiftcodes.sync

import android.content.Context
import android.util.Log
import com.brianmoler.borderlandsshiftcodes.data.*
import com.brianmoler.borderlandsshiftcodes.notification.NotificationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "SyncService"

/**
 * Centralized sync service that handles data synchronization and notifications.
 * 
 * This service provides a single implementation of sync logic that can be used by:
 * - ViewModel for UI-triggered syncs
 * - WorkManager for background syncs
 * - Manual sync triggers
 */
class SyncService(private val context: Context) {
    
    private val remoteRepository: RemoteShiftCodeRepository by lazy {
        RemoteShiftCodeRepository(context)
    }
    
    private val localRepository: LocalShiftCodeRepository by lazy {
        val database = ShiftCodeDatabase.getDatabase(context)
        LocalShiftCodeRepository(database.shiftCodeDao())
    }
    
    private val repository: ShiftCodeRepository by lazy {
        ShiftCodeRepository(remoteRepository, localRepository)
    }
    
    private val notificationManager: NotificationManager by lazy {
        NotificationManager(context)
    }
    
    /**
     * Performs sync with remote data and sends notifications for new codes.
     * 
     * @return SyncResult indicating the outcome of the sync operation
     */
    suspend fun syncWithNotifications(): SyncResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting sync with notifications")
            
            // Get existing active codes before sync for notification comparison
            val existingActiveCodes = repository.getActiveCodesSync()
                .map { it.code }
                .toSet()
            
            Log.d(TAG, "Found ${existingActiveCodes.size} existing active codes")
            
            // Perform sync with remote data
            val syncResult = repository.syncWithRemoteData()
            
            if (syncResult.isSuccess) {
                Log.d(TAG, "Sync successful: ${syncResult.codesAdded} added, ${syncResult.codesUpdated} updated, ${syncResult.codesDeleted} deleted")
                
                // Check for new active codes and send notifications
                if (syncResult.codesAdded > 0) {
                    val newActiveCodes = repository.getActiveCodesSync()
                        .filter { !existingActiveCodes.contains(it.code) }
                    
                    if (newActiveCodes.isNotEmpty()) {
                        Log.d(TAG, "Found ${newActiveCodes.size} new active codes, sending notification")
                        notificationManager.showNewCodesNotification(newActiveCodes)
                    }
                }
                
                syncResult
            } else {
                Log.e(TAG, "Sync failed: ${syncResult.error}")
                syncResult
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sync service failed with exception", e)
            SyncResult(
                isSuccess = false,
                error = e.message ?: "Unknown sync error"
            )
        }
    }
}
