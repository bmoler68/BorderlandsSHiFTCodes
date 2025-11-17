package com.brianmoler.borderlandsshiftcodes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.brianmoler.borderlandsshiftcodes.ui.ShiftCodeScreen
import com.brianmoler.borderlandsshiftcodes.ui.ShiftCodeViewModel
import com.brianmoler.borderlandsshiftcodes.ui.theme.BorderlandsSHiFTCodesTheme

/**
 * Main activity for the Borderlands SHiFT Codes application.
 * 
 * This activity serves as the entry point for the app and displays
 * the main screen containing SHiFT codes for Borderlands games.
 */
class MainActivity : ComponentActivity() {
    
    /**
     * ViewModel instance for managing SHiFT codes data and UI state.
     * Uses the `by viewModels()` delegate for automatic lifecycle management.
     */
    private val viewModel: ShiftCodeViewModel by viewModels()
    
    /**
     * Activity result launcher for notification permission request
     */
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Permission result is handled automatically by the notification system
        // No additional action needed here
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display for modern Android devices
        enableEdgeToEdge()
        
        // Request notification permission for Android 13+ devices
        requestNotificationPermission()
        
        // Initialize preferences
        viewModel.initializePreferences(this)
        
        // Set up the Compose UI
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            BorderlandsSHiFTCodesTheme(userThemeMode = uiState.themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShiftCodeScreen(viewModel = viewModel)
                }
            }
        }
    }
    
    /**
     * Requests notification permission for Android 13+ devices
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                else -> {
                    // Request permission
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}