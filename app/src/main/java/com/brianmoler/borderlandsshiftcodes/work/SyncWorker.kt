package com.brianmoler.borderlandsshiftcodes.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.brianmoler.borderlandsshiftcodes.sync.SyncService

private const val TAG = "SyncWorker"

/**
 * WorkManager worker that performs periodic synchronization with remote CSV data.
 * 
 * This worker delegates to SyncService to avoid code duplication with ViewModel sync.
 */
class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val syncService: SyncService by lazy {
        SyncService(context)
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting periodic sync work")
            
            val syncResult = syncService.syncWithNotifications()
            
            if (syncResult.isSuccess) {
                Log.d(TAG, "Sync successful: ${syncResult.codesAdded} added, ${syncResult.codesUpdated} updated, ${syncResult.codesDeleted} deleted")
                Result.success()
            } else {
                Log.e(TAG, "Sync failed: ${syncResult.error}")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sync work failed with exception", e)
            Result.failure()
        }
    }
}
