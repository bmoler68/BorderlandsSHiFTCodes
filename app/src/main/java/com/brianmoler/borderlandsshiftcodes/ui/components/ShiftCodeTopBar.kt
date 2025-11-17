package com.brianmoler.borderlandsshiftcodes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brianmoler.borderlandsshiftcodes.ui.constants.UiConstants

/**
 * Enhanced top app bar for the SHiFT codes screen
 */
@Composable
fun ShiftCodeTopBar(
    screenSize: ScreenSize,
    onRefresh: () -> Unit,
    onMenuClick: () -> Unit,
    isSyncing: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(UiConstants.CornerRadius.LARGE),
        elevation = CardDefaults.cardElevation(
            defaultElevation = UiConstants.Elevation.MEDIUM
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (screenSize.isExpanded) UiConstants.Spacing.LARGE else UiConstants.Spacing.MEDIUM,
                    top = UiConstants.Spacing.SMALL,
                    end = if (screenSize.isExpanded) UiConstants.Spacing.LARGE else UiConstants.Spacing.MEDIUM,
                    bottom = UiConstants.Spacing.SMALL
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Hamburger menu button and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.MEDIUM)
            ) {
                // Professional hamburger menu button
                Card(
                    modifier = Modifier
                        .size(if (screenSize.isExpanded) 48.dp else 40.dp)
                        .clickable { onMenuClick() },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(UiConstants.CornerRadius.MEDIUM),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = UiConstants.Elevation.SMALL
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                
                // App title with professional styling
                Column {
                    Text(
                        text = "Borderlands SHiFT Codes",
                        style = if (screenSize.isExpanded) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isSyncing) "Syncing with latest codes..." else "Stay updated with the latest codes",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSyncing) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Right side: Professional refresh button with sync indicator
            Card(
                modifier = Modifier
                    .size(if (screenSize.isExpanded) 48.dp else 40.dp)
                    .clickable(enabled = !isSyncing) { onRefresh() },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSyncing) 
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    else 
                        MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(UiConstants.CornerRadius.MEDIUM),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = UiConstants.Elevation.SMALL
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(if (screenSize.isExpanded) 24.dp else 20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh, 
                            contentDescription = "Refresh SHiFT codes",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Screen size information for responsive design
 */
data class ScreenSize(
    val isExpanded: Boolean,
    val isTablet: Boolean
) 