package com.brianmoler.borderlandsshiftcodes.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.brianmoler.borderlandsshiftcodes.config.AppConfig
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Room entity aligned with Supabase `shift_codes_current` expiration semantics.
 */
@Entity(
    tableName = "shift_codes",
    indices = [Index(value = ["code"], unique = true)]
)
data class ShiftCodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val reward: String,
    /** Real calendar expiration (`expiration_date`); null when a semantic flag is set. */
    val expirationDate: String? = null,
    val expirationTime: String = "",
    val isNonExpiring: Boolean = false,
    val isUnknownExpiration: Boolean = false,
    val bl: Boolean,
    val blTps: Boolean,
    val bl2: Boolean,
    val bl3: Boolean,
    val bl4: Boolean,
    val wonderlands: Boolean,
    val isKey: Boolean = false,
    val isCosmetic: Boolean = false,
    val isGear: Boolean = false,
    val isDeleted: Boolean = false,
    val isRedeemed: Boolean = false,
    val ingestedAtUtcMillis: Long = ShiftCodeExpiration.INGEST_SORT_UNKNOWN,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        const val NON_EXPIRING_DATE = ShiftCodeExpiration.NON_EXPIRING_DATE
        const val UNKNOWN_EXPIRATION_DATE = ShiftCodeExpiration.UNKNOWN_EXPIRATION_DATE

        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        private const val DEFAULT_EXPIRATION_TIME = "23:59"
    }

    init {
        require(code.isNotBlank()) { "Code cannot be blank" }
        require(code.length <= AppConfig.Validation.MAX_CODE_LENGTH) { "Code too long" }
        require(reward.isNotBlank()) { "Reward cannot be blank" }
        require(reward.length <= AppConfig.Validation.MAX_REWARD_LENGTH) { "Reward too long" }
        require(!(isNonExpiring && isUnknownExpiration)) { "Expiration flags are mutually exclusive" }
        if (!isNonExpiring && !isUnknownExpiration) {
            require(!expirationDate.isNullOrBlank()) { "expirationDate required when no semantic flag is set" }
            require(ShiftCodeExpiration.isValidExpirationDate(expirationDate!!)) { "Invalid expirationDate" }
        }
        require(bl || blTps || bl2 || bl3 || bl4 || wonderlands) { "At least one game must be supported" }
    }

    fun sortExpirationMillis(): Long =
        ShiftCodeExpiration.sortExpirationMillis(isNonExpiring, isUnknownExpiration, expirationDate)

    fun isExpired(): Boolean {
        if (isNonExpiring || isUnknownExpiration) return false
        val date = expirationDate ?: return false

        return try {
            val easternTimeZone = ZoneId.of("America/New_York")
            val nowInEastern = ZonedDateTime.now(easternTimeZone)
            val expirationLocalDate = LocalDate.parse(date, DATE_FORMATTER)
            val timeString = if (expirationTime.isBlank()) DEFAULT_EXPIRATION_TIME else expirationTime.trim()
            val (hour, minute, second) = parseTimeString(timeString)
            val expirationDateTimeEastern =
                expirationLocalDate.atTime(hour, minute, second).atZone(easternTimeZone)
            nowInEastern.isAfter(expirationDateTimeEastern)
        } catch (_: Exception) {
            false
        }
    }

    private fun parseTimeString(timeString: String): Triple<Int, Int, Int> {
        val trimmed = timeString.trim().uppercase()
        val hasAM = trimmed.endsWith(" AM") || trimmed.endsWith("AM")
        val hasPM = trimmed.endsWith(" PM") || trimmed.endsWith("PM")

        if (hasAM || hasPM) {
            val timeWithoutSuffix =
                if (trimmed.endsWith(" AM") || trimmed.endsWith(" PM")) {
                    trimmed.substring(0, trimmed.length - 3).trim()
                } else {
                    trimmed.substring(0, trimmed.length - 2).trim()
                }
            val timeParts = timeWithoutSuffix.split(":")
            if (timeParts.size >= 2) {
                val hour12 = timeParts[0].toIntOrNull() ?: return Triple(23, 59, 59)
                val minute = timeParts[1].toIntOrNull() ?: return Triple(23, 59, 59)
                val second = timeParts.getOrNull(2)?.toIntOrNull() ?: 0
                if (hour12 !in 1..12 || minute !in 0..59 || second !in 0..59) return Triple(23, 59, 59)
                val hour24 =
                    when {
                        hasAM && hour12 == 12 -> 0
                        hasAM && hour12 < 12 -> hour12
                        hasPM && hour12 == 12 -> 12
                        hasPM && hour12 < 12 -> hour12 + 12
                        else -> hour12
                    }
                return Triple(hour24, minute, second)
            }
        }

        val timeParts = trimmed.split(":")
        if (timeParts.size >= 2) {
            val hour = timeParts[0].toIntOrNull() ?: 23
            val minute = timeParts[1].toIntOrNull() ?: 59
            val second = timeParts.getOrNull(2)?.toIntOrNull() ?: 0
            if (hour in 0..23 && minute in 0..59 && second in 0..59) {
                return Triple(hour, minute, second)
            }
        }
        return Triple(23, 59, 59)
    }

    fun getStatus(): String =
        when {
            isNonExpiring -> "Non-Expiring"
            isExpired() -> "Expired"
            else -> "Active"
        }

    fun getDisplayExpiration(): String =
        when {
            isNonExpiring -> "Never"
            isUnknownExpiration -> "Unknown"
            expirationDate != null -> expirationDate
            else -> "Unknown"
        }

    fun getDisplayTime(): String {
        if (expirationTime.isNotBlank()) {
            return "${formatTimeForDisplay(expirationTime)} ET"
        }
        return ""
    }

    private fun formatTimeForDisplay(timeString: String): String {
        val trimmed = timeString.trim()
        val upper = trimmed.uppercase()
        val hasAM = upper.endsWith(" AM") || upper.endsWith("AM")
        val hasPM = upper.endsWith(" PM") || upper.endsWith("PM")

        if (hasAM || hasPM) {
            val timeWithoutSuffix =
                if (upper.endsWith(" AM") || upper.endsWith(" PM")) {
                    trimmed.substring(0, trimmed.length - 3).trim()
                } else {
                    trimmed.substring(0, trimmed.length - 2).trim()
                }
            val parts = timeWithoutSuffix.split(":")
            if (parts.size >= 2) {
                val ampm = if (hasAM) "AM" else "PM"
                return "${parts[0]}:${parts[1]} $ampm"
            }
        }

        val parts = trimmed.split(":")
        if (parts.size >= 2) {
            return "${parts[0]}:${parts[1]}"
        }
        return trimmed
    }

    fun isValid(): Boolean =
        try {
            code.isNotBlank() &&
                code.length <= AppConfig.Validation.MAX_CODE_LENGTH &&
                reward.isNotBlank() &&
                reward.length <= AppConfig.Validation.MAX_REWARD_LENGTH &&
                (isNonExpiring || isUnknownExpiration || (!expirationDate.isNullOrBlank() &&
                    ShiftCodeExpiration.isValidExpirationDate(expirationDate))) &&
                (bl || blTps || bl2 || bl3 || bl4 || wonderlands)
        } catch (_: Exception) {
            false
        }

    private fun sanitizeString(input: String): String =
        input.trim()
            .replace(Regex("[<>\"'&]"), "")
            .replace(Regex("\\s+"), " ")

    fun getSanitizedCode(): String = sanitizeString(code)

    fun getSanitizedReward(): String = sanitizeString(reward)

    fun hasChanged(remoteCode: ShiftCode): Boolean =
        code != remoteCode.code ||
            expirationDate != remoteCode.expirationDate ||
            expirationTime != remoteCode.expirationTime ||
            isNonExpiring != remoteCode.isNonExpiring ||
            isUnknownExpiration != remoteCode.isUnknownExpiration ||
            reward != remoteCode.reward ||
            bl != remoteCode.bl ||
            blTps != remoteCode.blTps ||
            bl2 != remoteCode.bl2 ||
            bl3 != remoteCode.bl3 ||
            bl4 != remoteCode.bl4 ||
            wonderlands != remoteCode.wonderlands ||
            isKey != remoteCode.isKey ||
            isCosmetic != remoteCode.isCosmetic ||
            isGear != remoteCode.isGear ||
            ingestedAtUtcMillis != remoteCode.ingestedAtUtcMillis

    fun updateFromRemote(remoteCode: ShiftCode): ShiftCodeEntity =
        copy(
            code = remoteCode.code,
            expirationDate = remoteCode.expirationDate,
            expirationTime = remoteCode.expirationTime,
            isNonExpiring = remoteCode.isNonExpiring,
            isUnknownExpiration = remoteCode.isUnknownExpiration,
            reward = remoteCode.reward,
            bl = remoteCode.bl,
            blTps = remoteCode.blTps,
            bl2 = remoteCode.bl2,
            bl3 = remoteCode.bl3,
            bl4 = remoteCode.bl4,
            wonderlands = remoteCode.wonderlands,
            isKey = remoteCode.isKey,
            isCosmetic = remoteCode.isCosmetic,
            isGear = remoteCode.isGear,
            ingestedAtUtcMillis = remoteCode.ingestedAtUtcMillis,
            lastUpdated = System.currentTimeMillis()
        )
}

fun ShiftCode.toEntity(): ShiftCodeEntity =
    ShiftCodeEntity(
        code = code,
        expirationDate = expirationDate,
        expirationTime = expirationTime,
        isNonExpiring = isNonExpiring,
        isUnknownExpiration = isUnknownExpiration,
        reward = reward,
        bl = bl,
        blTps = blTps,
        bl2 = bl2,
        bl3 = bl3,
        bl4 = bl4,
        wonderlands = wonderlands,
        isKey = isKey,
        isCosmetic = isCosmetic,
        isGear = isGear,
        isDeleted = false,
        isRedeemed = false,
        ingestedAtUtcMillis = ingestedAtUtcMillis,
        lastUpdated = System.currentTimeMillis()
    )
