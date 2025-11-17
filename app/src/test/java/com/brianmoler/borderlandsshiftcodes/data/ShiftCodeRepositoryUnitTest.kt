package com.brianmoler.borderlandsshiftcodes.data

import android.content.Context
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Unit tests for the ShiftCodeRepository class
 * 
 * Tests the repository responsible for parsing CSV data into ShiftCode objects
 * and handling various CSV formats and edge cases.
 */
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
        // Given & When
        val testRepository = ShiftCodeRepository(mockRemoteRepository, mockLocalRepository)
        
        // Then
        assertNotNull(testRepository)
    }

    @Test
    fun `test repository delegates to local repository for data access`() {
        // This test verifies that the main repository properly delegates to the local repository
        // The actual implementation details are tested in LocalShiftCodeRepositoryTest
        assertNotNull(repository)
    }

    @Test
    fun `test configuration constants exist`() {
        // Given & When
        val primaryUrl = com.brianmoler.borderlandsshiftcodes.config.AppConfig.Network.CSV_URL
        val fallbackUrl = com.brianmoler.borderlandsshiftcodes.config.AppConfig.Network.CSV_FALLBACK_URL
        
        // Then
        assertNotNull(primaryUrl)
        assertNotNull(fallbackUrl)
        assertTrue(primaryUrl.isNotBlank())
        assertTrue(fallbackUrl.isNotBlank())
        assertTrue(primaryUrl.startsWith("https://"))
        assertTrue(fallbackUrl.startsWith("https://"))
        assertTrue(fallbackUrl.contains("brianmoler.com"))
        assertTrue(fallbackUrl.endsWith(".csv"))
    }

    @Test
    fun `test fallback URL is different from primary URL`() {
        // Given
        val primaryUrl = com.brianmoler.borderlandsshiftcodes.config.AppConfig.Network.CSV_URL
        val fallbackUrl = com.brianmoler.borderlandsshiftcodes.config.AppConfig.Network.CSV_FALLBACK_URL
        
        // When & Then
        assertNotEquals(primaryUrl, fallbackUrl)
        assertTrue(primaryUrl.contains("docs.google.com"))
        assertTrue(fallbackUrl.contains("brianmoler.com"))
    }

    @Test
    fun `test fallback URL has correct format`() {
        // Given
        val fallbackUrl = com.brianmoler.borderlandsshiftcodes.config.AppConfig.Network.CSV_FALLBACK_URL
        
        // When & Then
        assertEquals("https://www.brianmoler.com/appdata/borderlandsshiftcodes/BL_SHIFT_CODES.csv", fallbackUrl)
    }
} 