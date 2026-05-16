package com.brianmoler.borderlandsshiftcodes

import com.brianmoler.borderlandsshiftcodes.data.TestShiftCodeFactory
import org.junit.Assert.*
import org.junit.Test

class WorkManagerTest {

    @Test
    fun testShiftCodeCreation() {
        val testCode = TestShiftCodeFactory.shiftCode(
            code = "TEST123456789",
            expiration = "2099-12-31",
            reward = "Test Reward",
            bl = true,
            bl2 = true
        )

        assertNotNull(testCode)
        assertEquals("TEST123456789", testCode.code)
        assertEquals("2099-12-31", testCode.expirationDate)
        assertEquals("Test Reward", testCode.reward)
        assertTrue(testCode.bl)
        assertTrue(testCode.bl2)
    }

    @Test
    fun testShiftCodeValidation() {
        val testCode = TestShiftCodeFactory.shiftCode(
            code = "VALID123456789",
            expiration = "2099-12-31",
            reward = "Valid Reward",
            bl = true,
            bl2 = true
        )

        assertTrue(testCode.isValid())
        assertFalse(testCode.isExpired())
        assertFalse(testCode.isNonExpiring)
        assertEquals("Active", testCode.getStatus())
    }

    @Test
    fun testShiftCodeExpiration() {
        val expiredCode = TestShiftCodeFactory.shiftCode(
            code = "EXPIRED123456789",
            expiration = "2020-01-01",
            reward = "Expired Reward",
            bl = true
        )

        val nonExpiringCode = TestShiftCodeFactory.shiftCode(
            code = "NEVER123456789",
            expiration = "1999-12-31",
            reward = "Non-expiring Reward",
            bl = true
        )

        assertTrue(expiredCode.isExpired())
        assertTrue(nonExpiringCode.isNonExpiring)
        assertEquals("Expired", expiredCode.getStatus())
        assertEquals("Non-expiring", nonExpiringCode.getStatus())
    }

    @Test
    fun testShiftCodeSanitization() {
        val shiftCode = TestShiftCodeFactory.shiftCode(
            code = "SANITIZE123456789",
            expiration = "2099-12-31",
            reward = "Sanitization Test",
            bl = true,
            bl2 = true
        )

        assertEquals(shiftCode.code, shiftCode.getSanitizedCode())
        assertEquals(shiftCode.reward, shiftCode.getSanitizedReward())
    }
}
