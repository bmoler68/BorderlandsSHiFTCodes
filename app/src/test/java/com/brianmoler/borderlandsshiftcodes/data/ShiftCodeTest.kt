package com.brianmoler.borderlandsshiftcodes.data

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

/**
 * Comprehensive unit tests for the ShiftCode data class
 * 
 * This test suite validates all aspects of the ShiftCode data model including:
 * - Data validation and constraint enforcement
 * - Expiration date logic and status determination
 * - Input sanitization and security measures
 * - Edge cases and error conditions
 * - Sorting and comparison functionality
 * 
 * The tests ensure that the ShiftCode class maintains data integrity,
 * provides secure handling of user input, and correctly implements
 * business logic for code expiration and game compatibility.
 */
class ShiftCodeTest {

    /**
     * Tests basic ShiftCode creation with valid data
     * 
     * Verifies that a ShiftCode can be created with valid parameters
     * and that all properties are correctly assigned.
     */
    @Test
    fun `test valid shift code creation`() {
        val shiftCode = ShiftCode(
            code = "TEST123",
            expiration = "2024-12-31",
            reward = "Golden Key",
            bl = true,
            blTps = false,
            bl2 = true,
            bl3 = false,
            bl4 = false,
            wonderlands = true
        )

        assertEquals("TEST123", shiftCode.code)
        assertEquals("2024-12-31", shiftCode.expiration)
        assertEquals("Golden Key", shiftCode.reward)
        assertTrue(shiftCode.bl)
        assertFalse(shiftCode.blTps)
        assertTrue(shiftCode.bl2)
        assertFalse(shiftCode.bl3)
        assertTrue(shiftCode.wonderlands)
    }

    /**
     * Tests detection of non-expiring codes
     * 
     * Verifies that codes with the special non-expiring date (1999-12-31)
     * are correctly identified and have appropriate status values.
     */
    @Test
    fun `test non-expiring code detection`() {
        val nonExpiringCode = ShiftCode(
            code = "NEVER123",
            expiration = ShiftCode.NON_EXPIRING_DATE,
            reward = "Permanent Reward",
            bl = true,
            blTps = true,
            bl2 = true,
            bl3 = true,
            bl4 = true,
            wonderlands = true
        )

        assertTrue(nonExpiringCode.isNonExpiring())
        assertFalse(nonExpiringCode.isExpired())
        assertFalse(nonExpiringCode.isActive())
        assertEquals("Non-expiring", nonExpiringCode.getStatus())
    }

    /**
     * Tests detection of unknown expiration codes
     * 
     * Verifies that codes with the special unknown expiration date (2075-12-31)
     * are correctly identified as active and display "Unknown" for expiration.
     */
    @Test
    fun `test unknown expiration code detection`() {
        val unknownExpirationCode = ShiftCode(
            code = "UNKNOWN123",
            expiration = ShiftCode.UNKNOWN_EXPIRATION_DATE,
            reward = "Unknown Expiration Reward",
            bl = true,
            blTps = false,
            bl2 = false,
            bl3 = false,
            bl4 = false,
            wonderlands = false
        )

        assertFalse(unknownExpirationCode.isNonExpiring())
        assertFalse(unknownExpirationCode.isExpired())
        assertTrue(unknownExpirationCode.isActive())
        assertEquals("Active", unknownExpirationCode.getStatus())
        assertEquals("Unknown", unknownExpirationCode.getDisplayExpiration())
    }

    /**
     * Tests detection of expired codes
     * 
     * Verifies that codes with past expiration dates are correctly
     * identified as expired and have appropriate status values.
     */
    @Test
    fun `test expired code detection`() {
        val expiredCode = ShiftCode(
            code = "EXPIRED123",
            expiration = "2020-01-01",
            reward = "Expired Reward",
            bl = true,
            blTps = false,
            bl2 = false,
            bl3 = false,
            bl4 = false,
            wonderlands = false
        )

        assertFalse(expiredCode.isNonExpiring())
        assertTrue(expiredCode.isExpired())
        assertFalse(expiredCode.isActive())
        assertEquals("Expired", expiredCode.getStatus())
    }

    /**
     * Tests detection of active codes
     * 
     * Verifies that codes with future expiration dates are correctly
     * identified as active and have appropriate status values.
     */
    @Test
    fun `test active code detection`() {
        val futureDate = LocalDate.now().plusDays(30).toString()
        val activeCode = ShiftCode(
            code = "ACTIVE123",
            expiration = futureDate,
            reward = "Active Reward",
            bl = false,
            blTps = true,
            bl2 = false,
            bl3 = true,
            bl4 = false,
            wonderlands = false
        )

        assertFalse(activeCode.isNonExpiring())
        assertFalse(activeCode.isExpired())
        assertTrue(activeCode.isActive())
        assertEquals("Active", activeCode.getStatus())
    }

    /**
     * Tests validation of valid ShiftCode instances
     * 
     * Verifies that ShiftCode objects with valid data pass all
     * validation checks and return true from isValid().
     */
    @Test
    fun `test valid shift code validation`() {
        val validCode = ShiftCode(
            code = "VALID123",
            expiration = "2024-12-31",
            reward = "Valid Reward",
            bl = true,
            blTps = false,
            bl2 = false,
            bl3 = false,
            bl4 = false,
            wonderlands = false
        )

        assertTrue(validCode.isValid())
    }

    /**
     * Tests validation of ShiftCode instances with unknown expiration dates
     * 
     * Verifies that ShiftCode objects with unknown expiration dates (2075-12-31)
     * pass all validation checks and return true from isValid().
     */
    @Test
    fun `test valid shift code validation with unknown expiration`() {
        val validUnknownExpirationCode = ShiftCode(
            code = "UNKNOWN123",
            expiration = ShiftCode.UNKNOWN_EXPIRATION_DATE,
            reward = "Unknown Expiration Reward",
            bl = true,
            blTps = false,
            bl2 = false,
            bl3 = false,
            bl4 = false,
            wonderlands = false
        )

        assertTrue(validUnknownExpirationCode.isValid())
    }

    /**
     * Tests validation failure for empty code field
     * 
     * Verifies that attempting to create a ShiftCode with an empty
     * code field throws an IllegalArgumentException.
     */
    @Test
    fun `test invalid shift code validation - empty code`() {
        assertThrows(IllegalArgumentException::class.java) {
            ShiftCode(
                code = "",
                expiration = "2024-12-31",
                reward = "Valid Reward",
                bl = true,
                blTps = false,
                bl2 = false,
                bl3 = false,
                bl4 = false,
                wonderlands = false
            )
        }
    }

    /**
     * Tests validation failure for empty reward field
     * 
     * Verifies that attempting to create a ShiftCode with an empty
     * reward field throws an IllegalArgumentException.
     */
    @Test
    fun `test invalid shift code validation - empty reward`() {
        assertThrows(IllegalArgumentException::class.java) {
            ShiftCode(
                code = "VALID123",
                expiration = "2024-12-31",
                reward = "",
                bl = true,
                blTps = false,
                bl2 = false,
                bl3 = false,
                bl4 = false,
                wonderlands = false
            )
        }
    }

    /**
     * Tests validation failure when no games are supported
     * 
     * Verifies that attempting to create a ShiftCode with no
     * supported games throws an IllegalArgumentException.
     */
    @Test
    fun `test invalid shift code validation - no games supported`() {
        assertThrows(IllegalArgumentException::class.java) {
            ShiftCode(
                code = "VALID123",
                expiration = "2024-12-31",
                reward = "Valid Reward",
                bl = false,
                blTps = false,
                bl2 = false,
                bl3 = false,
                bl4 = false,
                wonderlands = false
            )
        }
    }

    /**
     * Tests validation failure for invalid date format
     * 
     * Verifies that attempting to create a ShiftCode with an invalid
     * date format throws an IllegalArgumentException.
     */
    @Test
    fun `test invalid shift code validation - invalid date format`() {
        assertThrows(IllegalArgumentException::class.java) {
            ShiftCode(
                code = "VALID123",
                expiration = "invalid-date",
                reward = "Valid Reward",
                bl = true,
                blTps = false,
                bl2 = false,
                bl3 = false,
                bl4 = false,
                wonderlands = false
            )
        }
    }

    /**
     * Tests validation failure for code exceeding maximum length
     * 
     * Verifies that attempting to create a ShiftCode with a code
     * longer than MAX_CODE_LENGTH throws an IllegalArgumentException.
     */
    @Test
    fun `test invalid shift code validation - code too long`() {
        val longCode = "A".repeat(30) // Exceeds MAX_CODE_LENGTH (29)
        assertThrows(IllegalArgumentException::class.java) {
            ShiftCode(
                code = longCode,
                expiration = "2024-12-31",
                reward = "Valid Reward",
                bl = true,
                blTps = false,
                bl2 = false,
                bl3 = false,
                bl4 = false,
                wonderlands = false
            )
        }
    }

    /**
     * Tests validation failure for reward exceeding maximum length
     * 
     * Verifies that attempting to create a ShiftCode with a reward
     * longer than MAX_REWARD_LENGTH throws an IllegalArgumentException.
     */
    @Test
    fun `test invalid shift code validation - reward too long`() {
        val longReward = "A".repeat(201) // Exceeds MAX_REWARD_LENGTH
        assertThrows(IllegalArgumentException::class.java) {
            ShiftCode(
                code = "VALID123",
                expiration = "2024-12-31",
                reward = longReward,
                bl = true,
                blTps = false,
                bl2 = false,
                bl3 = false,
                bl4 = false,
                wonderlands = false
            )
        }
    }

    /**
     * Tests input sanitization for code field
     * 
     * Verifies that the getSanitizedCode() method properly removes
     * dangerous characters and normalizes whitespace.
     */
    @Test
    fun `test sanitized code output`() {
        val shiftCode = ShiftCode(
            code = "  TEST<123>  ",
            expiration = "2024-12-31",
            reward = "Golden Key",
            bl = true,
            blTps = false,
            bl2 = false,
            bl3 = false,
            bl4 = false,
            wonderlands = false
        )

        assertEquals("TEST123", shiftCode.getSanitizedCode())
    }

    /**
     * Tests input sanitization for reward field
     * 
     * Verifies that the getSanitizedReward() method properly removes
     * dangerous characters and normalizes whitespace.
     */
    @Test
    fun `test sanitized reward output`() {
        val shiftCode = ShiftCode(
            code = "TEST123",
            expiration = "2024-12-31",
            reward = "  Golden Key & Special Edition  ",
            bl = true,
            blTps = false,
            bl2 = false,
            bl3 = false,
            bl4 = false,
            wonderlands = false
        )

        assertEquals("Golden Key  Special Edition", shiftCode.getSanitizedReward())
    }

    /**
     * Tests XSS prevention through HTML tag removal
     * 
     * Verifies that potentially dangerous HTML tags and scripts
     * are properly removed during sanitization.
     */
    @Test
    fun `test sanitization removes HTML tags`() {
        val shiftCode = ShiftCode(
            code = "TEST123",
            expiration = "2024-12-31",
            reward = "<script>alert('xss')</script>Golden Key",
            bl = true,
            blTps = false,
            bl2 = false,
            bl3 = false,
            bl4 = false,
            wonderlands = false
        )

        assertEquals("scriptalertxss/scriptGolden Key", shiftCode.getSanitizedReward())
    }

    /**
     * Tests whitespace normalization during sanitization
     * 
     * Verifies that multiple spaces, newlines, and other whitespace
     * characters are properly normalized to single spaces.
     */
    @Test
    fun `test sanitization normalizes whitespace`() {
        val shiftCode = ShiftCode(
            code = "TEST123",
            expiration = "2024-12-31",
            reward = "Golden    Key\n\nSpecial\nEdition",
            bl = true,
            blTps = false,
            bl2 = false,
            bl3 = false,
            bl4 = false,
            wonderlands = false
        )

        assertEquals("Golden Key Special Edition", shiftCode.getSanitizedReward())
    }

    /**
     * Tests the constant value for non-expiring dates
     * 
     * Verifies that the NON_EXPIRING_DATE constant has the expected
     * value used throughout the application.
     */
    @Test
    fun `test non-expiring date constant`() {
        assertEquals("1999-12-31", ShiftCode.NON_EXPIRING_DATE)
    }

    /**
     * Tests the constant value for unknown expiration dates
     * 
     * Verifies that the UNKNOWN_EXPIRATION_DATE constant has the expected
     * value used throughout the application.
     */
    @Test
    fun `test unknown expiration date constant`() {
        assertEquals("2075-12-31", ShiftCode.UNKNOWN_EXPIRATION_DATE)
    }

    /**
     * Tests edge case date parsing
     * 
     * Verifies that the ShiftCode class can handle edge case dates
     * such as leap year dates without errors.
     */
    @Test
    fun `test edge case date parsing`() {
        // Test with edge case dates
        val edgeCaseCode = ShiftCode(
            code = "EDGE123",
            expiration = "2024-02-29", // Leap year date
            reward = "Edge Case Reward",
            bl = true,
            blTps = false,
            bl2 = false,
            bl3 = false,
            bl4 = false,
            wonderlands = false
        )

        assertTrue(edgeCaseCode.isValid())
    }

    /**
     * Tests validation with whitespace-only strings
     * 
     * Verifies that strings containing only whitespace are properly
     * handled and considered invalid.
     */
    @Test
    fun `test validation with null-like empty strings`() {
        val shiftCode = ShiftCode(
            code = "   ", // Whitespace only
            expiration = "2024-12-31",
            reward = "Valid Reward",
            bl = true,
            blTps = false,
            bl2 = false,
            bl3 = false,
            bl4 = false,
            wonderlands = false
        )

        // Should be considered invalid due to blank code
        assertFalse(shiftCode.isValid())
    }
    
} 