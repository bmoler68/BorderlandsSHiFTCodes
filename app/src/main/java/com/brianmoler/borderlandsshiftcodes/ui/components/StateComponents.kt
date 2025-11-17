package com.brianmoler.borderlandsshiftcodes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brianmoler.borderlandsshiftcodes.ui.constants.UiConstants

/**
 * Professional loading state composable
 */
@Composable
fun LoadingState(screenSize: ScreenSize) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(UiConstants.CornerRadius.LARGE),
            elevation = CardDefaults.cardElevation(
                defaultElevation = UiConstants.Elevation.MEDIUM
            )
        ) {
            Column(
                modifier = Modifier.padding(UiConstants.Spacing.XL),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(if (screenSize.isExpanded) UiConstants.Spacing.XXL else UiConstants.Spacing.XL)
                )
                Spacer(modifier = Modifier.height(UiConstants.Spacing.MEDIUM))
                Text(
                    text = "Loading SHiFT Codes...",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(UiConstants.Spacing.SMALL))
                Text(
                    text = "Please wait while we fetch the latest codes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Professional error state composable
 */
@Composable
fun ErrorState(
    error: String,
    screenSize: ScreenSize,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (screenSize.isExpanded) UiConstants.Spacing.XL else UiConstants.Spacing.LARGE),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(UiConstants.CornerRadius.LARGE),
            elevation = CardDefaults.cardElevation(
                defaultElevation = UiConstants.Elevation.MEDIUM
            )
        ) {
            Column(
                modifier = Modifier.padding(UiConstants.Spacing.XL),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(if (screenSize.isExpanded) 80.dp else 64.dp)
                        .background(
                            color = MaterialTheme.colorScheme.error,
                            shape = RoundedCornerShape(if (screenSize.isExpanded) 40.dp else 32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚠️",
                        fontSize = if (screenSize.isExpanded) 32.sp else 28.sp
                    )
                }
                Spacer(modifier = Modifier.height(UiConstants.Spacing.MEDIUM))
                Text(
                    text = "Error loading data",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(UiConstants.Spacing.SMALL))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(UiConstants.Spacing.LARGE))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(UiConstants.CornerRadius.MEDIUM)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(UiConstants.Spacing.SMALL))
                    Text("Retry")
                }
            }
        }
    }
}

/**
 * Professional empty state composable
 */
@Composable
fun EmptyState(screenSize: ScreenSize) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(UiConstants.CornerRadius.LARGE),
            elevation = CardDefaults.cardElevation(
                defaultElevation = UiConstants.Elevation.MEDIUM
            )
        ) {
            Column(
                modifier = Modifier.padding(UiConstants.Spacing.XL),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(if (screenSize.isExpanded) 80.dp else 64.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(if (screenSize.isExpanded) 40.dp else 32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎮",
                        fontSize = if (screenSize.isExpanded) 32.sp else 28.sp
                    )
                }
                Spacer(modifier = Modifier.height(UiConstants.Spacing.MEDIUM))
                Text(
                    text = "No SHiFT codes available",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(UiConstants.Spacing.SMALL))
                Text(
                    text = "Try refreshing to load the latest codes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
} 