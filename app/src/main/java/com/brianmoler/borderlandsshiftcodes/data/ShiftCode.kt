package com.brianmoler.borderlandsshiftcodes.data

import com.brianmoler.borderlandsshiftcodes.config.AppConfig
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Represents a Borderlands SHiFT code with its associated metadata.
 * 
 * @property code The actual SHiFT code string
 * @property expiration The expiration date in yyyy-MM-dd format, "1999-12-31" for non-expiring codes, or "2075-12-31" for unknown expiration codes
 * @property expirationTime The expiration time in HH:mm format (Eastern Time), empty string if not specified
 * @property reward Description of the reward for redeeming this code
 * @property bl Whether this code works for Borderlands 1
 * @property blTps Whether this code works for Borderlands: The Pre-Sequel
 * @property bl2 Whether this code works for Borderlands 2
 * @property bl3 Whether this code works for Borderlands 3
 * @property bl4 Whether this code works for Borderlands 4
 * @property wonderlands Whether this code works for Tiny Tina's Wonderlands
 * @property isKey Whether this code is for a key reward
 * @property isCosmetic Whether this code is for a cosmetic reward
 * @property isGear Whether this code is for a gear reward
 */
data class ShiftCode(
    val code: String,
    val expiration: String,
    val expirationTime: String = "",
    val reward: String,
    val bl: Boolean,
    val blTps: Boolean,
    val bl2: Boolean,
    val bl3: Boolean,
    val bl4: Boolean,
    val wonderlands: Boolean,
    val isKey: Boolean = false,
    val isCosmetic: Boolean = false,
    val isGear: Boolean = false
) {
    companion object {
        const val NON_EXPIRING_DATE = "1999-12-31"
        const val UNKNOWN_EXPIRATION_DATE = "2075-12-31"
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        private const val DEFAULT_EXPIRATION_TIME = "23:59:59" // Default to end of day if time not specified
        
        /**
         * Sanitizes input string by removing potentially dangerous characters
         * @param input The input string to sanitize
         * @return Sanitized string
         */
        private fun sanitizeString(input: String): String {
            return input.trim()
                .replace(Regex("[<>\"'&]"), "") // Remove HTML/XML special characters
                .replace(Regex("\\s+"), " ") // Normalize whitespace
        }
        
        /**
         * Validates date format
         * @param date The date string to validate
         * @return true if valid, false otherwise
         */
        private fun isValidDateFormat(date: String): Boolean {
            return try {
                if (date == NON_EXPIRING_DATE || date == UNKNOWN_EXPIRATION_DATE) return true
                LocalDate.parse(date, DATE_FORMATTER)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    init {
        // Validate and sanitize inputs during construction
        require(code.isNotBlank()) { "Code cannot be blank" }
        require(code.length <= AppConfig.Validation.MAX_CODE_LENGTH) { "Code too long" }
        require(reward.isNotBlank()) { "Reward cannot be blank" }
        require(reward.length <= AppConfig.Validation.MAX_REWARD_LENGTH) { "Reward too long" }
        require(isValidDateFormat(expiration)) { "Invalid date format" }
        require(bl || blTps || bl2 || bl3 || bl4 || wonderlands) { "At least one game must be supported" }
    }

    /**
     * Checks if this code is expired.
     * Expiration is calculated based on both date and time in Eastern Time (ET).
     * @return true if the code is expired, false otherwise
     */
    fun isExpired(): Boolean {
        if (expiration == NON_EXPIRING_DATE || expiration == UNKNOWN_EXPIRATION_DATE) return false
        
        return try {
            val easternTimeZone = ZoneId.of("America/New_York")
            val nowInEastern = ZonedDateTime.now(easternTimeZone)
            
            // Parse expiration date
            val expirationDate = LocalDate.parse(expiration, DATE_FORMATTER)
            
            // Parse expiration time, defaulting to end of day if not specified
            val timeString = if (expirationTime.isBlank()) DEFAULT_EXPIRATION_TIME else expirationTime.trim()
            val (hour, minute, second) = parseTimeString(timeString)
            
            // Create expiration datetime in Eastern Time
            val expirationDateTimeEastern = expirationDate.atTime(hour, minute, second).atZone(easternTimeZone)
            
            return nowInEastern.isAfter(expirationDateTimeEastern)
        } catch (e: Exception) {
            // If we can't parse the date/time, assume it's not expired
            false
        }
    }

    /**
     * Parses a time string in format HH:MM:SS AM/PM into hour, minute, and second components.
     * Supports both 12-hour format with AM/PM (e.g., "12:11:00 AM", "01:30:00 PM") 
     * and 24-hour format (e.g., "23:59:59").
     * 
     * @param timeString Time string to parse (e.g., "12:11:00 AM", "01:30:00 PM", "23:59:59")
     * @return Triple of (hour, minute, second) in 24-hour format
     */
    private fun parseTimeString(timeString: String): Triple<Int, Int, Int> {
        val trimmed = timeString.trim().uppercase()
        
        // Check for AM/PM format
        val hasAM = trimmed.endsWith(" AM") || trimmed.endsWith("AM")
        val hasPM = trimmed.endsWith(" PM") || trimmed.endsWith("PM")
        
        if (hasAM || hasPM) {
            // Remove AM/PM suffix
            val timeWithoutSuffix = if (trimmed.endsWith(" AM") || trimmed.endsWith(" PM")) {
                trimmed.substring(0, trimmed.length - 3).trim()
            } else {
                trimmed.substring(0, trimmed.length - 2).trim()
            }
            
            val timeParts = timeWithoutSuffix.split(":")
            
            if (timeParts.size >= 2) {
                val hour12 = timeParts[0].toIntOrNull() ?: return Triple(23, 59, 59)
                val minute = timeParts[1].toIntOrNull() ?: return Triple(23, 59, 59)
                val second = timeParts.getOrNull(2)?.toIntOrNull() ?: 0
                
                // Validate ranges
                if (hour12 < 1 || hour12 > 12) return Triple(23, 59, 59)
                if (minute < 0 || minute > 59) return Triple(23, 59, 59)
                if (second < 0 || second > 59) return Triple(23, 59, 59)
                
                // Convert 12-hour to 24-hour format
                val hour24 = when {
                    hasAM && hour12 == 12 -> 0  // 12:xx:xx AM = 00:xx:xx
                    hasAM && hour12 < 12 -> hour12  // 1-11:xx:xx AM = 01-11:xx:xx
                    hasPM && hour12 == 12 -> 12  // 12:xx:xx PM = 12:xx:xx
                    hasPM && hour12 < 12 -> hour12 + 12  // 1-11:xx:xx PM = 13-23:xx:xx
                    else -> hour12
                }
                
                return Triple(hour24, minute, second)
            }
        }
        
        // Try 24-hour format (HH:MM:SS or HH:MM)
        val timeParts = trimmed.split(":")
        if (timeParts.size >= 2) {
            val hour = timeParts[0].toIntOrNull() ?: 23
            val minute = timeParts[1].toIntOrNull() ?: 59
            val second = timeParts.getOrNull(2)?.toIntOrNull() ?: 0
            
            // Validate ranges for 24-hour format
            if (hour in 0..23 && minute in 0..59 && second in 0..59) {
                return Triple(hour, minute, second)
            }
        }
        
        // Default to end of day if parsing fails
        return Triple(23, 59, 59)
    }

    /**
     * Checks if this code is non-expiring.
     * @return true if the code never expires, false otherwise
     */
    fun isNonExpiring(): Boolean = expiration == NON_EXPIRING_DATE

    /**
     * Checks if this code is currently active (not expired and not non-expiring).
     * @return true if the code is active, false otherwise
     */
    fun isActive(): Boolean = !isExpired() && !isNonExpiring()

    /**
     * Gets the status of this code.
     * @return the current status as a string
     */
    fun getStatus(): String = when {
        isNonExpiring() -> "Non-expiring"
        isExpired() -> "Expired"
        else -> "Active"
    }

    /**
     * Gets the display text for the expiration date.
     * @return "Never" for non-expiring codes, "Unknown" for unknown expiration codes, or the actual date
     */
    fun getDisplayExpiration(): String = when {
        isNonExpiring() -> "Never"
        expiration == UNKNOWN_EXPIRATION_DATE -> "Unknown"
        else -> expiration
    }

    /**
     * Validates that this ShiftCode has valid data.
     * @return true if the code is valid, false otherwise
     */
    fun isValid(): Boolean {
        return try {
            code.isNotBlank() && 
            code.length <= AppConfig.Validation.MAX_CODE_LENGTH &&
            expiration.isNotBlank() && 
            isValidDateFormat(expiration) &&
            reward.isNotBlank() &&
            reward.length <= AppConfig.Validation.MAX_REWARD_LENGTH &&
            (bl || blTps || bl2 || bl3 || bl4 || wonderlands) // At least one game must be supported
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Gets a sanitized version of the code for safe display
     * @return Sanitized code string
     */
    fun getSanitizedCode(): String = sanitizeString(code)
    
    /**
     * Gets a sanitized version of the reward for safe display
     * @return Sanitized reward string
     */
    fun getSanitizedReward(): String = sanitizeString(reward)
    
} 