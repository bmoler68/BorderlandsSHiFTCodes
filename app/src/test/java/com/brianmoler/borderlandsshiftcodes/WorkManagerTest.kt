package com.brianmoler.borderlandsshiftcodes

import com.brianmoler.borderlandsshiftcodes.data.ShiftCode
import org.junit.Test
import org.junit.Assert.*

class WorkManagerTest {
    
    @Test
    fun testShiftCodeCreation() {
        // Test that ShiftCode can be created with valid data
        val testCode = ShiftCode(
            code = "TEST123456789",
            expiration = "2025-12-31",
            reward = "Test Reward",
            bl = true,
            blTps = false,
            bl2 = true,
            bl3 = false,
            bl4 = false,
            wonderlands = false
        )
        
        assertNotNull(testCode)
        assertEquals("TEST123456789", testCode.code)
        assertEquals("2025-12-31", testCode.expiration)
        assertEquals("Test Reward", testCode.reward)
        assertTrue(testCode.bl)
        assertFalse(testCode.blTps)
        assertTrue(testCode.bl2)
        assertFalse(testCode.bl3)
        assertFalse(testCode.bl4)
        assertFalse(testCode.wonderlands)
    }
    
    @Test
    fun testShiftCodeValidation() {
        // Test that ShiftCode validates correctly
        val testCode = ShiftCode(
            code = "VALID123456789",
            expiration = "2025-12-31",
            reward = "Valid Reward",
            bl = true,
            blTps = false,
            bl2 = true,
            bl3 = false,
            bl4 = false,
            wonderlands = false
        )
        
        assertTrue(testCode.isValid())
        assertFalse(testCode.isExpired())
        assertFalse(testCode.isNonExpiring())
        assertEquals("Active", testCode.getStatus())
    }
    
    @Test
    fun testShiftCodeExpiration() {
        // Test expiration logic
        val expiredCode = ShiftCode(
            code = "EXPIRED123456789",
            expiration = "2020-01-01",
            reward = "Expired Reward",
            bl = true,
            blTps = false,
            bl2 = false,
            bl3 = false,
            bl4 = false,
            wonderlands = false
        )
        
        val nonExpiringCode = ShiftCode(
            code = "NEVER123456789",
            expiration = "1999-12-31",
            reward = "Non-expiring Reward",
            bl = true,
            blTps = false,
            bl2 = false,
            bl3 = false,
            bl4 = false,
            wonderlands = false
        )
        
        assertTrue(expiredCode.isExpired())
        assertTrue(nonExpiringCode.isNonExpiring())
        assertEquals("Expired", expiredCode.getStatus())
        assertEquals("Non-expiring", nonExpiringCode.getStatus())
    }
    
    @Test
    fun testShiftCodeSanitization() {
        // Test sanitization methods
        val shiftCode = ShiftCode(
            code = "SANITIZE123456789",
            expiration = "2025-12-31",
            reward = "Sanitization Test",
            bl = true,
            blTps = false,
            bl2 = true,
            bl3 = false,
            bl4 = false,
            wonderlands = false
        )
        
        val sanitizedCode = shiftCode.getSanitizedCode()
        val sanitizedReward = shiftCode.getSanitizedReward()
        
        assertNotNull(sanitizedCode)
        assertNotNull(sanitizedReward)
        assertEquals(shiftCode.code, sanitizedCode)
        assertEquals(shiftCode.reward, sanitizedReward)
    }
}
