package com.brianmoler.borderlandsshiftcodes.data

import android.content.Context
import android.util.Log
import com.brianmoler.borderlandsshiftcodes.config.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

private const val TAG = "RemoteShiftCodeRepository"
private const val MAX_RETRY_ATTEMPTS = 3
private const val RETRY_DELAY_MS = 1000L

private const val REST_SCHEMA = "borderlands_shift"
private const val REST_TABLE = "shift_codes_current"
private const val PAGE_SIZE = 500

/**
 * Fetches SHiFT codes from Supabase PostgREST (same view as the web dashboard).
 */
class RemoteShiftCodeRepository(private val context: Context) {

    private val httpClient =
        OkHttpClient.Builder()
            .connectTimeout(AppConfig.Network.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(AppConfig.Network.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(AppConfig.Network.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder =
                    original
                        .newBuilder()
                        .header("Cache-Control", "no-cache, no-store, must-revalidate")
                        .header("Pragma", "no-cache")
                        .header("Expires", "0")
                chain.proceed(requestBuilder.build())
            }
            .build()

    /**
     * Downloads all rows from [REST_TABLE] in [REST_SCHEMA] with paging.
     */
    suspend fun fetchShiftCodes(): List<ShiftCode> =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Fetching SHiFT codes from Supabase")

            val base = AppConfig.Network.SUPABASE_URL
            val anonKey = AppConfig.Network.SUPABASE_ANON_KEY

            var lastException: Exception? = null

            repeat(MAX_RETRY_ATTEMPTS) { attempt ->
                try {
                    return@withContext fetchAllPages(base, anonKey)
                } catch (e: Exception) {
                    lastException = e
                    Log.w(TAG, "Attempt ${attempt + 1} failed: ${e.message}")
                    if (attempt < MAX_RETRY_ATTEMPTS - 1) {
                        delay(RETRY_DELAY_MS * (attempt + 1))
                    }
                }
            }

            Log.e(TAG, "All retry attempts failed for Supabase fetch")
            throw lastException ?: Exception("Unknown error fetching Supabase")
        }

    private fun fetchAllPages(base: String, anonKey: String): List<ShiftCode> {
        val collected = mutableListOf<ShiftCode>()
        var offset = 0

        while (true) {
            val url =
                "$base/rest/v1/$REST_TABLE" +
                    "?select=*&order=code.asc&limit=$PAGE_SIZE&offset=$offset"

            val request =
                Request.Builder()
                    .url(url)
                    .header("apikey", anonKey)
                    .header("Authorization", "Bearer $anonKey")
                    .header("Accept", "application/json")
                    .header("Accept-Profile", REST_SCHEMA)
                    .header("User-Agent", AppConfig.Network.getUserAgent(context))
                    .get()
                    .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string().orEmpty().take(512)
                    throw NetworkException(
                        "Supabase REST ${response.code}: ${errBody.ifBlank { response.message }}"
                            .take(1024),
                        response.code
                    )
                }
                val raw = response.body?.string().orEmpty()
                val chunk = JSONArray(raw)
                val n = chunk.length()
                if (n == 0) {
                    Log.d(TAG, "Parsed ${collected.size} SHiFT code(s) from Supabase")
                    return collected
                }
                repeat(n) { i ->
                    val row = chunk.optJSONObject(i) ?: return@repeat
                    val code = shiftCodeFromJsonOrNull(row)
                    if (code != null) {
                        collected += code
                    }
                }
                if (n < PAGE_SIZE) {
                    Log.d(TAG, "Parsed ${collected.size} SHiFT code(s) from Supabase")
                    return collected
                }
                offset += PAGE_SIZE
            }
        }
    }
}

/** Maps one PostgREST row to [ShiftCode], or null when the row cannot be modeled. */
internal fun shiftCodeFromJsonOrNull(row: JSONObject): ShiftCode? {
    try {
        val isNonExpiring =
            row.optBoolean("is_non_expiring") ||
                "t".equals(row.optString("is_non_expiring"), ignoreCase = true)
        val isUnknownExpiration =
            row.optBoolean("is_unknown_expiration") ||
                "t".equals(row.optString("is_unknown_expiration"), ignoreCase = true)

        if (isNonExpiring && isUnknownExpiration) {
            Log.w(TAG, "Skipping row: both expiration flags set for ${row.optString("code")}")
            return null
        }

        val codeStr = row.optString("code", "").trim()
        val rewardStr = row.optString("reward", "").trim()
        if (codeStr.isBlank() || rewardStr.isBlank()) {
            Log.w(TAG, "Skipping row: blank code or reward")
            return null
        }

        val expirationDateNormalized =
            ShiftCodeExpiration.normalizeExpirationDateString(row.optString("expiration_date", ""))
        if (!isNonExpiring && !isUnknownExpiration && expirationDateNormalized == null) {
            Log.w(
                TAG,
                "Skipping row $codeStr: neither expiration flags nor a valid expiration_date"
            )
            return null
        }

        val time12 =
            row.optString("expiration_time_12h", "").trim().ifBlank {
                formatPostgresTimeAs12h(row.optString("expiration_time", ""))
            }

        return ShiftCode(
            code = codeStr,
            reward = rewardStr,
            expirationDate = if (isNonExpiring || isUnknownExpiration) null else expirationDateNormalized,
            expirationTime = time12,
            isNonExpiring = isNonExpiring,
            isUnknownExpiration = isUnknownExpiration,
            bl = row.optBooleanCompat("bl"),
            blTps = row.optBooleanCompat("bl_tps"),
            bl2 = row.optBooleanCompat("bl2"),
            bl3 = row.optBooleanCompat("bl3"),
            bl4 = row.optBooleanCompat("bl4"),
            wonderlands = row.optBooleanCompat("wonderlands"),
            isKey = row.optBooleanCompat("is_key"),
            isCosmetic = row.optBooleanCompat("is_cosmetic"),
            isGear = row.optBooleanCompat("is_gear"),
            ingestedAtUtcMillis =
                ShiftCodeExpiration.parseIngestedAtUtcMillisFromJson(
                    if (row.has("ingested_at_utc") && !row.isNull("ingested_at_utc")) row.get("ingested_at_utc") else null
                )
        )
    } catch (e: Exception) {
        val codeHint = row.optString("code", "?")
        Log.w(TAG, "Skipping row $codeHint: ${e.message}", e)
        return null
    }
}

private fun JSONObject.optBooleanCompat(name: String): Boolean {
    if (!has(name) || isNull(name)) return false
    return when (val v = get(name)) {
        is Boolean -> v
        is String -> v.equals("true", ignoreCase = true) || v == "t"
        is Number -> v.toInt() != 0
        else -> false
    }
}

/**
 * Mirrors [dashboard/shift-codes-dashboard.js] `formatPostgresTimeAs12h` for when
 * `expiration_time_12h` is absent.
 */
internal fun formatPostgresTimeAs12h(t: String): String {
    val m =
        Regex("""^\s*(\d{1,2}):(\d{2})(?::(\d{2}))?""")
            .find(t.trim())
            ?: return ""

    val hourRaw = m.groupValues[1].toIntOrNull() ?: return ""
    val minute = m.groupValues[2].padStart(2, '0')
    val second = (m.groupValues.getOrNull(3) ?: "").ifBlank { "00" }.padStart(2, '0')
    if (hourRaw !in 0..23) return ""

    val amPm = if (hourRaw >= 12) "PM" else "AM"
    val h12 =
        when (hourRaw % 12) {
            0 -> 12
            else -> hourRaw % 12
        }

    return String.format(java.util.Locale.US, "%d:%s:%s %s", h12, minute, second, amPm)
}

class NetworkException(message: String?, val statusCode: Int) : Exception(message)
