package com.brianmoler.borderlandsshiftcodes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brianmoler.borderlandsshiftcodes.ui.theme.ThemeMode

/**
 * Dialog for selecting theme mode with clear visual indicators and descriptions
 */
@Composable
fun ThemeToggleDialog(
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf(currentThemeMode) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Choose Theme",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // System Theme Option
                ThemeOption(
                    themeMode = ThemeMode.SYSTEM,
                    icon = Icons.Default.Settings,
                    title = "System Default",
                    description = "Follows your device's theme setting",
                    isSelected = selectedTheme == ThemeMode.SYSTEM,
                    onSelected = { selectedTheme = ThemeMode.SYSTEM }
                )
                
                // Light Theme Option
                ThemeOption(
                    themeMode = ThemeMode.LIGHT,
                    icon = Icons.Default.LightMode,
                    title = "Light Theme",
                    description = "Always use light colors",
                    isSelected = selectedTheme == ThemeMode.LIGHT,
                    onSelected = { selectedTheme = ThemeMode.LIGHT }
                )
                
                // Dark Theme Option
                ThemeOption(
                    themeMode = ThemeMode.DARK,
                    icon = Icons.Default.DarkMode,
                    title = "Dark Theme",
                    description = "Always use dark colors",
                    isSelected = selectedTheme == ThemeMode.DARK,
                    onSelected = { selectedTheme = ThemeMode.DARK }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onThemeModeSelected(selectedTheme)
                    onDismiss()
                },
                enabled = selectedTheme != currentThemeMode
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Individual theme option in the selection dialog
 */
@Composable
private fun ThemeOption(
    themeMode: ThemeMode,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelected,
                role = Role.RadioButton
            )
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null // Handled by parent Row
        )
        
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
