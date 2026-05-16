package com.brianmoler.borderlandsshiftcodes.data

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Shared expiration / ingest semantics aligned with Supabase `borderlands_shift.shift_codes_current`
 * and the web dashboard.
 */
object ShiftCodeExpiration {
    const val NON_EXPIRING_DATE = "1999-12-31"
    const val UNKNOWN_EXPIRATION_DATE = "2075-12-31"
    const val INGEST_SORT_UNKNOWN = Long.MIN_VALUE

    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /** Milliseconds at noon UTC for list sort (matches dashboard `expirationDate`). */
    fun sortExpirationMillis(
        isNonExpiring: Boolean,
        isUnknownExpiration: Boolean,
        expirationDate: String?
    ): Long =
        when {
            isUnknownExpiration -> dateAtNoonUtcMillis(UNKNOWN_EXPIRATION_DATE)
            isNonExpiring -> dateAtNoonUtcMillis(NON_EXPIRING_DATE)
            !expirationDate.isNullOrBlank() -> dateAtNoonUtcMillis(expirationDate)
            else -> 0L
        }

    fun isValidExpirationDate(date: String): Boolean =
        try {
            LocalDate.parse(date, DATE_FORMATTER)
            true
        } catch (_: Exception) {
            false
        }

    /**
     * Normalizes PostgREST / Supabase `expiration_date` values to `yyyy-MM-dd`.
     * Accepts plain dates and ISO-8601 strings (e.g. `2030-12-31T00:00:00+00:00`).
     */
    fun normalizeExpirationDateString(raw: String): String? {
        val s = raw.trim()
        if (s.isEmpty()) return null
        if (isValidExpirationDate(s)) return s
        if (s.length >= 10) {
            val datePart = s.substring(0, 10)
            if (isValidExpirationDate(datePart)) return datePart
        }
        return null
    }

    fun fromLegacyExpirationColumn(expiration: String): LegacyExpirationFields =
        when (expiration) {
            NON_EXPIRING_DATE ->
                LegacyExpirationFields(
                    isNonExpiring = true,
                    isUnknownExpiration = false,
                    expirationDate = null
                )
            UNKNOWN_EXPIRATION_DATE ->
                LegacyExpirationFields(
                    isNonExpiring = false,
                    isUnknownExpiration = true,
                    expirationDate = null
                )
            else ->
                LegacyExpirationFields(
                    isNonExpiring = false,
                    isUnknownExpiration = false,
                    expirationDate = expiration
                )
        }

    /**
     * Secondary sort key for catalog list ordering.
     * Unknown / placeholder ingest sorts last among rows with the same expiration.
     */
    fun ingestSortKeyMillis(ingestedAtUtcMillis: Long): Long =
        if (ingestedAtUtcMillis == INGEST_SORT_UNKNOWN) INGEST_SORT_UNKNOWN else ingestedAtUtcMillis

    /**
     * Catalog list order: expiration desc, ingest desc, code asc.
     */
    fun catalogListComparator(): Comparator<ShiftCodeEntity> =
        compareByDescending<ShiftCodeEntity> { it.sortExpirationMillis() }
            .thenByDescending { ingestSortKeyMillis(it.ingestedAtUtcMillis) }
            .thenBy { it.code }

    /**
     * Parses `ingested_at_utc` for persistence and sorting.
     * Placeholder ingest on 1999-12-31 UTC is stored as [INGEST_SORT_UNKNOWN].
     */
    fun parseIngestedAtUtcMillis(iso8601OrEmpty: String): Long {
        val s = iso8601OrEmpty.trim()
        if (s.isEmpty()) return INGEST_SORT_UNKNOWN
        val instant = parseIngestedInstantLenient(s) ?: return INGEST_SORT_UNKNOWN
        val utcDay = instant.atZone(ZoneOffset.UTC).toLocalDate()
        if (utcDay == LocalDate.of(1999, 12, 31)) return INGEST_SORT_UNKNOWN
        return instant.toEpochMilli()
    }

    /**
     * Reads `ingested_at_utc` from a PostgREST JSON row (string or, rarely, numeric epoch).
     */
    fun parseIngestedAtUtcMillisFromJson(value: Any?): Long =
        when (value) {
            null -> INGEST_SORT_UNKNOWN
            is String -> parseIngestedAtUtcMillis(value)
            is Number -> epochMillisFromJsonNumber(value)
            else -> INGEST_SORT_UNKNOWN
        }

    private fun parseIngestedInstantLenient(raw: String): Instant? {
        val candidates =
            listOf(
                raw,
                raw.replace(' ', 'T'),
                if (raw.endsWith("+00")) "${raw.replace(' ', 'T')}:00" else raw
            ).distinct()
        for (candidate in candidates) {
            try {
                return Instant.parse(candidate)
            } catch (_: Exception) {
                // try next
            }
            try {
                return OffsetDateTime.parse(candidate, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()
            } catch (_: Exception) {
                // try next
            }
            try {
                return LocalDateTime.parse(candidate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .atZone(ZoneOffset.UTC)
                    .toInstant()
            } catch (_: Exception) {
                // try next
            }
        }
        return null
    }

    private fun epochMillisFromJsonNumber(value: Number): Long {
        val n = value.toLong()
        // Values below ~year 5138 in ms are treated as Unix seconds (PostgREST edge cases).
        return if (n in 1..99_999_999_999L) n * 1000 else n
    }

    private fun dateAtNoonUtcMillis(dateStr: String): Long =
        LocalDate.parse(dateStr, DATE_FORMATTER)
            .atTime(12, 0)
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
}

data class LegacyExpirationFields(
    val isNonExpiring: Boolean,
    val isUnknownExpiration: Boolean,
    val expirationDate: String?
)

/** Applies [ShiftCodeExpiration.catalogListComparator] to this list. */
fun List<ShiftCodeEntity>.sortedForCatalog(): List<ShiftCodeEntity> =
    sortedWith(ShiftCodeExpiration.catalogListComparator())
