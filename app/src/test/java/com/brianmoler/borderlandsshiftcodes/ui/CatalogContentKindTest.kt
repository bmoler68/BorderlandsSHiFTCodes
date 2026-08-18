package com.brianmoler.borderlandsshiftcodes.ui

import com.brianmoler.borderlandsshiftcodes.data.TestShiftCodeFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class CatalogContentKindTest {

    @Test
    fun loadingTakesPrecedenceOverCodesErrorAndOffline() {
        val state = ShiftCodeUiState(
            isLoading = true,
            shiftCodes = listOf(TestShiftCodeFactory.entity()),
            error = "db error",
            isOfflineMode = true
        )
        assertEquals(CatalogContentKind.LOADING, state.catalogContentKind())
    }

    @Test
    fun cachedCodesShowListWhenSyncIsOffline() {
        val state = ShiftCodeUiState(
            isLoading = false,
            shiftCodes = listOf(TestShiftCodeFactory.entity()),
            error = null,
            isOfflineMode = true
        )
        assertEquals(CatalogContentKind.LIST, state.catalogContentKind())
    }

    @Test
    fun cachedCodesShowListEvenIfCatalogErrorIsSet() {
        val state = ShiftCodeUiState(
            isLoading = false,
            shiftCodes = listOf(TestShiftCodeFactory.entity()),
            error = "db error",
            isOfflineMode = false
        )
        assertEquals(CatalogContentKind.LIST, state.catalogContentKind())
    }

    @Test
    fun emptyCatalogWithRoomErrorShowsError() {
        val state = ShiftCodeUiState(
            isLoading = false,
            error = "db error",
            isOfflineMode = false
        )
        assertEquals(CatalogContentKind.ERROR, state.catalogContentKind())
    }

    @Test
    fun emptyCatalogWithOfflineSyncShowsEmptyNotError() {
        val state = ShiftCodeUiState(
            isLoading = false,
            error = null,
            isOfflineMode = true
        )
        assertEquals(CatalogContentKind.EMPTY, state.catalogContentKind())
    }

    @Test
    fun emptyCatalogWhenOnlineShowsEmpty() {
        val state = ShiftCodeUiState(isLoading = false)
        assertEquals(CatalogContentKind.EMPTY, state.catalogContentKind())
    }
}
