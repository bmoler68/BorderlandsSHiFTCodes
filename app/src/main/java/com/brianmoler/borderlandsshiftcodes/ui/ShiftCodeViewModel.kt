package com.brianmoler.borderlandsshiftcodes.ui

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brianmoler.borderlandsshiftcodes.data.*
import com.brianmoler.borderlandsshiftcodes.notification.NotificationManager
import com.brianmoler.borderlandsshiftcodes.sync.SyncService
import com.brianmoler.borderlandsshiftcodes.ui.theme.ThemeMode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ShiftCodeViewModel"

/**
 * UI state for the SHiFT codes screen
 */
data class ShiftCodeUiState(
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val error: String? = null,
    val shiftCodes: List<ShiftCodeEntity> = emptyList(),
    val currentFilter: FilterType = FilterType.ALL,
    val currentGameFilter: GameFilterType = GameFilterType.ALL_GAMES,
    val currentRewardFilter: RewardFilterType = RewardFilterType.ALL_REWARDS,
    val isDrawerOpen: Boolean = false,
    val isOfflineMode: Boolean = false,
    val lastSyncTime: Long? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val showThemeDialog: Boolean = false,
    val isCompactView: Boolean = false
)

/**
 * ViewModel for managing SHiFT codes data and UI state
 */
class ShiftCodeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ShiftCodeUiState(isLoading = true))
    val uiState: StateFlow<ShiftCodeUiState> = _uiState.asStateFlow()

    private var repository: ShiftCodeRepository? = null
    private var sharedPreferences: SharedPreferences? = null
    private var notificationManager: NotificationManager? = null
    private var syncService: SyncService? = null
    private var currentLoadingJob: Job? = null

    companion object {
        private const val PREFS_NAME = "ShiftCodePreferences"
        private const val KEY_CURRENT_FILTER = "current_filter"
        private const val KEY_CURRENT_GAME_FILTER = "current_game_filter"
        private const val KEY_CURRENT_REWARD_FILTER = "current_reward_filter"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_COMPACT_VIEW = "compact_view"
    }

    /**
     * Initialize SharedPreferences and repository with context
     * @param context Application context
     */
    fun initializePreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // Initialize repositories
        val remoteRepository = RemoteShiftCodeRepository(context)
        val database = ShiftCodeDatabase.getDatabase(context)
        val localRepository = LocalShiftCodeRepository(database.shiftCodeDao())
        repository = ShiftCodeRepository(remoteRepository, localRepository)
        
        // Initialize notification manager and sync service
        notificationManager = NotificationManager(context)
        syncService = SyncService(context)
        
        loadPersistentFilters()
        loadThemePreference()
        loadCompactViewPreference()
        loadLocalCodes()
        // Attempt sync in background
        syncWithRemoteData()
    }

    /**
     * Loads the saved persistent filters from SharedPreferences
     */
    private fun loadPersistentFilters() {
        sharedPreferences?.let { prefs ->
            // Load status filter
            val filterName = prefs.getString(KEY_CURRENT_FILTER, FilterType.ALL.name)
            val currentFilter = try {
                FilterType.valueOf(filterName ?: FilterType.ALL.name)
            } catch (e: IllegalArgumentException) {
                FilterType.ALL
            }
            
            // Load game filter
            val gameFilterName = prefs.getString(KEY_CURRENT_GAME_FILTER, GameFilterType.ALL_GAMES.name)
            val currentGameFilter = try {
                GameFilterType.valueOf(gameFilterName ?: GameFilterType.ALL_GAMES.name)
            } catch (e: IllegalArgumentException) {
                GameFilterType.ALL_GAMES
            }
            
            // Load reward filter
            val rewardFilterName = prefs.getString(KEY_CURRENT_REWARD_FILTER, RewardFilterType.ALL_REWARDS.name)
            val currentRewardFilter = try {
                RewardFilterType.valueOf(rewardFilterName ?: RewardFilterType.ALL_REWARDS.name)
            } catch (e: IllegalArgumentException) {
                RewardFilterType.ALL_REWARDS
            }
            
            _uiState.value = _uiState.value.copy(
                currentFilter = currentFilter,
                currentGameFilter = currentGameFilter,
                currentRewardFilter = currentRewardFilter
            )
            Log.d(TAG, "Loaded persistent filters - Status: $currentFilter, Game: $currentGameFilter, Reward: $currentRewardFilter")
        }
    }

    /**
     * Loads the saved theme preference from SharedPreferences
     */
    private fun loadThemePreference() {
        sharedPreferences?.let { prefs ->
            val themeName = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
            val themeMode = try {
                ThemeMode.valueOf(themeName ?: ThemeMode.SYSTEM.name)
            } catch (e: IllegalArgumentException) {
                ThemeMode.SYSTEM
            }
            
            _uiState.value = _uiState.value.copy(themeMode = themeMode)
            Log.d(TAG, "Loaded theme preference: $themeMode")
        }
    }

    /**
     * Loads the saved compact view preference from SharedPreferences
     */
    private fun loadCompactViewPreference() {
        sharedPreferences?.let { prefs ->
            val isCompactView = prefs.getBoolean(KEY_COMPACT_VIEW, false)
            _uiState.value = _uiState.value.copy(isCompactView = isCompactView)
            Log.d(TAG, "Loaded compact view preference: $isCompactView")
        }
    }

    /**
     * Saves the current filter to SharedPreferences for persistence
     * @param filterType The filter type to save
     */
    private fun saveCurrentFilter(filterType: FilterType) {
        sharedPreferences?.let { prefs ->
            prefs.edit {
                putString(KEY_CURRENT_FILTER, filterType.name)
            }
            Log.d(TAG, "Saved current filter: $filterType")
        }
    }

    /**
     * Saves the current game filter to SharedPreferences for persistence
     * @param gameFilterType The game filter type to save
     */
    private fun saveCurrentGameFilter(gameFilterType: GameFilterType) {
        sharedPreferences?.let { prefs ->
            prefs.edit {
                putString(KEY_CURRENT_GAME_FILTER, gameFilterType.name)
            }
            Log.d(TAG, "Saved current game filter: $gameFilterType")
        }
    }

    /**
     * Saves the current reward filter to SharedPreferences for persistence
     * @param rewardFilterType The reward filter type to save
     */
    private fun saveCurrentRewardFilter(rewardFilterType: RewardFilterType) {
        sharedPreferences?.let { prefs ->
            prefs.edit {
                putString(KEY_CURRENT_REWARD_FILTER, rewardFilterType.name)
            }
            Log.d(TAG, "Saved current reward filter: $rewardFilterType")
        }
    }

    /**
     * Loads SHiFT codes from the local database
     * 
     * This method ensures only one Flow collection is active at a time by canceling
     * any previous loading job before starting a new one. This prevents race conditions
     * where multiple concurrent collections could overwrite each other with stale filter values.
     */
    private fun loadLocalCodes() {
        // Cancel any existing loading job to prevent race conditions
        currentLoadingJob?.cancel()
        
        Log.d(TAG, "Loading SHiFT codes from local database...")
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        // Capture current filter values from state
        val currentFilter = _uiState.value.currentFilter
        val currentGameFilter = _uiState.value.currentGameFilter
        val currentRewardFilter = _uiState.value.currentRewardFilter
        
        currentLoadingJob = viewModelScope.launch {
            try {
                repository?.getFilteredCodes(currentFilter, currentGameFilter, currentRewardFilter)?.collect { codes ->
                    Log.d(TAG, "Successfully loaded ${codes.size} SHiFT codes from local database with filters: Status=$currentFilter, Game=$currentGameFilter, Reward=$currentRewardFilter")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        shiftCodes = codes,
                        error = null
                    )
                }
            } catch (e: CancellationException) {
                // Re-throw cancellation exceptions - this is expected when canceling a previous load
                throw e
            } catch (e: Exception) {
                // Only update UI state if this is a real error (not cancellation)
                Log.e(TAG, "Error loading SHiFT codes from local database: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    /**
     * Syncs with remote data and updates local database
     */
    fun syncWithRemoteData() {
        Log.d(TAG, "Syncing with remote data...")
        _uiState.value = _uiState.value.copy(isSyncing = true, error = null)
        
        viewModelScope.launch {
            try {
                val syncResult = syncService?.syncWithNotifications()
                if (syncResult?.isSuccess == true) {
                    Log.d(TAG, "Sync successful: ${syncResult.codesAdded} added, ${syncResult.codesUpdated} updated, ${syncResult.codesDeleted} deleted")
                    _uiState.value = _uiState.value.copy(
                        isSyncing = false,
                        isOfflineMode = false,
                        lastSyncTime = System.currentTimeMillis(),
                        error = null
                    )
                    // Reload codes to reflect changes
                    loadLocalCodes()
                } else {
                    Log.w(TAG, "Sync failed: ${syncResult?.error}")
                    _uiState.value = _uiState.value.copy(
                        isSyncing = false,
                        isOfflineMode = true,
                        error = syncResult?.error ?: "Sync failed"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing with remote data: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    isOfflineMode = true,
                    error = e.message ?: "Sync error occurred"
                )
            }
        }
    }

    /**
     * Marks a SHiFT code as redeemed or unredeemed
     * @param code The SHiFT code string
     * @param isRedeemed The redemption status
     */
    fun toggleRedemptionStatus(code: String, isRedeemed: Boolean) {
        viewModelScope.launch {
            try {
                val success = repository?.updateRedemptionStatus(code, isRedeemed) ?: false
                if (success) {
                    Log.d(TAG, "Successfully updated redemption status for code: $code")
                    // Reload codes to reflect changes
                    loadLocalCodes()
                } else {
                    Log.e(TAG, "Failed to update redemption status for code: $code")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating redemption status: ${e.message}")
            }
        }
    }

    /**
     * Sets the current filter and reloads codes
     * @param filterType The filter type to apply
     */
    fun setFilter(filterType: FilterType) {
        Log.d(TAG, "Setting filter to: $filterType")
        _uiState.value = _uiState.value.copy(currentFilter = filterType)
        saveCurrentFilter(filterType)
        loadLocalCodes() // Reload with new filter
    }

    /**
     * Sets the current game filter and reloads codes
     * @param gameFilterType The game filter type to apply
     */
    fun setGameFilter(gameFilterType: GameFilterType) {
        Log.d(TAG, "Setting game filter to: $gameFilterType")
        _uiState.value = _uiState.value.copy(currentGameFilter = gameFilterType)
        saveCurrentGameFilter(gameFilterType)
        loadLocalCodes() // Reload with new filter
    }

    /**
     * Sets the current reward filter and reloads codes
     * @param rewardFilterType The reward filter type to apply
     */
    fun setRewardFilter(rewardFilterType: RewardFilterType) {
        Log.d(TAG, "Setting reward filter to: $rewardFilterType")
        _uiState.value = _uiState.value.copy(currentRewardFilter = rewardFilterType)
        saveCurrentRewardFilter(rewardFilterType)
        loadLocalCodes() // Reload with new filter
    }

    /**
     * Opens the navigation drawer
     */
    fun openDrawer() {
        _uiState.value = _uiState.value.copy(isDrawerOpen = true)
    }

    /**
     * Closes the navigation drawer
     */
    fun closeDrawer() {
        _uiState.value = _uiState.value.copy(isDrawerOpen = false)
    }

    /**
     * Sets the theme mode and saves it to SharedPreferences
     * @param themeMode The theme mode to apply
     */
    fun setThemeMode(themeMode: ThemeMode) {
        Log.d(TAG, "Setting theme mode to: $themeMode")
        _uiState.value = _uiState.value.copy(themeMode = themeMode)
        saveThemePreference(themeMode)
    }

    /**
     * Shows the theme selection dialog
     */
    fun showThemeDialog() {
        _uiState.value = _uiState.value.copy(showThemeDialog = true)
    }

    /**
     * Hides the theme selection dialog
     */
    fun hideThemeDialog() {
        _uiState.value = _uiState.value.copy(showThemeDialog = false)
    }

    /**
     * Saves the theme preference to SharedPreferences for persistence
     * @param themeMode The theme mode to save
     */
    private fun saveThemePreference(themeMode: ThemeMode) {
        sharedPreferences?.let { prefs ->
            prefs.edit {
                putString(KEY_THEME_MODE, themeMode.name)
            }
            Log.d(TAG, "Saved theme preference: $themeMode")
        }
    }

    /**
     * Sets the compact view mode and saves it to SharedPreferences
     * @param isCompactView Whether compact view should be enabled
     */
    fun setCompactView(isCompactView: Boolean) {
        Log.d(TAG, "Setting compact view to: $isCompactView")
        _uiState.value = _uiState.value.copy(isCompactView = isCompactView)
        saveCompactViewPreference(isCompactView)
    }

    /**
     * Saves the compact view preference to SharedPreferences for persistence
     * @param isCompactView Whether compact view is enabled
     */
    private fun saveCompactViewPreference(isCompactView: Boolean) {
        sharedPreferences?.let { prefs ->
            prefs.edit {
                putBoolean(KEY_COMPACT_VIEW, isCompactView)
            }
            Log.d(TAG, "Saved compact view preference: $isCompactView")
        }
    }
} 