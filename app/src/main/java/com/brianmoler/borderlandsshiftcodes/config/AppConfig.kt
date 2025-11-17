package com.brianmoler.borderlandsshiftcodes.config

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import java.util.Properties

private const val TAG = "AppConfig"
private const val SECRETS_FILE = "secrets.properties"

/**
 * Application configuration constants
 */
object AppConfig {
    
    // Lazy-loaded secrets properties
    private var secretsProperties: Properties? = null
    
    /**
     * Loads secrets from the assets/secrets.properties file.
     * This must be called during app initialization.
     * 
     * @param context The application context for accessing assets
     * @throws IllegalStateException if the secrets file cannot be loaded
     */
    fun loadSecrets(context: Context) {
        try {
            val properties = Properties()
            context.assets.open(SECRETS_FILE).use { inputStream ->
                properties.load(inputStream)
            }
            secretsProperties = properties
            Log.d(TAG, "Secrets loaded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load secrets.properties", e)
            throw IllegalStateException("Failed to load secrets.properties file. Please ensure the file exists in app/src/main/assets/", e)
        }
    }
    
    /**
     * Gets a property value from the secrets file.
     * 
     * @param key The property key
     * @return The property value
     * @throws IllegalStateException if secrets are not loaded or the key is not found
     */
    private fun getSecret(key: String): String {
        val properties = secretsProperties
            ?: throw IllegalStateException("Secrets not loaded. Call AppConfig.loadSecrets() first.")
        
        return properties.getProperty(key)
            ?: throw IllegalStateException("Required secret '$key' not found in secrets.properties")
    }
    
    /**
     * Network configuration
     */
    object Network {
        /**
         * Gets the CSV URL from secrets file.
         */
        val CSV_URL: String
            get() = AppConfig.getSecret("csv.url")
        
        /**
         * Gets the CSV fallback URL from secrets file.
         */
        val CSV_FALLBACK_URL: String
            get() = AppConfig.getSecret("csv.fallback.url")
        
        const val NETWORK_TIMEOUT_SECONDS = 30L
        
        /**
         * Gets the User-Agent string for network requests.
         * 
         * @param context The context for accessing package information
         * @return A formatted User-Agent string with app name and version
         */
        fun getUserAgent(context: Context): String {
            return "BorderlandsSHiFTCodes/${App.getVersionName(context)}"
        }
    }
    
    /**
     * Data validation
     */
    object Validation {
        const val MAX_CODE_LENGTH = 29
        const val MAX_REWARD_LENGTH = 200
    }
    
    /**
     * App information and external links
     */
    object App {
        /**
         * SHiFT website URL (public URL, not a secret)
         */
        const val SHIFT_WEBSITE_URL = "https://shift.gearboxsoftware.com"
        
        /**
         * Gets the privacy policy URL from secrets file.
         */
        val PRIVACY_POLICY_URL: String
            get() = AppConfig.getSecret("privacy.policy.url")
        
        /**
         * Gets the about page URL from secrets file.
         */
        val ABOUT_PAGE_URL: String
            get() = AppConfig.getSecret("about.page.url")
        
        const val COPYRIGHT_YEAR = "2025"
        const val COPYRIGHT_HOLDER = "Brian Moler"
        const val LICENSE = "MIT License"
        
        // ==================== VERSION INFORMATION ====================
        
        /**
         * Gets the formatted version name for display.
         * 
         * @param context The context for accessing package information
         * @return A formatted string containing the version name
         */
        fun getVersionName(context: Context): String {
            return try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                packageInfo.versionName ?: "Unknown"
            } catch (e: PackageManager.NameNotFoundException) {
                "Unknown"
            }
        }
        
        /**
         * Gets the version code of the application.
         * 
         * Handles both modern (Android 9+) and legacy version code formats.
         * Returns a default value if the package information cannot be retrieved.
         * 
         * @param context The context for accessing package information
         * @return The version code as an integer
         */
        fun getVersionCode(context: Context): Int {
            return try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode.toInt()
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode
                }
            } catch (e: PackageManager.NameNotFoundException) {
                10000
            }
        }
        
        /**
         * Gets the complete version information including copyright and license.
         * 
         * @param context The context for accessing package information
         * @return A formatted string with version, copyright, and license
         */
        fun getFullVersionInfo(context: Context): String {
            return "v${getVersionName(context)}\nCopyright (c) $COPYRIGHT_YEAR $COPYRIGHT_HOLDER\nLicensed under the $LICENSE"
        }
    }
    
}