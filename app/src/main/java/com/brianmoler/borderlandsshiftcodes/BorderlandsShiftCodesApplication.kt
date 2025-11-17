package com.brianmoler.borderlandsshiftcodes

import android.app.Application
import android.util.Log
import com.brianmoler.borderlandsshiftcodes.config.AppConfig
import com.brianmoler.borderlandsshiftcodes.work.SyncScheduler

private const val TAG = "BorderlandsShiftCodesApp"

/**
 * Application class for the Borderlands SHiFT Codes app.
 * 
 * This class handles:
 * - Loading secrets configuration from assets
 * - Initializing WorkManager for background sync
 * - Starting periodic sync scheduling
 * - Application-level setup and configuration
 */
class BorderlandsShiftCodesApplication : Application() {
    
    private lateinit var syncScheduler: SyncScheduler
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application onCreate")
        
        // Load secrets configuration
        AppConfig.loadSecrets(this)
        
        // Initialize sync scheduler
        syncScheduler = SyncScheduler(this)
        
        // Schedule periodic sync
        syncScheduler.schedulePeriodicSync()
        
        Log.d(TAG, "Application initialization complete")
    }
    
}
