package com.brianmoler.borderlandsshiftcodes.data

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for the NetworkException class
 * 
 * Tests the custom exception used for network-related errors in the
 * ShiftCodeRepository when HTTP requests fail.
 */
class NetworkExceptionTest {

    @Test
    fun `test network exception creation with message and status code`() {
        // Given
        val message = "Network request failed"
        val statusCode = 404

        // When
        val exception = NetworkException(message, statusCode)

        // Then
        assertEquals(message, exception.message)
        assertEquals(statusCode, exception.statusCode)
    }

    @Test
    fun `test network exception creation with null message`() {
        // Given
        val statusCode = 500

        // When
        val exception = NetworkException(null, statusCode)

        // Then
        assertNull(exception.message)
        assertEquals(statusCode, exception.statusCode)
    }

    @Test
    fun `test network exception creation with empty message`() {
        // Given
        val message = ""
        val statusCode = 200

        // When
        val exception = NetworkException(message, statusCode)

        // Then
        assertEquals(message, exception.message)
        assertEquals(statusCode, exception.statusCode)
    }

    @Test
    fun `test network exception extends exception`() {
        // Given
        val exception = NetworkException("Test message", 200)

        // When & Then
        assertTrue(exception is Exception)
        assertTrue(exception is Throwable)
    }

    @Test
    fun `test network exception with various HTTP status codes`() {
        // Test common HTTP status codes
        val testCases = listOf(
            200 to "OK",
            400 to "Bad Request",
            401 to "Unauthorized",
            403 to "Forbidden",
            404 to "Not Found",
            500 to "Internal Server Error",
            502 to "Bad Gateway",
            503 to "Service Unavailable"
        )

        testCases.forEach { (statusCode, description) ->
            val exception = NetworkException(description, statusCode)
            assertEquals(statusCode, exception.statusCode)
            assertEquals(description, exception.message)
        }
    }

    @Test
    fun `test network exception with edge case status codes`() {
        // Test edge case status codes
        val edgeCases = listOf(0, -1, 999, 1000, Int.MAX_VALUE)

        edgeCases.forEach { statusCode ->
            val exception = NetworkException("Edge case: $statusCode", statusCode)
            assertEquals(statusCode, exception.statusCode)
            assertEquals("Edge case: $statusCode", exception.message)
        }
    }

    @Test
    fun `test network exception immutability`() {
        // Given
        val originalMessage = "Original message"
        val originalStatusCode = 404
        val exception = NetworkException(originalMessage, originalStatusCode)

        // When & Then
        // Properties should remain unchanged
        assertEquals(originalMessage, exception.message)
        assertEquals(originalStatusCode, exception.statusCode)
        
        // Multiple accesses should return same values
        assertEquals(originalMessage, exception.message)
        assertEquals(originalStatusCode, exception.statusCode)
    }

    @Test
    fun `test network exception in exception handling`() {
        // Given
        val networkException = NetworkException("Network failed", 500)

        // When & Then
        try {
            throw networkException
        } catch (e: NetworkException) {
            assertEquals("Network failed", e.message)
            assertEquals(500, e.statusCode)
        } catch (e: Exception) {
            fail("Should have caught NetworkException, not generic Exception")
        }
    }
} 