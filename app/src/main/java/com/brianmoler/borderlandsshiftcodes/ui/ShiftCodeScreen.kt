package com.brianmoler.borderlandsshiftcodes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.brianmoler.borderlandsshiftcodes.ui.components.*
import com.brianmoler.borderlandsshiftcodes.data.FilterType
import com.brianmoler.borderlandsshiftcodes.data.GameFilterType

/**
 * Determines screen size based on configuration
 */
@Composable
private fun getScreenSize(): ScreenSize {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp
    
    // Consider expanded if width is >= 840dp (tablet landscape or large phone)
    val isExpanded = screenWidthDp >= 840
    val isTablet = screenWidthDp >= 600 && screenHeightDp >= 600
    
    return ScreenSize(isExpanded, isTablet)
}

/**
 * Main screen for displaying SHiFT codes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftCodeScreen(
    viewModel: ShiftCodeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val screenSize = getScreenSize()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    
    // Bottom sheet state
    val filterBottomSheetState = rememberModalBottomSheetState()
    var showFilterBottomSheet by remember { mutableStateOf(false) }
    
    // Calculate available height for the drawer (screen height minus system insets)
    val availableHeight = with(LocalDensity.current) {
        val screenHeightDp = configuration.screenHeightDp
        val safeDrawingInsets = WindowInsets.safeDrawing.asPaddingValues()
        val topInset = safeDrawingInsets.calculateTopPadding()
        val bottomInset = safeDrawingInsets.calculateBottomPadding()
        (screenHeightDp.dp - topInset - bottomInset)
    }
    
    val drawerState = rememberDrawerState(
        initialValue = if (uiState.isDrawerOpen) DrawerValue.Open else DrawerValue.Closed
    )
    
    // Handle drawer state changes
    LaunchedEffect(uiState.isDrawerOpen) {
        if (uiState.isDrawerOpen) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }
    
    // Handle drawer state changes from gestures
    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.currentValue == DrawerValue.Open && !uiState.isDrawerOpen) {
            viewModel.openDrawer()
        } else if (drawerState.currentValue == DrawerValue.Closed && uiState.isDrawerOpen) {
            viewModel.closeDrawer()
        }
    }
    
    ModalNavigationDrawer(
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                NavigationDrawer(
                    onDismiss = { viewModel.closeDrawer() },
                    availableHeight = availableHeight,
                    currentThemeMode = uiState.themeMode,
                    onThemeModeChange = { themeMode -> viewModel.setThemeMode(themeMode) },
                    onShowThemeDialog = { viewModel.showThemeDialog() },
                    isCompactView = uiState.isCompactView,
                    onCompactViewChange = { isCompact -> viewModel.setCompactView(isCompact) }
                )
            }
        },
        drawerState = drawerState,
        gesturesEnabled = true,
        modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Scaffold(
            topBar = {
                ShiftCodeTopBar(
                    screenSize = screenSize,
                    onRefresh = { viewModel.syncWithRemoteData() },
                    onMenuClick = { viewModel.openDrawer() },
                    isSyncing = uiState.isSyncing,
                    modifier = Modifier
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showFilterBottomSheet = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                                                                                          Icon(
                                       imageVector = Icons.Default.Search,
                                       contentDescription = "Open filters"
                                   )
                }
            },
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
        ) { paddingValues ->
            ShiftCodeContent(
                uiState = uiState,
                screenSize = screenSize,
                onRefresh = { viewModel.syncWithRemoteData() },
                onFilterChanged = { viewModel.setFilter(it) },
                onGameFilterChanged = { viewModel.setGameFilter(it) },
                onToggleRedemption = { code, isRedeemed -> viewModel.toggleRedemptionStatus(code, isRedeemed) },
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        }
    }
    
    // Filter Bottom Sheet
    if (showFilterBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterBottomSheet = false },
            sheetState = filterBottomSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            FilterBottomSheet(
                currentFilter = uiState.currentFilter,
                currentGameFilter = uiState.currentGameFilter,
                currentRewardFilter = uiState.currentRewardFilter,
                onFilterChanged = { filterType ->
                    viewModel.setFilter(filterType)
                },
                onGameFilterChanged = { gameFilterType ->
                    viewModel.setGameFilter(gameFilterType)
                },
                onRewardFilterChanged = { rewardFilterType ->
                    viewModel.setRewardFilter(rewardFilterType)
                },
                onDismiss = { showFilterBottomSheet = false }
            )
        }
    }
    
    // Theme Selection Dialog
    if (uiState.showThemeDialog) {
        ThemeToggleDialog(
            currentThemeMode = uiState.themeMode,
            onThemeModeSelected = { themeMode ->
                viewModel.setThemeMode(themeMode)
            },
            onDismiss = { viewModel.hideThemeDialog() }
        )
    }
    
    // Dialogs
    // General options dialog removed - functionality now handled by persistent filter tray
}

/**
 * Main content area for the SHiFT codes screen
 */
@Composable
private fun ShiftCodeContent(
    uiState: ShiftCodeUiState,
    screenSize: ScreenSize,
    onRefresh: () -> Unit,
    onFilterChanged: (FilterType) -> Unit,
    onGameFilterChanged: (GameFilterType) -> Unit,
    onToggleRedemption: ((String, Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.isLoading -> LoadingState(screenSize)
            uiState.error != null -> ErrorState(
                error = uiState.error,
                screenSize = screenSize,
                onRetry = onRefresh
            )
            uiState.shiftCodes.isEmpty() -> EmptyState(screenSize)
            else -> ShiftCodeList(
                uiState = uiState,
                screenSize = screenSize,
                onFilterChanged = onFilterChanged,
                onGameFilterChanged = onGameFilterChanged,
                onToggleRedemption = onToggleRedemption
            )
        }
    }
} 