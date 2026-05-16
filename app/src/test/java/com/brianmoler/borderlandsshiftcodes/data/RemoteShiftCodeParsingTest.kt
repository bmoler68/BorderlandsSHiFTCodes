package com.brianmoler.borderlandsshiftcodes.data

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class RemoteShiftCodeParsingTest {

    @Test
    fun shiftCodeFromJsonOrNull_acceptsValid29CharCode() {
        val row = testRow(code = "TST99-CURSR-TEST1-APK02-60516")
        val parsed = shiftCodeFromJsonOrNull(row)
        assertNotNull(parsed)
        assertEquals("TST99-CURSR-TEST1-APK02-60516", parsed!!.code)
    }

    @Test
    fun shiftCodeFromJsonOrNull_rejectsCodeLongerThan29Chars() {
        // Sample insert used 6-char final segment → 30 chars total; app max is 29 (5×5 + 4 hyphens).
        val row = testRow(code = "TST99-CURSR-TEST1-APK02-260516")
        assertNull(shiftCodeFromJsonOrNull(row))
    }

    @Test
    fun shiftCodeFromJsonOrNull_normalizesIsoExpirationDate() {
        val row = testRow(expirationDate = "2030-12-31T00:00:00+00:00")
        val parsed = shiftCodeFromJsonOrNull(row)
        assertNotNull(parsed)
        assertEquals("2030-12-31", parsed!!.expirationDate)
    }

    @Test
    fun normalizeExpirationDateString_handlesDateOnlyAndIso() {
        assertEquals("2030-12-31", ShiftCodeExpiration.normalizeExpirationDateString("2030-12-31"))
        assertEquals(
            "2030-12-31",
            ShiftCodeExpiration.normalizeExpirationDateString("2030-12-31T23:59:00+00:00")
        )
    }

    private fun testRow(
        code: String = "TST99-CURSR-TEST1-APK02-60516",
        expirationDate: String = "2030-12-31"
    ): JSONObject =
        JSONObject()
            .put("code", code)
            .put("reward", "[TEST] ingest/sort verification")
            .put("expiration_date", expirationDate)
            .put("expiration_time_12h", "11:59:00 PM")
            .put("is_non_expiring", "false")
            .put("is_unknown_expiration", "false")
            .put("bl", "false")
            .put("bl_tps", "false")
            .put("bl2", "false")
            .put("bl3", "false")
            .put("bl4", "true")
            .put("wonderlands", "false")
            .put("is_key", "false")
            .put("is_cosmetic", "true")
            .put("is_gear", "false")
            .put("ingested_at_utc", "2026-05-16T12:00:00+00:00")
}
