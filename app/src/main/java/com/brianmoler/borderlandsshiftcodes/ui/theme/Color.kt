package com.brianmoler.borderlandsshiftcodes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Professional color scheme for Borderlands theme
 * 
 * This file defines the complete color palette used throughout the application,
 * ensuring consistency and maintaining the professional Borderlands aesthetic.
 */

// Primary brand colors
/** Deep professional blue - used for primary actions, headers, and key UI elements */
val BorderlandsPrimary = Color(0xFF1E3A8A)
/** Sophisticated slate - used for secondary actions and supporting UI elements */
val BorderlandsSecondary = Color(0xFF475569)
/** Professional red - used for destructive actions and error states */
val BorderlandsTertiary = Color(0xFFDC2626)

// Background and surface colors
/** Clean off-white - used for the main application background */
val BorderlandsBackground = Color(0xFFF8FAFC)
/** Pure white - used for card surfaces and elevated UI elements */
val BorderlandsSurface = Color(0xFFFFFFFF)

// Error and status colors
/** Professional error red - used for error messages and critical states */
val BorderlandsError = Color(0xFFDC2626)

// Text and content colors
/** White text - used on primary, secondary, and tertiary colored backgrounds */
val BorderlandsOnPrimary = Color(0xFFFFFFFF)
/** White text - used on secondary colored backgrounds */
val BorderlandsOnSecondary = Color(0xFFFFFFFF)
/** White text - used on tertiary colored backgrounds */
val BorderlandsOnTertiary = Color(0xFFFFFFFF)
/** Dark text - used on light backgrounds for optimal readability */
val BorderlandsOnBackground = Color(0xFF0F172A)
/** Dark text - used on light surfaces for optimal readability */
val BorderlandsOnSurface = Color(0xFF0F172A)
/** White text - used on error colored backgrounds */
val BorderlandsOnError = Color(0xFFFFFFFF)

// Additional professional colors
/** Light surface variant - used for subtle surface differentiation */
val BorderlandsSurfaceVariant = Color(0xFFF1F5F9)
/** Subtle outline - used for borders and dividers */
val BorderlandsOutline = Color(0xFFCBD5E1)
/** Lighter outline variant - used for subtle borders and separators */
val BorderlandsOutlineVariant = Color(0xFFE2E8F0)

// Status colors with professional appearance
/** Professional green - used for active/valid states and success indicators */
val BorderlandsActiveColor = Color(0xFF059669)
/** Professional red - used for expired/invalid states and error indicators */
val BorderlandsExpiredColor = Color(0xFFDC2626)
/** Professional blue - used for non-expiring states and informational indicators */
val BorderlandsNonExpiringColor = Color(0xFF2563EB)

// Theme-aware status background colors - unified colors for both themes
/** Light theme active background - using dark mode colors for consistency */
val ActiveBackgroundLight = Color(0xFF0F5132)
/** Dark theme active background - brighter for better visibility */
val ActiveBackgroundDark = Color(0xFF0F5132)
/** Light theme expired background - using dark mode colors for consistency */
val ExpiredBackgroundLight = Color(0xFF991B1B)
/** Dark theme expired background - brighter for better visibility */
val ExpiredBackgroundDark = Color(0xFF991B1B)
/** Light theme non-expiring background - using dark mode colors for consistency */
val NonExpiringBackgroundLight = Color(0xFF1D4ED8)
/** Dark theme non-expiring background - brighter for better visibility */
val NonExpiringBackgroundDark = Color(0xFF1D4ED8)

// Theme-aware redemption colors - unified colors for both themes
/** Light theme redeemed background - using dark mode colors for consistency */
val RedeemedBackgroundLight = Color(0xFF0F5132)
/** Dark theme redeemed background - brighter for better visibility */
val RedeemedBackgroundDark = Color(0xFF0F5132)
/** Light theme not redeemed background - using dark mode colors for consistency */
val NotRedeemedBackgroundLight = Color(0xFF92400E)
/** Dark theme not redeemed background - brighter for better visibility */
val NotRedeemedBackgroundDark = Color(0xFF92400E)

// Theme-aware "ALL" filter colors - unified colors for both themes
/** Light theme ALL filter background - using consistent dark color */
val AllFilterBackgroundLight = Color(0xFF1E3A8A) // Using BorderlandsPrimary
/** Dark theme ALL filter background - using consistent dark color */
val AllFilterBackgroundDark = Color(0xFF1E3A8A) // Using BorderlandsPrimary

// Theme-aware text colors - unified white text for both themes
/** Light theme active text - white for consistency with dark mode */
val ActiveTextLight = Color(0xFFFFFFFF)
/** Dark theme active text - white for better contrast */
val ActiveTextDark = Color(0xFFFFFFFF)
/** Light theme expired text - white for consistency with dark mode */
val ExpiredTextLight = Color(0xFFFFFFFF)
/** Dark theme expired text - white for better contrast */
val ExpiredTextDark = Color(0xFFFFFFFF)
/** Light theme non-expiring text - white for consistency with dark mode */
val NonExpiringTextLight = Color(0xFFFFFFFF)
/** Dark theme non-expiring text - white for better contrast */
val NonExpiringTextDark = Color(0xFFFFFFFF)
/** Light theme redeemed text - white for consistency with dark mode */
val RedeemedTextLight = Color(0xFFFFFFFF)
/** Dark theme redeemed text - white for better contrast */
val RedeemedTextDark = Color(0xFFFFFFFF)
/** Light theme not redeemed text - white for consistency with dark mode */
val NotRedeemedTextLight = Color(0xFFFFFFFF)
/** Dark theme not redeemed text - white for better contrast */
val NotRedeemedTextDark = Color(0xFFFFFFFF)

/**
 * Theme-aware color functions for dynamic UI elements
 */

/**
 * Gets the appropriate active status background color based on current theme
 */
@Composable
fun getActiveBackgroundColor(): Color {
    // Check if we're in dark mode by comparing with our dark background color
    return if (MaterialTheme.colorScheme.background == Color(0xFF0F172A) || 
               MaterialTheme.colorScheme.background == Color(0xFF1E293B)) {
        ActiveBackgroundDark
    } else {
        ActiveBackgroundLight
    }
}

/**
 * Gets the appropriate expired status background color based on current theme
 */
@Composable
fun getExpiredBackgroundColor(): Color {
    // Check if we're in dark mode by comparing with our dark background color
    return if (MaterialTheme.colorScheme.background == Color(0xFF0F172A) || 
               MaterialTheme.colorScheme.background == Color(0xFF1E293B)) {
        ExpiredBackgroundDark
    } else {
        ExpiredBackgroundLight
    }
}

/**
 * Gets the appropriate non-expiring status background color based on current theme
 */
@Composable
fun getNonExpiringBackgroundColor(): Color {
    // Check if we're in dark mode by comparing with our dark background color
    return if (MaterialTheme.colorScheme.background == Color(0xFF0F172A) || 
               MaterialTheme.colorScheme.background == Color(0xFF1E293B)) {
        NonExpiringBackgroundDark
    } else {
        NonExpiringBackgroundLight
    }
}

/**
 * Gets the appropriate redeemed background color based on current theme
 */
@Composable
fun getRedeemedBackgroundColor(): Color {
    // Check if we're in dark mode by comparing with our dark background color
    return if (MaterialTheme.colorScheme.background == Color(0xFF0F172A) || 
               MaterialTheme.colorScheme.background == Color(0xFF1E293B)) {
        RedeemedBackgroundDark
    } else {
        RedeemedBackgroundLight
    }
}

/**
 * Gets the appropriate not redeemed background color based on current theme
 */
@Composable
fun getNotRedeemedBackgroundColor(): Color {
    // Check if we're in dark mode by comparing with our dark background color
    return if (MaterialTheme.colorScheme.background == Color(0xFF0F172A) || 
               MaterialTheme.colorScheme.background == Color(0xFF1E293B)) {
        NotRedeemedBackgroundDark
    } else {
        NotRedeemedBackgroundLight
    }
}

/**
 * Gets the appropriate active status text color based on current theme
 */
@Composable
fun getActiveTextColor(): Color {
    // Check if we're in dark mode by comparing with our dark background color
    return if (MaterialTheme.colorScheme.background == Color(0xFF0F172A) || 
               MaterialTheme.colorScheme.background == Color(0xFF1E293B)) {
        ActiveTextDark
    } else {
        ActiveTextLight
    }
}

/**
 * Gets the appropriate expired status text color based on current theme
 */
@Composable
fun getExpiredTextColor(): Color {
    // Check if we're in dark mode by comparing with our dark background color
    return if (MaterialTheme.colorScheme.background == Color(0xFF0F172A) || 
               MaterialTheme.colorScheme.background == Color(0xFF1E293B)) {
        ExpiredTextDark
    } else {
        ExpiredTextLight
    }
}

/**
 * Gets the appropriate non-expiring status text color based on current theme
 */
@Composable
fun getNonExpiringTextColor(): Color {
    // Check if we're in dark mode by comparing with our dark background color
    return if (MaterialTheme.colorScheme.background == Color(0xFF0F172A) || 
               MaterialTheme.colorScheme.background == Color(0xFF1E293B)) {
        NonExpiringTextDark
    } else {
        NonExpiringTextLight
    }
}

/**
 * Gets the appropriate redeemed text color based on current theme
 */
@Composable
fun getRedeemedTextColor(): Color {
    // Check if we're in dark mode by comparing with our dark background color
    return if (MaterialTheme.colorScheme.background == Color(0xFF0F172A) || 
               MaterialTheme.colorScheme.background == Color(0xFF1E293B)) {
        RedeemedTextDark
    } else {
        RedeemedTextLight
    }
}

/**
 * Gets the appropriate not redeemed text color based on current theme
 */
@Composable
fun getNotRedeemedTextColor(): Color {
    // Check if we're in dark mode by comparing with our dark background color
    return if (MaterialTheme.colorScheme.background == Color(0xFF0F172A) || 
               MaterialTheme.colorScheme.background == Color(0xFF1E293B)) {
        NotRedeemedTextDark
    } else {
        NotRedeemedTextLight
    }
}

/**
 * Gets the appropriate ALL filter background color based on current theme
 */
@Composable
fun getAllFilterBackgroundColor(): Color {
    // Check if we're in dark mode by comparing with our dark background color
    return if (MaterialTheme.colorScheme.background == Color(0xFF0F172A) || 
               MaterialTheme.colorScheme.background == Color(0xFF1E293B)) {
        AllFilterBackgroundDark
    } else {
        AllFilterBackgroundLight
    }
}
