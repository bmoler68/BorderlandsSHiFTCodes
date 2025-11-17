package com.brianmoler.borderlandsshiftcodes.ui.constants

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Professional UI constants for consistent styling and spacing
 */
object UiConstants {
    
    // Professional spacing system
    object Spacing {
        val XS = 4.dp
        val SMALL = 8.dp
        val MEDIUM = 16.dp
        val LARGE = 24.dp
        val XL = 32.dp
        val XXL = 48.dp
    }
    
    // Professional typography scale
    object Typography {
        val CAPTION = 10.sp
        val SMALL_TEXT = 12.sp
        val MEDIUM_TEXT = 14.sp
    }
    
    // Professional corner radius system
    object CornerRadius {
        val SMALL = 8.dp
        val MEDIUM = 12.dp
        val LARGE = 16.dp
    }
    
    // Professional game colors
    object GameColors {
        val BORDERLANDS_1 = Color(0xFF1E40AF) // Deep professional blue
        val BORDERLANDS_TPS = Color(0xFF7C3AED) // Rich purple
        val BORDERLANDS_2 = Color(0xFFEA580C) // Vibrant orange
        val BORDERLANDS_3 = Color(0xFF16A34A) // Fresh green
        val BORDERLANDS_4 = Color(0xFF92400E) // Warm brown
        val WONDERLANDS = Color(0xFFBE185D) // Rich pink
    }
    
    // Professional status colors
    object StatusColors {
        val ACTIVE = Color(0xFFDCFCE7) // Light professional green
        val EXPIRED = Color(0xFFFEE2E2) // Light professional red
        val NON_EXPIRING = Color(0xFFDBEAFE) // Light professional blue
    }
    
    // Professional redemption status colors
    object RedemptionColors {
        val REDEEMED_BACKGROUND = Color(0xFFDCFCE7) // Light professional green
        val REDEEMED_TEXT = Color(0xFF059669) // Professional green
        val NOT_REDEEMED_BACKGROUND = Color(0xFFFEF3C7) // Light professional amber
        val NOT_REDEEMED_TEXT = Color(0xFFD97706) // Professional amber
    }
    
    // Professional elevation system
    object Elevation {
        val XS = 2.dp
        val SMALL = 4.dp
        val MEDIUM = 8.dp
        val LARGE = 12.dp
    }
    
    // Professional gradients - These should be replaced with theme-aware functions
    // TODO: Remove these hardcoded gradients and use MaterialTheme colors instead
    object Gradients {
        val SURFACE_START = Color(0xFFFFFFFF)
        val SURFACE_END = Color(0xFFF8FAFC)
        val CARD_START = Color(0xFFFFFFFF)
        val CARD_END = Color(0xFFF1F5F9)
    }
} 