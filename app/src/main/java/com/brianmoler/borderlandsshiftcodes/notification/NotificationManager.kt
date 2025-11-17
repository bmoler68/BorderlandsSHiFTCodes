package com.brianmoler.borderlandsshiftcodes.notification

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.brianmoler.borderlandsshiftcodes.MainActivity
import com.brianmoler.borderlandsshiftcodes.R
import com.brianmoler.borderlandsshiftcodes.data.ShiftCodeEntity

/**
 * Manages notifications for the Borderlands SHiFT Codes app.
 * 
 * This class handles:
 * - Creating notification channels for Android 8.0+
 * - Showing notifications for new active codes
 * - Managing notification permissions and display
 */
class NotificationManager(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID = "new_codes_channel"
        private const val CHANNEL_NAME = "New SHiFT Codes"
        private const val CHANNEL_DESCRIPTION = "Notifications for new active SHiFT codes"
        private const val NOTIFICATION_ID = 1001
    }
    
    private val notificationManager = NotificationManagerCompat.from(context)
    
    init {
        createNotificationChannel()
    }
    
    /**
     * Creates the notification channel for Android 8.0+ devices
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                setShowBadge(true)
            }
            
            val systemNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            systemNotificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Shows a notification for newly added active codes
     * @param newCodes List of newly added active codes
     */
    fun showNewCodesNotification(newCodes: List<ShiftCodeEntity>) {
        if (newCodes.isEmpty()) return
        
        val title = if (newCodes.size == 1) {
            "New SHiFT Code Available!"
        } else {
            "${newCodes.size} New SHiFT Codes Available!"
        }
        
        val contentText = if (newCodes.size == 1) {
            "Check out the new active code: ${newCodes.first().getSanitizedCode()}"
        } else {
            "Check out ${newCodes.size} new active codes in the app!"
        }
        
        val bigText = buildString {
            append("New active SHiFT codes:\n\n")
            newCodes.take(5).forEach { code ->
                append("• ${code.getSanitizedCode()}\n")
                append("  ${code.getSanitizedReward()}\n\n")
            }
            if (newCodes.size > 5) {
                append("... and ${newCodes.size - 5} more!")
            }
        }
        
        // Create intent to open the app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .build()
        
        // Show the notification
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }
    
}
