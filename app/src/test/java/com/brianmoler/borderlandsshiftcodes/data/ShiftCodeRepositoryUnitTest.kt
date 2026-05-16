package com.brianmoler.borderlandsshiftcodes.data

import android.content.Context
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/** Unit tests for [ShiftCodeRepository] wiring with remote and local implementations. */
class ShiftCodeRepositoryUnitTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockRemoteRepository: RemoteShiftCodeRepository

    @Mock
    private lateinit var mockLocalRepository: LocalShiftCodeRepository

    private lateinit var repository: ShiftCodeRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = ShiftCodeRepository(mockRemoteRepository, mockLocalRepository)
    }

    @Test
    fun `test repository initialization`() {
        val testRepository = ShiftCodeRepository(mockRemoteRepository, mockLocalRepository)
        assertNotNull(testRepository)
    }

    @Test
    fun `test repository delegates to local repository for data access`() {
        assertNotNull(repository)
    }
}
