package com.lite.streamvault.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A stable, random ID for this install — generated once and stored locally.
 * Used for the referral system so it works with no login/account at all.
 * NOTE: this resets if the user clears app data or reinstalls (there's no
 * account to tie it to), which is the accepted tradeoff of a no-login system.
 */
@Singleton
class DeviceIdProvider @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("morad_device", Context.MODE_PRIVATE)

    val deviceId: String by lazy {
        prefs.getString(KEY_DEVICE_ID, null) ?: UUID.randomUUID().toString().also {
            prefs.edit().putString(KEY_DEVICE_ID, it).apply()
        }
    }

    // Short, shareable version of the ID used as the referral code itself —
    // avoids needing a separate "generate a code" round trip to the server.
    val referralCode: String
        get() = deviceId.replace("-", "").take(8).uppercase()

    companion object {
        private const val KEY_DEVICE_ID = "device_id"
    }
}
