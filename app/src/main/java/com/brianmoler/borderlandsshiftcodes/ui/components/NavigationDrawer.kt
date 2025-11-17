package com.brianmoler.borderlandsshiftcodes.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brianmoler.borderlandsshiftcodes.ui.constants.UiConstants
import com.brianmoler.borderlandsshiftcodes.config.AppConfig
import com.brianmoler.borderlandsshiftcodes.ui.theme.ThemeMode
import androidx.compose.ui.unit.Dp
import androidx.core.net.toUri

/**
 * Navigation drawer for the SHiFT codes app with responsive design and scrolling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawer(
    onDismiss: () -> Unit,
    availableHeight: Dp,
    currentThemeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onShowThemeDialog: () -> Unit,
    isCompactView: Boolean = false,
    onCompactViewChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    
    // Responsive sizing based on screen dimensions
    val isCompact = screenWidthDp < 600
    val isMedium = screenWidthDp >= 600 && screenWidthDp < 840
    val isExpanded = screenWidthDp >= 840
    
    // Dynamic width based on screen size
    val drawerWidth = when {
        isExpanded -> 400.dp
        isMedium -> 360.dp
        else -> 280.dp  // Smaller for compact screens
    }
    
    // Use the provided available height directly
    val drawerHeight = availableHeight
    
    Box(
        modifier = modifier
            .width(drawerWidth)
            .height(drawerHeight)
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                top = 0.dp, // Padding is handled by Scaffold's padding
                start = 0.dp, // Padding is handled by Scaffold's padding
                end = 0.dp // Padding is handled by Scaffold's padding
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = when {
                    isExpanded -> UiConstants.Spacing.XL
                    isMedium -> UiConstants.Spacing.LARGE
                    else -> UiConstants.Spacing.MEDIUM
                },
                end = when {
                    isExpanded -> UiConstants.Spacing.XL
                    isMedium -> UiConstants.Spacing.LARGE
                    else -> UiConstants.Spacing.MEDIUM
                },
                top = when {
                    isExpanded -> UiConstants.Spacing.XL
                    isMedium -> UiConstants.Spacing.LARGE
                    else -> UiConstants.Spacing.MEDIUM
                },
                bottom = when {
                    isExpanded -> UiConstants.Spacing.XL
                    isMedium -> UiConstants.Spacing.LARGE
                    else -> UiConstants.Spacing.MEDIUM
                }
            ),
            verticalArrangement = Arrangement.spacedBy(
                when {
                    isExpanded -> UiConstants.Spacing.MEDIUM
                    else -> UiConstants.Spacing.SMALL
                }
            )
        ) {
            // Drawer Header
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(
                        when {
                            isExpanded -> UiConstants.Spacing.MEDIUM
                            else -> UiConstants.Spacing.SMALL
                        }
                    )
                ) {
                    Text(
                        text = "Borderlands SHiFT Codes",
                        style = when {
                            isExpanded -> MaterialTheme.typography.headlineMedium
                            isMedium -> MaterialTheme.typography.headlineSmall
                            else -> MaterialTheme.typography.titleLarge
                        },
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Show version info based on screen size
                    if (isExpanded) {
                        Text(
                            text = "Version ${AppConfig.App.getVersionName(context)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Unofficial fan project",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "v${AppConfig.App.getVersionName(context)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Divider
            item {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
            
            // Redeem Code - First Navigation Item
            item {
                NavigationDrawerItem(
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Redeem,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    label = { 
                        Text(
                            text = "Redeem Code",
                            style = when {
                                isExpanded -> MaterialTheme.typography.titleMedium
                                isMedium -> MaterialTheme.typography.bodyLarge
                                else -> MaterialTheme.typography.bodyMedium
                            }
                        )
                    },
                    selected = false,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW,
                            AppConfig.App.SHIFT_WEBSITE_URL.toUri())
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
            
            // Navigation Items
            item {
                NavigationDrawerItem(
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    label = { 
                        Text(
                            text = "Privacy Policy",
                            style = when {
                                isExpanded -> MaterialTheme.typography.titleMedium
                                isMedium -> MaterialTheme.typography.bodyLarge
                                else -> MaterialTheme.typography.bodyMedium
                            }
                        )
                    },
                    selected = false,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW,
                            AppConfig.App.PRIVACY_POLICY_URL.toUri())
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
            
            item {
                NavigationDrawerItem(
                    icon = { 
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    label = { 
                        Text(
                            text = "About",
                            style = when {
                                isExpanded -> MaterialTheme.typography.titleMedium
                                isMedium -> MaterialTheme.typography.bodyLarge
                                else -> MaterialTheme.typography.bodyMedium
                            }
                        )
                    },
                    selected = false,
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            AppConfig.App.ABOUT_PAGE_URL.toUri())
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
            
            // Theme Selection - After About
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(
                        when {
                            isExpanded -> UiConstants.Spacing.MEDIUM
                            else -> UiConstants.Spacing.SMALL
                        }
                    )
                ) {
                    Text(
                        text = "Theme",
                        style = when {
                            isExpanded -> MaterialTheme.typography.titleMedium
                            isMedium -> MaterialTheme.typography.bodyLarge
                            else -> MaterialTheme.typography.bodyMedium
                        },
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                            horizontal = when {
                                isExpanded -> UiConstants.Spacing.LARGE
                                isMedium -> UiConstants.Spacing.MEDIUM
                                else -> UiConstants.Spacing.SMALL
                            }
                        )
                    )
                    
                    // Theme Selection Icons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = when {
                                    isExpanded -> UiConstants.Spacing.LARGE
                                    isMedium -> UiConstants.Spacing.MEDIUM
                                    else -> UiConstants.Spacing.SMALL
                                }
                            ),
                        horizontalArrangement = Arrangement.spacedBy(
                            when {
                                isExpanded -> UiConstants.Spacing.MEDIUM
                                else -> UiConstants.Spacing.SMALL
                            }
                        )
                    ) {
                        // System Theme
                        ThemeIconButton(
                            icon = Icons.Default.Settings,
                            label = "System",
                            isSelected = currentThemeMode == ThemeMode.SYSTEM,
                            onClick = { onThemeModeChange(ThemeMode.SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Light Theme
                        ThemeIconButton(
                            icon = Icons.Default.LightMode,
                            label = "Light",
                            isSelected = currentThemeMode == ThemeMode.LIGHT,
                            onClick = { onThemeModeChange(ThemeMode.LIGHT) },
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Dark Theme
                        ThemeIconButton(
                            icon = Icons.Default.DarkMode,
                            label = "Dark",
                            isSelected = currentThemeMode == ThemeMode.DARK,
                            onClick = { onThemeModeChange(ThemeMode.DARK) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // Compact View Toggle - After Theme Selection
            item {
                if (onCompactViewChange != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = when {
                                    isExpanded -> UiConstants.Spacing.LARGE
                                    isMedium -> UiConstants.Spacing.MEDIUM
                                    else -> UiConstants.Spacing.SMALL
                                },
                                vertical = when {
                                    isExpanded -> UiConstants.Spacing.MEDIUM
                                    else -> UiConstants.Spacing.SMALL
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                when {
                                    isExpanded -> UiConstants.Spacing.MEDIUM
                                    else -> UiConstants.Spacing.SMALL
                                }
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ViewAgenda,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(
                                    when {
                                        isExpanded -> 24.dp
                                        isMedium -> 20.dp
                                        else -> 18.dp
                                    }
                                )
                            )
                            Text(
                                text = "Compact View",
                                style = when {
                                    isExpanded -> MaterialTheme.typography.titleMedium
                                    isMedium -> MaterialTheme.typography.bodyLarge
                                    else -> MaterialTheme.typography.bodyMedium
                                },
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        Switch(
                            checked = isCompactView,
                            onCheckedChange = { onCompactViewChange(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }
            
            // Footer
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(
                        when {
                            isExpanded -> UiConstants.Spacing.MEDIUM
                            else -> UiConstants.Spacing.SMALL
                        }
                    )
                ) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    
                    // Disclaimer text
                    Text(
                        text = "Unofficial Fan Project",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Not affiliated with Gearbox or 2K Games",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Always show expanded license view
                    Text(
                        text = "Copyright (c) ${AppConfig.App.COPYRIGHT_YEAR} ${AppConfig.App.COPYRIGHT_HOLDER}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Licensed under the ${AppConfig.App.LICENSE}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Individual theme selection button with icon and label
 */
@Composable
private fun ThemeIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        androidx.compose.ui.graphics.Color.Transparent
                    },
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) {
                    androidx.compose.ui.graphics.Color.White
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.size(24.dp)
            )
        }
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) {
                // Use white on dark backgrounds, black on light backgrounds
                if (MaterialTheme.colorScheme.background == androidx.compose.ui.graphics.Color(0xFF0F172A) || 
                    MaterialTheme.colorScheme.background == androidx.compose.ui.graphics.Color(0xFF1E293B)) {
                    androidx.compose.ui.graphics.Color.White // White text on dark background
                } else {
                    androidx.compose.ui.graphics.Color.Black // Black text on light background
                }
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
} 