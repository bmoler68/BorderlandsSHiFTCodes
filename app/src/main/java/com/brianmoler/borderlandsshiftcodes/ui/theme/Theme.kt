package com.brianmoler.borderlandsshiftcodes.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Dark color scheme for the Borderlands SHiFT Codes application
 * 
 * This scheme provides a professional dark theme that maintains the Borderlands aesthetic
 * while ensuring excellent readability and reducing eye strain in low-light conditions.
 * 
 * The dark theme uses:
 * - Dark backgrounds (0F172A, 1E293B) for reduced eye strain
 * - Light text (F8FAFC) for optimal contrast
 * - Professional accent colors that maintain brand identity
 * - Appropriate surface variants for visual hierarchy
 */
private val DarkColorScheme = darkColorScheme(
    primary = BorderlandsPrimary,
    secondary = BorderlandsSecondary,
    tertiary = BorderlandsTertiary,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFF334155),
    error = BorderlandsError,
    onPrimary = BorderlandsOnPrimary,
    onSecondary = BorderlandsOnSecondary,
    onTertiary = BorderlandsOnTertiary,
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFFCBD5E1),
    onError = BorderlandsOnError,
    outline = BorderlandsOutline,
    outlineVariant = BorderlandsOutlineVariant
)

/**
 * Light color scheme for the Borderlands SHiFT Codes application
 * 
 * This scheme provides a clean, professional light theme that emphasizes readability
 * and maintains the Borderlands brand identity in well-lit conditions.
 * 
 * The light theme uses:
 * - Light backgrounds (F8FAFC, FFFFFF) for clean appearance
 * - Dark text (0F172A) for optimal contrast
 * - Professional accent colors that stand out appropriately
 * - Subtle surface variants for visual hierarchy
 */
private val LightColorScheme = lightColorScheme(
    primary = BorderlandsPrimary,
    secondary = BorderlandsSecondary,
    tertiary = BorderlandsTertiary,
    background = BorderlandsBackground,
    surface = BorderlandsSurface,
    surfaceVariant = BorderlandsSurfaceVariant,
    error = BorderlandsError,
    onPrimary = BorderlandsOnPrimary,
    onSecondary = BorderlandsOnSecondary,
    onTertiary = BorderlandsOnTertiary,
    onBackground = BorderlandsOnBackground,
    onSurface = BorderlandsOnSurface,
    onSurfaceVariant = Color(0xFF475569),
    onError = BorderlandsOnError,
    outline = BorderlandsOutline,
    outlineVariant = BorderlandsOutlineVariant
)

/**
 * Main theme composable for the Borderlands SHiFT Codes application
 * 
 * This theme provides consistent styling across the entire application, including:
 * - Color schemes (light/dark) with professional Borderlands branding
 * - Typography system optimized for readability
 * - Dynamic color support for Android 12+ (disabled for brand consistency)
 * - Automatic status bar theming
 * - Window insets handling for edge-to-edge display
 * - User theme preference override support
 * 
 * @param userThemeMode User's theme preference (null means use system preference)
 * @param dynamicColor Whether to enable dynamic colors on Android 12+ (disabled for brand consistency)
 * @param content The composable content to apply the theme to
 */
@Composable
fun BorderlandsSHiFTCodesTheme(
    userThemeMode: ThemeMode? = null,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled for consistent professional appearance
    content: @Composable () -> Unit
) {
    // Determine the effective dark theme based on user preference and system setting
    val systemInDarkTheme = isSystemInDarkTheme()
    val darkTheme = when (userThemeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM, null -> systemInDarkTheme
    }
    // Determine the appropriate color scheme based on theme and device capabilities
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        // Apply theme to the system UI (status bar, navigation bar)
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar color to match the primary theme color
            window.statusBarColor = colorScheme.primary.toArgb()
            // Configure status bar appearance (light/dark icons) based on theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Apply the Material 3 theme with our custom color scheme and typography
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}