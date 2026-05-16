package com.brianmoler.borderlandsshiftcodes.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShiftCodeExpirationTest {

    @Test
    fun parseIngestedAtUtcMillis_parsesIsoOffset() {
        val ms = ShiftCodeExpiration.parseIngestedAtUtcMillis("2024-06-15T18:30:00+00:00")
        assertTrue(ms > 0L)
        assertTrue(ms != ShiftCodeExpiration.INGEST_SORT_UNKNOWN)
    }

    @Test
    fun parseIngestedAtUtcMillis_parsesSpaceSeparatedLikePostgres() {
        val iso = ShiftCodeExpiration.parseIngestedAtUtcMillis("2024-06-15T18:30:00+00:00")
        val spaced = ShiftCodeExpiration.parseIngestedAtUtcMillis("2024-06-15 18:30:00+00:00")
        assertEquals(iso, spaced)
    }

    @Test
    fun parseIngestedAtUtcMillis_placeholderDateIsUnknown() {
        assertEquals(
            ShiftCodeExpiration.INGEST_SORT_UNKNOWN,
            ShiftCodeExpiration.parseIngestedAtUtcMillis("1999-12-31T00:00:00Z")
        )
    }

    @Test
    fun dashboardListComparator_sortsByIngestWithinSameExpiration() {
        val exp = "2030-12-31"
        val older = TestShiftCodeFactory.entity(
            code = "AAAAA-AAAAA-AAAAA-AAAAA-AAAAA",
            expiration = exp,
            reward = "R1",
            bl = true
        ).copy(ingestedAtUtcMillis = 1_700_000_000_000L)
        val newer = older.copy(
            code = "BBBBB-BBBBB-BBBBB-BBBBB-BBBBB",
            ingestedAtUtcMillis = 1_800_000_000_000L
        )
        val sorted = listOf(older, newer).sortedLikeDashboard()
        assertEquals(newer.code, sorted[0].code)
        assertEquals(older.code, sorted[1].code)
    }

    @Test
    fun dashboardListComparator_unknownIngestSortsAfterKnownForSameExpiration() {
        val exp = "2030-12-31"
        val known = TestShiftCodeFactory.entity(
            code = "AAAAA-AAAAA-AAAAA-AAAAA-AAAAA",
            expiration = exp,
            reward = "R1",
            bl = true
        ).copy(ingestedAtUtcMillis = 1_700_000_000_000L)
        val unknown = known.copy(
            code = "BBBBB-BBBBB-BBBBB-BBBBB-BBBBB",
            ingestedAtUtcMillis = ShiftCodeExpiration.INGEST_SORT_UNKNOWN
        )
        val sorted = listOf(unknown, known).sortedLikeDashboard()
        assertEquals(known.code, sorted[0].code)
        assertEquals(unknown.code, sorted[1].code)
    }
}
