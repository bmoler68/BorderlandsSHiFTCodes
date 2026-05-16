package com.brianmoler.borderlandsshiftcodes.data

/**
 * Builds [ShiftCode] / [ShiftCodeEntity] instances for unit tests using legacy expiration strings
 * or explicit Supabase-shaped fields.
 */
object TestShiftCodeFactory {
    fun shiftCode(
        code: String = "TEST123456789",
        expiration: String = "2099-12-31",
        expirationTime: String = "",
        reward: String = "Test Reward",
        bl: Boolean = true,
        blTps: Boolean = false,
        bl2: Boolean = false,
        bl3: Boolean = false,
        bl4: Boolean = false,
        wonderlands: Boolean = false,
        isKey: Boolean = false,
        isCosmetic: Boolean = false,
        isGear: Boolean = false,
        ingestedAtUtcMillis: Long = ShiftCodeExpiration.INGEST_SORT_UNKNOWN
    ): ShiftCode {
        val fields = ShiftCodeExpiration.fromLegacyExpirationColumn(expiration)
        return ShiftCode(
            code = code,
            reward = reward,
            expirationDate = fields.expirationDate,
            expirationTime = expirationTime,
            isNonExpiring = fields.isNonExpiring,
            isUnknownExpiration = fields.isUnknownExpiration,
            bl = bl,
            blTps = blTps,
            bl2 = bl2,
            bl3 = bl3,
            bl4 = bl4,
            wonderlands = wonderlands,
            isKey = isKey,
            isCosmetic = isCosmetic,
            isGear = isGear,
            ingestedAtUtcMillis = ingestedAtUtcMillis
        )
    }

    fun entity(
        code: String = "TEST123456789",
        expiration: String = "2099-12-31",
        expirationTime: String = "",
        reward: String = "Test Reward",
        bl: Boolean = true,
        blTps: Boolean = false,
        bl2: Boolean = false,
        bl3: Boolean = false,
        bl4: Boolean = false,
        wonderlands: Boolean = false,
        isKey: Boolean = false,
        isCosmetic: Boolean = false,
        isGear: Boolean = false,
        ingestedAtUtcMillis: Long = ShiftCodeExpiration.INGEST_SORT_UNKNOWN
    ): ShiftCodeEntity {
        val fields = ShiftCodeExpiration.fromLegacyExpirationColumn(expiration)
        return ShiftCodeEntity(
            code = code,
            reward = reward,
            expirationDate = fields.expirationDate,
            expirationTime = expirationTime,
            isNonExpiring = fields.isNonExpiring,
            isUnknownExpiration = fields.isUnknownExpiration,
            bl = bl,
            blTps = blTps,
            bl2 = bl2,
            bl3 = bl3,
            bl4 = bl4,
            wonderlands = wonderlands,
            isKey = isKey,
            isCosmetic = isCosmetic,
            isGear = isGear,
            ingestedAtUtcMillis = ingestedAtUtcMillis
        )
    }
}
