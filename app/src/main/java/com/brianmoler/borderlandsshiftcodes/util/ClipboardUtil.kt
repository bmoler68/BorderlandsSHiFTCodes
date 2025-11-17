package com.brianmoler.borderlandsshiftcodes.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

/**
 * Utility class for clipboard operations
 */
object ClipboardUtil {
    
    /**
     * Copies text to clipboard and shows a toast message
     * @param context The application context
     * @param text The text to copy
     * @param label The label for the clipboard data
     * @param toastMessage The message to show in the toast
     */
    fun copyToClipboard(
        context: Context,
        text: String,
        label: String = "Copied Text",
        toastMessage: String = "Copied to clipboard!"
    ) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to copy to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
} 