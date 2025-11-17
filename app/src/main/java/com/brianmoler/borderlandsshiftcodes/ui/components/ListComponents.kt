package com.brianmoler.borderlandsshiftcodes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brianmoler.borderlandsshiftcodes.data.ShiftCodeEntity
import com.brianmoler.borderlandsshiftcodes.data.FilterType
import com.brianmoler.borderlandsshiftcodes.data.GameFilterType
import com.brianmoler.borderlandsshiftcodes.data.RewardFilterType
import com.brianmoler.borderlandsshiftcodes.ui.ShiftCodeUiState
import com.brianmoler.borderlandsshiftcodes.ui.constants.UiConstants
import com.brianmoler.borderlandsshiftcodes.ui.theme.*

import com.brianmoler.borderlandsshiftcodes.util.ClipboardUtil

/**
 * Main list component for displaying SHiFT codes
 */
@Composable
fun ShiftCodeList(
    uiState: ShiftCodeUiState,
    screenSize: ScreenSize,
    onFilterChanged: (FilterType) -> Unit,
    onGameFilterChanged: (GameFilterType) -> Unit,
    onToggleRedemption: ((String, Boolean) -> Unit)? = null
) {
    val filteredCodes = getFilteredCodes(uiState)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = if (screenSize.isExpanded) UiConstants.Spacing.LARGE else UiConstants.Spacing.MEDIUM
            )
    ) {
        // Small padding between header and filter status
        Spacer(modifier = Modifier.height(UiConstants.Spacing.SMALL))
        
        // Current filters summary
        CurrentFiltersSummary(
            currentFilter = uiState.currentFilter,
            currentGameFilter = uiState.currentGameFilter,
            currentRewardFilter = uiState.currentRewardFilter
        )
        
        Spacer(modifier = Modifier.height(UiConstants.Spacing.MEDIUM))
        
        // Codes list
        if (filteredCodes.isEmpty()) {
            NoFilteredCodesMessage()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(
                    if (uiState.isCompactView) 4.dp else UiConstants.Spacing.MEDIUM
                ),
                contentPadding = PaddingValues(vertical = UiConstants.Spacing.XS)
            ) {
                items(filteredCodes) { shiftCode ->
                    if (uiState.isCompactView) {
                        CompactShiftCodeCard(
                            shiftCode = shiftCode,
                            screenSize = screenSize,
                            currentGameFilter = uiState.currentGameFilter,
                            onToggleRedemption = onToggleRedemption
                        )
                    } else {
                        ShiftCodeCard(
                            shiftCode = shiftCode,
                            screenSize = screenSize,
                            currentGameFilter = uiState.currentGameFilter,
                            onToggleRedemption = onToggleRedemption
                        )
                    }
                }
            }
        }
    }
}

/**
 * Summary of current active filters
 */
@Composable
private fun CurrentFiltersSummary(
    currentFilter: FilterType,
    currentGameFilter: GameFilterType,
    currentRewardFilter: RewardFilterType,
    modifier: Modifier = Modifier
) {
    val filterText = when (currentFilter) {
        FilterType.ALL -> "All Status"
        FilterType.ACTIVE -> "Active Only"
        FilterType.EXPIRED -> "Expired Only"
        FilterType.NON_EXPIRING -> "Non-Expiring Only"
        FilterType.NOT_REDEEMED -> "Not Redeemed"
    }
    
    val gameText = when (currentGameFilter) {
        GameFilterType.ALL_GAMES -> "All Games"
        GameFilterType.BL -> "Borderlands 1"
        GameFilterType.BL_TPS -> "Borderlands TPS"
        GameFilterType.BL2 -> "Borderlands 2"
        GameFilterType.BL3 -> "Borderlands 3"
        GameFilterType.BL4 -> "Borderlands 4"
        GameFilterType.WONDERLANDS -> "Wonderlands"
    }
    
    val rewardText = when (currentRewardFilter) {
        RewardFilterType.ALL_REWARDS -> null // Don't show if all rewards
        RewardFilterType.KEY -> "Keys"
        RewardFilterType.COSMETIC -> "Cosmetics"
        RewardFilterType.GEAR -> "Gear"
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(UiConstants.CornerRadius.LARGE),
        elevation = CardDefaults.cardElevation(
            defaultElevation = UiConstants.Elevation.SMALL
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UiConstants.Spacing.MEDIUM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filter icon
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(UiConstants.Spacing.MEDIUM))
            
            // Filter content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Active Filters",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(UiConstants.Spacing.XS))
                
                Text(
                    text = buildString {
                        append(filterText)
                        append(" • ")
                        append(gameText)
                        rewardText?.let {
                            append(" • ")
                            append(it)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Status indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = when (currentFilter) {
                            FilterType.ACTIVE -> BorderlandsActiveColor
                            FilterType.EXPIRED -> BorderlandsExpiredColor
                            FilterType.NON_EXPIRING -> BorderlandsNonExpiringColor
                            FilterType.NOT_REDEEMED -> Color(0xFFFF9800) // Orange for redemption status
                            FilterType.ALL -> BorderlandsPrimary
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * Bottom sheet for filter options
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilter: FilterType,
    currentGameFilter: GameFilterType,
    currentRewardFilter: RewardFilterType,
    onFilterChanged: (FilterType) -> Unit,
    onGameFilterChanged: (GameFilterType) -> Unit,
    onRewardFilterChanged: (RewardFilterType) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusFilters = listOf(
        FilterType.ALL to "All",
        FilterType.ACTIVE to "Active",
        FilterType.EXPIRED to "Expired",
        FilterType.NON_EXPIRING to "Non-Expiring",
        FilterType.NOT_REDEEMED to "Not Redeemed"
    )
    
    val gameFilters = listOf(
        GameFilterType.ALL_GAMES to "All",
        GameFilterType.BL to "Borderlands 1",
        GameFilterType.BL_TPS to "Borderlands TPS",
        GameFilterType.BL2 to "Borderlands 2",
        GameFilterType.BL3 to "Borderlands 3",
        GameFilterType.BL4 to "Borderlands 4",
        GameFilterType.WONDERLANDS to "Wonderlands"
    )
    
    val rewardFilters = listOf(
        RewardFilterType.ALL_REWARDS to "All",
        RewardFilterType.KEY to "Keys",
        RewardFilterType.COSMETIC to "Cosmetics",
        RewardFilterType.GEAR to "Gear"
    )
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = UiConstants.Spacing.MEDIUM,
                end = UiConstants.Spacing.MEDIUM,
                bottom = UiConstants.Spacing.MEDIUM,
                top = UiConstants.Spacing.SMALL
            )
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close filters"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(UiConstants.Spacing.XS))
        
        // Status Filter Section
        Text(
            text = "Status",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(UiConstants.Spacing.XS))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.SMALL),
            contentPadding = PaddingValues(vertical = UiConstants.Spacing.XS)
        ) {
            items(statusFilters) { (filterType, label) ->
                FilterChipItem(
                    onClick = { onFilterChanged(filterType) },
                    label = { Text(label, fontSize = UiConstants.Typography.SMALL_TEXT) },
                    selected = currentFilter == filterType,
                    filterType = filterType
                )
            }
        }
        
        Spacer(modifier = Modifier.height(UiConstants.Spacing.XS))
        
        // Game Filter Section
        Text(
            text = "Games",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(UiConstants.Spacing.XS))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.SMALL),
            contentPadding = PaddingValues(vertical = UiConstants.Spacing.XS)
        ) {
            items(gameFilters) { (gameFilterType, label) ->
                FilterChipItem(
                    onClick = { onGameFilterChanged(gameFilterType) },
                    label = { Text(label, fontSize = UiConstants.Typography.SMALL_TEXT) },
                    selected = currentGameFilter == gameFilterType,
                    gameFilterType = gameFilterType
                )
            }
        }
        
        Spacer(modifier = Modifier.height(UiConstants.Spacing.XS))
        
        // Reward Filter Section
        Text(
            text = "Reward Type",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(UiConstants.Spacing.XS))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.SMALL),
            contentPadding = PaddingValues(vertical = UiConstants.Spacing.XS)
        ) {
            items(rewardFilters) { (rewardFilterType, label) ->
                FilterChipItem(
                    onClick = { onRewardFilterChanged(rewardFilterType) },
                    label = { Text(label, fontSize = UiConstants.Typography.SMALL_TEXT) },
                    selected = currentRewardFilter == rewardFilterType,
                    rewardFilterType = rewardFilterType
                )
            }
        }
        
        Spacer(modifier = Modifier.height(UiConstants.Spacing.XS))
        
        // Apply Button
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Apply Filters")
        }
    }
}

/**
 * Filter chip for use in the bottom sheet
 */
@Composable
private fun FilterChipItem(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    filterType: FilterType? = null,
    gameFilterType: GameFilterType? = null,
    rewardFilterType: RewardFilterType? = null
) {
    // Determine colors based on selection and type
    val containerColor = when {
        selected && filterType != null -> {
            when (filterType) {
                FilterType.ACTIVE -> getActiveBackgroundColor()
                FilterType.EXPIRED -> getExpiredBackgroundColor()
                FilterType.NON_EXPIRING -> getNonExpiringBackgroundColor()
                FilterType.NOT_REDEEMED -> getNotRedeemedBackgroundColor()
                FilterType.ALL -> getAllFilterBackgroundColor()
            }
        }
        selected && gameFilterType != null -> {
            when (gameFilterType) {
                GameFilterType.BL -> UiConstants.GameColors.BORDERLANDS_1
                GameFilterType.BL_TPS -> UiConstants.GameColors.BORDERLANDS_TPS
                GameFilterType.BL2 -> UiConstants.GameColors.BORDERLANDS_2
                GameFilterType.BL3 -> UiConstants.GameColors.BORDERLANDS_3
                GameFilterType.BL4 -> UiConstants.GameColors.BORDERLANDS_4
                GameFilterType.WONDERLANDS -> UiConstants.GameColors.WONDERLANDS
                GameFilterType.ALL_GAMES -> getAllFilterBackgroundColor()
            }
        }
        selected && rewardFilterType != null -> {
            // Use a distinct color for reward filters - you can customize these
            when (rewardFilterType) {
                RewardFilterType.KEY -> Color(0xFFDAA520) // Dark goldenrod for keys (better text contrast)
                RewardFilterType.COSMETIC -> Color(0xFFFF69B4) // Hot pink for cosmetics
                RewardFilterType.GEAR -> Color(0xFF4169E1) // Royal blue for gear
                RewardFilterType.ALL_REWARDS -> getAllFilterBackgroundColor()
            }
        }
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = when {
        selected && filterType != null -> {
            when (filterType) {
                FilterType.ACTIVE -> getActiveTextColor()
                FilterType.EXPIRED -> getExpiredTextColor()
                FilterType.NON_EXPIRING -> getNonExpiringTextColor()
                FilterType.NOT_REDEEMED -> getNotRedeemedTextColor()
                FilterType.ALL -> Color(0xFFFFFFFF) // White text for consistency
            }
        }
        selected && gameFilterType != null -> {
            when (gameFilterType) {
                GameFilterType.ALL_GAMES -> Color(0xFFFFFFFF) // White text for ALL_GAMES consistency
                else -> MaterialTheme.colorScheme.onPrimary // Use onPrimary for other game chips
            }
        }
        selected && rewardFilterType != null -> {
            Color(0xFFFFFFFF) // White text for reward filters
        }
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    FilterChip(
        onClick = onClick,
        label = label,
        selected = selected,
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = containerColor,
            selectedLabelColor = contentColor
        )
    )
}

/**
 * Professional message shown when no codes match the current filter
 */
@Composable
private fun NoFilteredCodesMessage() {
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
                Text(
                    text = "🎮",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = UiConstants.Spacing.MEDIUM)
                )
                Text(
                    text = "No codes found",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(UiConstants.Spacing.SMALL))
                Text(
                    text = "Try adjusting your filters to see more results",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Professional SHiFT code card with modern design
 */
@Composable
fun ShiftCodeCard(
    shiftCode: ShiftCodeEntity,
    screenSize: ScreenSize,
    currentGameFilter: GameFilterType,
    onToggleRedemption: ((String, Boolean) -> Unit)? = null
) {
    val context = LocalContext.current
    val isNonExpiring = shiftCode.isNonExpiring()
    val isExpired = shiftCode.isExpired()
    
    // Professional status colors
    val statusColor = when {
        isNonExpiring -> BorderlandsNonExpiringColor
        isExpired -> BorderlandsExpiredColor
        else -> BorderlandsActiveColor
    }
    
    // Theme-aware status text colors
    val statusTextColor = when {
        isNonExpiring -> getNonExpiringTextColor()
        isExpired -> getExpiredTextColor()
        else -> getActiveTextColor()
    }
    
    val statusBackgroundColor = when {
        isNonExpiring -> getNonExpiringBackgroundColor()
        isExpired -> getExpiredBackgroundColor()
        else -> getActiveBackgroundColor()
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = UiConstants.Spacing.XS),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(UiConstants.CornerRadius.LARGE),
        elevation = CardDefaults.cardElevation(
            defaultElevation = UiConstants.Elevation.LARGE
        ),
        border = BorderStroke(2.dp, statusColor.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface
                )
                .padding(
                    horizontal = if (screenSize.isExpanded) UiConstants.Spacing.LARGE else UiConstants.Spacing.MEDIUM,
                    vertical = if (screenSize.isExpanded) UiConstants.Spacing.MEDIUM else UiConstants.Spacing.SMALL
                )
        ) {
            // Header section with status indicator and redemption toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Status badges row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.SMALL)
                ) {
                    // Status badge
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = statusBackgroundColor
                        ),
                        shape = RoundedCornerShape(UiConstants.CornerRadius.MEDIUM),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = UiConstants.Elevation.XS
                        )
                    ) {
                        Text(
                            text = shiftCode.getStatus(),
                            modifier = Modifier.padding(
                                horizontal = UiConstants.Spacing.MEDIUM,
                                vertical = UiConstants.Spacing.SMALL
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = statusTextColor,
                            fontSize = if (screenSize.isExpanded) UiConstants.Typography.SMALL_TEXT else UiConstants.Typography.CAPTION
                        )
                    }
                    
                    // Enhanced redemption status badge
                    Card(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onToggleRedemption?.invoke(shiftCode.code, !shiftCode.isRedeemed)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor =                                 if (shiftCode.isRedeemed) 
                                    getRedeemedBackgroundColor()
                                else 
                                    getNotRedeemedBackgroundColor()
                        ),
                        shape = RoundedCornerShape(UiConstants.CornerRadius.MEDIUM),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = UiConstants.Elevation.XS
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = UiConstants.Spacing.MEDIUM,
                                vertical = UiConstants.Spacing.SMALL
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.XS)
                        ) {
                            Icon(
                                imageVector = if (shiftCode.isRedeemed) 
                                    Icons.Default.CheckCircle 
                                else 
                                    Icons.Default.RadioButtonUnchecked,
                                contentDescription = if (shiftCode.isRedeemed) 
                                    "Redeemed" 
                                else 
                                    "Not redeemed",
                                tint = if (shiftCode.isRedeemed) 
                                    getRedeemedTextColor()
                                else 
                                    getNotRedeemedTextColor(),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (shiftCode.isRedeemed) "Redeemed" else "Not Redeemed",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                    color = if (shiftCode.isRedeemed) 
                                        getRedeemedTextColor()
                                    else 
                                        getNotRedeemedTextColor(),
                                fontSize = if (screenSize.isExpanded) UiConstants.Typography.SMALL_TEXT else UiConstants.Typography.CAPTION
                            )
                        }
                    }
                }
                
                // Expiration info
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Expires",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = if (screenSize.isExpanded) UiConstants.Typography.CAPTION else 9.sp
                    )
                    Text(
                        text = if (isNonExpiring) "Never" else shiftCode.getDisplayExpiration(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = if (screenSize.isExpanded) UiConstants.Typography.MEDIUM_TEXT else UiConstants.Typography.SMALL_TEXT
                    )
                    // Display time below date if available
                    if (!isNonExpiring && shiftCode.expiration != ShiftCodeEntity.UNKNOWN_EXPIRATION_DATE && shiftCode.getDisplayTime().isNotBlank()) {
                        Text(
                            text = shiftCode.getDisplayTime(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = if (screenSize.isExpanded) UiConstants.Typography.SMALL_TEXT else UiConstants.Typography.CAPTION
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(UiConstants.Spacing.SMALL))
            
            // Code section with professional styling
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(UiConstants.CornerRadius.MEDIUM),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = UiConstants.Elevation.SMALL
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(UiConstants.Spacing.SMALL),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = shiftCode.code,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = if (screenSize.isExpanded) UiConstants.Typography.SMALL_TEXT else UiConstants.Typography.CAPTION,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                    
                    Spacer(modifier = Modifier.width(UiConstants.Spacing.MEDIUM))
                    
                    // Professional copy button
                    Card(
                        modifier = Modifier
                            .size(if (screenSize.isExpanded) 32.dp else 28.dp)
                            .clickable {
                                ClipboardUtil.copyToClipboard(
                                    context = context,
                                    text = shiftCode.code,
                                    label = "SHiFT Code",
                                    toastMessage = "Code copied to clipboard!"
                                )
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
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
                            Text(
                                text = "📋",
                                fontSize = if (screenSize.isExpanded) 14.sp else 12.sp
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(UiConstants.Spacing.SMALL))
            
            // Reward text (no header)
            Text(
                text = shiftCode.reward,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = if (screenSize.isExpanded) UiConstants.Typography.MEDIUM_TEXT else UiConstants.Typography.SMALL_TEXT,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(horizontal = UiConstants.Spacing.SMALL)
            )
            
            Spacer(modifier = Modifier.height(UiConstants.Spacing.SMALL))
            
            // Compatible Games section (no header)
            Column {
                
                // Professional game chips layout - show only filtered game when filter is active
                val gameChips = mutableListOf<Pair<String, Color>>()
                
                when (currentGameFilter) {
                    GameFilterType.ALL_GAMES -> {
                        // Show all compatible games when "All" is selected
                        if (shiftCode.bl) gameChips.add("BL" to UiConstants.GameColors.BORDERLANDS_1)
                        if (shiftCode.blTps) gameChips.add("TPS" to UiConstants.GameColors.BORDERLANDS_TPS)
                        if (shiftCode.bl2) gameChips.add("BL2" to UiConstants.GameColors.BORDERLANDS_2)
                        if (shiftCode.bl3) gameChips.add("BL3" to UiConstants.GameColors.BORDERLANDS_3)
                        if (shiftCode.bl4) gameChips.add("BL4" to UiConstants.GameColors.BORDERLANDS_4)
                        if (shiftCode.wonderlands) gameChips.add("WL" to UiConstants.GameColors.WONDERLANDS)
                    }
                    GameFilterType.BL -> if (shiftCode.bl) gameChips.add("BL" to UiConstants.GameColors.BORDERLANDS_1)
                    GameFilterType.BL_TPS -> if (shiftCode.blTps) gameChips.add("TPS" to UiConstants.GameColors.BORDERLANDS_TPS)
                    GameFilterType.BL2 -> if (shiftCode.bl2) gameChips.add("BL2" to UiConstants.GameColors.BORDERLANDS_2)
                    GameFilterType.BL3 -> if (shiftCode.bl3) gameChips.add("BL3" to UiConstants.GameColors.BORDERLANDS_3)
                    GameFilterType.BL4 -> if (shiftCode.bl4) gameChips.add("BL4" to UiConstants.GameColors.BORDERLANDS_4)
                    GameFilterType.WONDERLANDS -> if (shiftCode.wonderlands) gameChips.add("WL" to UiConstants.GameColors.WONDERLANDS)
                }
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.SMALL),
                    contentPadding = PaddingValues(vertical = UiConstants.Spacing.XS)
                ) {
                    items(gameChips) { (text, color) ->
                        ProfessionalGameChip(
                            text = text,
                            color = color,
                            isExpanded = screenSize.isExpanded
                        )
                    }
                }
            }
        }
    }
}

/**
 * Ultra-compact SHiFT code card for maximum space efficiency
 */
@Composable
fun CompactShiftCodeCard(
    shiftCode: ShiftCodeEntity,
    screenSize: ScreenSize,
    currentGameFilter: GameFilterType,
    onToggleRedemption: ((String, Boolean) -> Unit)? = null
) {
    val context = LocalContext.current
    val isNonExpiring = shiftCode.isNonExpiring()
    val isExpired = shiftCode.isExpired()
    
    // Status color for left border
    val statusColor = when {
        isNonExpiring -> BorderlandsNonExpiringColor
        isExpired -> BorderlandsExpiredColor
        else -> BorderlandsActiveColor
    }
    
    // Status dot color - use the same color as the left border for consistency and visibility
    val statusDotColor = statusColor
    
    // Compact expiration display (MM/dd format)
    val expirationDisplay = when {
        isNonExpiring -> "Never"
        shiftCode.expiration != ShiftCodeEntity.UNKNOWN_EXPIRATION_DATE -> {
            val dateParts = shiftCode.expiration.split("-")
            if (dateParts.size == 3) {
                "${dateParts[1].toIntOrNull() ?: ""}/${dateParts[2].toIntOrNull() ?: ""}"
            } else {
                shiftCode.getDisplayExpiration()
            }
        }
        else -> "Unknown"
    }
    
    // Get game chips
    val gameChips = mutableListOf<String>()
    when (currentGameFilter) {
        GameFilterType.ALL_GAMES -> {
            if (shiftCode.bl) gameChips.add("BL")
            if (shiftCode.blTps) gameChips.add("TPS")
            if (shiftCode.bl2) gameChips.add("BL2")
            if (shiftCode.bl3) gameChips.add("BL3")
            if (shiftCode.bl4) gameChips.add("BL4")
            if (shiftCode.wonderlands) gameChips.add("WL")
        }
        GameFilterType.BL -> if (shiftCode.bl) gameChips.add("BL")
        GameFilterType.BL_TPS -> if (shiftCode.blTps) gameChips.add("TPS")
        GameFilterType.BL2 -> if (shiftCode.bl2) gameChips.add("BL2")
        GameFilterType.BL3 -> if (shiftCode.bl3) gameChips.add("BL3")
        GameFilterType.BL4 -> if (shiftCode.bl4) gameChips.add("BL4")
        GameFilterType.WONDERLANDS -> if (shiftCode.wonderlands) gameChips.add("WL")
    }
    val gamesText = gameChips.joinToString("•")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(UiConstants.CornerRadius.SMALL),
        elevation = CardDefaults.cardElevation(
            defaultElevation = UiConstants.Elevation.XS
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 6.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Left border indicator - matches row height when using IntrinsicSize.Min
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxSize()
                    .background(statusColor, shape = RoundedCornerShape(1.5.dp))
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // First line: Status dot + Code + Copy button + Redemption icon + Expiration
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status dot
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(statusDotColor, shape = CircleShape)
                    )
                    
                    // Code text
                    Text(
                        text = shiftCode.code,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                    
                    // Copy button (icon only)
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                ClipboardUtil.copyToClipboard(
                                    context = context,
                                    text = shiftCode.code,
                                    label = "SHiFT Code",
                                    toastMessage = "Code copied!"
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy code",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Redemption icon
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                color = if (shiftCode.isRedeemed) 
                                    getRedeemedBackgroundColor().copy(alpha = 0.3f)
                                else 
                                    getNotRedeemedBackgroundColor().copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                            .clickable {
                                onToggleRedemption?.invoke(shiftCode.code, !shiftCode.isRedeemed)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (shiftCode.isRedeemed) 
                                Icons.Default.CheckCircle 
                            else 
                                Icons.Default.RadioButtonUnchecked,
                            contentDescription = if (shiftCode.isRedeemed) "Redeemed" else "Not redeemed",
                            modifier = Modifier.size(14.dp),
                            tint = if (shiftCode.isRedeemed) 
                                BorderlandsActiveColor
                            else 
                                Color(0xFFD97706) // Amber/brown for not redeemed
                        )
                    }
                    
                    // Expiration (compact)
                    Text(
                        text = expirationDisplay,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp
                    )
                }
                
                // Second line: Reward + Games
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reward text
                    Text(
                        text = shiftCode.reward,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                    
                    // Games (if any)
                    if (gamesText.isNotEmpty()) {
                        Text(
                            text = gamesText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Professional game compatibility chip
 */
@Composable
fun ProfessionalGameChip(
    text: String,
    color: Color,
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(UiConstants.CornerRadius.SMALL),
        elevation = CardDefaults.cardElevation(
            defaultElevation = UiConstants.Elevation.XS
        ),
        modifier = modifier
            .width(if (isExpanded) 50.dp else 40.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = UiConstants.Spacing.SMALL,
                    vertical = UiConstants.Spacing.XS
                ),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = if (isExpanded) UiConstants.Typography.CAPTION else 9.sp,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Gets filtered codes based on the current UI state
 */
private fun getFilteredCodes(uiState: ShiftCodeUiState): List<ShiftCodeEntity> {
    val codes = uiState.shiftCodes
    val statusFilteredCodes = when (uiState.currentFilter) {
        FilterType.ALL -> codes
        FilterType.ACTIVE -> codes.filter { !it.isExpired() && !it.isNonExpiring() }
        FilterType.EXPIRED -> codes.filter { it.isExpired() }
        FilterType.NON_EXPIRING -> codes.filter { it.isNonExpiring() }
        FilterType.NOT_REDEEMED -> codes.filter { !it.isRedeemed }
    }
    
    val gameFilteredCodes = when (uiState.currentGameFilter) {
        GameFilterType.ALL_GAMES -> statusFilteredCodes
        GameFilterType.BL -> statusFilteredCodes.filter { it.bl }
        GameFilterType.BL_TPS -> statusFilteredCodes.filter { it.blTps }
        GameFilterType.BL2 -> statusFilteredCodes.filter { it.bl2 }
        GameFilterType.BL3 -> statusFilteredCodes.filter { it.bl3 }
        GameFilterType.BL4 -> statusFilteredCodes.filter { it.bl4 }
        GameFilterType.WONDERLANDS -> statusFilteredCodes.filter { it.wonderlands }
    }
    
    val rewardFilteredCodes = when (uiState.currentRewardFilter) {
        RewardFilterType.ALL_REWARDS -> gameFilteredCodes
        RewardFilterType.KEY -> gameFilteredCodes.filter { it.isKey }
        RewardFilterType.COSMETIC -> gameFilteredCodes.filter { it.isCosmetic }
        RewardFilterType.GEAR -> gameFilteredCodes.filter { it.isGear }
    }
    
    // Sort by expiration date in descending order (newest first) - matches database sorting
    return rewardFilteredCodes.sortedByDescending { it.expiration }
} 