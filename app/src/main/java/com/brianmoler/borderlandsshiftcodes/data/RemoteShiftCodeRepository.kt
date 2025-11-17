package com.brianmoler.borderlandsshiftcodes.data

import android.content.Context
import android.util.Log
import com.brianmoler.borderlandsshiftcodes.config.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

private const val TAG = "RemoteShiftCodeRepository"
private const val MAX_RETRY_ATTEMPTS = 3
private const val RETRY_DELAY_MS = 1000L

/**
 * Repository for fetching SHiFT codes from remote CSV sources.
 * 
 * This class handles all remote data access operations including fetching
 * from remote sources and parsing CSV data. It's separate from the local
 * database operations to maintain clear separation of concerns.
 */
class RemoteShiftCodeRepository(private val context: Context) {
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(AppConfig.Network.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(AppConfig.Network.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(AppConfig.Network.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
            chain.proceed(requestBuilder.build())
        }
        .build()

    /**
     * Fetches SHiFT codes from the remote CSV source with retry logic and fallback URL
     * @return List of parsed SHiFT codes
     * @throws Exception if both primary and fallback URLs fail after all retries
     */
    suspend fun fetchShiftCodes(): List<ShiftCode> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Fetching SHiFT codes from remote source")
        
        // Try primary URL first
        try {
            val codes = fetchFromUrl(AppConfig.Network.CSV_URL, "primary")
            Log.d(TAG, "Successfully fetched from primary URL")
            return@withContext codes
        } catch (e: Exception) {
            Log.w(TAG, "Primary URL failed: ${e.message}, trying fallback URL")
        }
        
        // Try fallback URL if primary fails
        try {
            val codes = fetchFromUrl(AppConfig.Network.CSV_FALLBACK_URL, "fallback")
            Log.d(TAG, "Successfully fetched from fallback URL")
            return@withContext codes
        } catch (e: Exception) {
            Log.e(TAG, "Both primary and fallback URLs failed")
            throw e
        }
    }

    /**
     * Fetches SHiFT codes from a specific URL with retry logic
     * @param url The URL to fetch from
     * @param urlType Description of the URL type for logging
     * @return List of parsed SHiFT codes
     * @throws Exception if network request fails or parsing fails after all retries
     */
    private suspend fun fetchFromUrl(url: String, urlType: String): List<ShiftCode> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Fetching SHiFT codes from $urlType URL: $url")
        
        var lastException: Exception? = null
        
        for (attempt in 1..MAX_RETRY_ATTEMPTS) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", AppConfig.Network.getUserAgent(context))
                    .build()
                    
                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw NetworkException("Network error from $urlType URL: ${response.code}", response.code)
                    }
                    
                    val csv = response.body.string()
                        
                    Log.d(TAG, "Successfully fetched CSV data from $urlType URL, parsing...")
                    return@withContext parseCsvToShiftCodes(csv)
                }
            } catch (e: Exception) {
                lastException = e
                Log.w(TAG, "Attempt $attempt failed for $urlType URL: ${e.message}")
                
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    delay(RETRY_DELAY_MS * attempt) // Exponential backoff
                }
            }
        }
        
        Log.e(TAG, "All retry attempts failed for $urlType URL")
        throw lastException ?: Exception("Unknown error occurred with $urlType URL")
    }

    /**
     * Parses CSV content into a list of ShiftCode objects
     * @param csv The CSV content as a string
     * @return List of parsed ShiftCode objects
     */
    private fun parseCsvToShiftCodes(csv: String): List<ShiftCode> {
        if (csv.isBlank()) {
            Log.w(TAG, "CSV is empty")
            return emptyList()
        }
        
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) {
            Log.w(TAG, "No valid lines found in CSV")
            return emptyList()
        }
        
        val header = lines.first().split(",")
        val columnIndices = getColumnIndices(header)
        
        // Validate that required columns exist
        if (columnIndices["CODE"] == -1) {
            throw IllegalArgumentException("Required column 'CODE' not found in CSV")
        }
        
        val parsedCodes = lines.drop(1).mapNotNull { line ->
            try {
                val cols = parseCsvLine(line)
                createShiftCodeFromColumns(cols, columnIndices)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to parse CSV line", e)
                null
            }
        }
        
        Log.d(TAG, "Parsed ${parsedCodes.size} SHiFT codes from CSV")
        return parsedCodes
    }

    /**
     * Gets the column indices for the CSV headers
     * @param header The CSV header row
     * @return Map of column names to their indices
     */
    private fun getColumnIndices(header: List<String>): Map<String, Int> {
        return mapOf(
            "CODE" to header.indexOfFirst { it.equals("CODE", ignoreCase = true) },
            "EXPIRATION" to header.indexOfFirst { it.equals("EXPIRATION", ignoreCase = true) },
            "EXPIRATION TIME" to header.indexOfFirst { it.equals("EXPIRATION TIME", ignoreCase = true) },
            "REWARD" to header.indexOfFirst { it.equals("REWARD", ignoreCase = true) },
            "BL" to header.indexOfFirst { it.equals("BL", ignoreCase = true) },
            "BL:TPS" to header.indexOfFirst { it.equals("BL:TPS", ignoreCase = true) },
            "BL2" to header.indexOfFirst { it.equals("BL2", ignoreCase = true) },
            "BL3" to header.indexOfFirst { it.equals("BL3", ignoreCase = true) },
            "BL4" to header.indexOfFirst { it.equals("BL4", ignoreCase = true) },
            "WONDERLANDS" to header.indexOfFirst { it.contains("Wonderlands", ignoreCase = true) },
            "IS_KEY" to header.indexOfFirst { it.equals("IS_KEY", ignoreCase = true) },
            "IS_COSMETIC" to header.indexOfFirst { it.equals("IS_COSMETIC", ignoreCase = true) },
            "IS_GEAR" to header.indexOfFirst { it.equals("IS_GEAR", ignoreCase = true) }
        )
    }

    /**
     * Creates a ShiftCode object from CSV columns
     * @param cols The CSV columns
     * @param columnIndices Map of column names to indices
     * @return ShiftCode object or null if creation fails
     */
    private fun createShiftCodeFromColumns(cols: List<String>, columnIndices: Map<String, Int>): ShiftCode? {
        return try {
            val code = cols.getOrNull(columnIndices["CODE"] ?: -1)?.trim() ?: ""
            val expiration = cols.getOrNull(columnIndices["EXPIRATION"] ?: -1)?.trim() ?: ""
            val expirationTime = cols.getOrNull(columnIndices["EXPIRATION TIME"] ?: -1)?.trim() ?: ""
            val reward = cols.getOrNull(columnIndices["REWARD"] ?: -1)?.trim() ?: ""
            
            // Validate required fields
            if (code.isBlank() || expiration.isBlank() || reward.isBlank()) {
                return null
            }
            
            ShiftCode(
                code = code,
                expiration = expiration,
                expirationTime = expirationTime,
                reward = reward,
                bl = cols.getOrNull(columnIndices["BL"] ?: -1)?.trim().equals("Y", ignoreCase = true),
                blTps = cols.getOrNull(columnIndices["BL:TPS"] ?: -1)?.trim().equals("Y", ignoreCase = true),
                bl2 = cols.getOrNull(columnIndices["BL2"] ?: -1)?.trim().equals("Y", ignoreCase = true),
                bl3 = cols.getOrNull(columnIndices["BL3"] ?: -1)?.trim().equals("Y", ignoreCase = true),
                bl4 = cols.getOrNull(columnIndices["BL4"] ?: -1)?.trim().equals("Y", ignoreCase = true),
                wonderlands = cols.getOrNull(columnIndices["WONDERLANDS"] ?: -1)?.trim().equals("Y", ignoreCase = true),
                isKey = cols.getOrNull(columnIndices["IS_KEY"] ?: -1)?.trim().equals("Y", ignoreCase = true),
                isCosmetic = cols.getOrNull(columnIndices["IS_COSMETIC"] ?: -1)?.trim().equals("Y", ignoreCase = true),
                isGear = cols.getOrNull(columnIndices["IS_GEAR"] ?: -1)?.trim().equals("Y", ignoreCase = true)
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to create ShiftCode from columns", e)
            null
        }
    }

    /**
     * Parses a CSV line, handling quoted fields properly
     * @param line The CSV line to parse
     * @return List of parsed fields
     */
    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var inQuotes = false
        var value = StringBuilder()
        
        for (c in line) {
            when (c) {
                '"' -> inQuotes = !inQuotes
                ',' -> if (inQuotes) {
                    value.append(c)
                } else {
                    result.add(value.toString())
                    value = StringBuilder()
                }
                else -> value.append(c)
            }
        }
        result.add(value.toString())
        return result
    }
}

/**
 * Custom exception for network-related errors
 */
class NetworkException(message: String?, val statusCode: Int) : Exception(message)
