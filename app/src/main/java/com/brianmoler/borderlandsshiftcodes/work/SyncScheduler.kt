package com.brianmoler.borderlandsshiftcodes.work

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

private const val TAG = "SyncScheduler"
private const val WORK_NAME = "sync_shift_codes"

/**
 * Manages scheduling of periodic sync work using WorkManager.
 * 
 * This class handles:
 * - Scheduling periodic background sync
 * - Canceling existing sync work
 * - Managing sync constraints (network, battery optimization)
 */
class SyncScheduler(private val context: Context) {
    
    private val workManager = WorkManager.getInstance(context)
    
    /**
     * Schedules periodic sync work with appropriate constraints.
     * The sync will run every 4 hours when the device is connected to network
     * and has sufficient battery.
     */
    fun schedulePeriodicSync() {
        Log.d(TAG, "Scheduling periodic sync work")
        
        // Define constraints for the sync work
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        // Create periodic work request
        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            4, TimeUnit.HOURS, // Repeat every 4 hours
            1, TimeUnit.HOURS  // Flex interval of 1 hour
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag(WORK_NAME)
            .build()
        
        // Enqueue the work with unique work name
        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
        
        Log.d(TAG, "Periodic sync work scheduled successfully")
    }
}
