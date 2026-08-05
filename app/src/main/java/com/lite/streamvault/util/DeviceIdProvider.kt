package com.lite.streamvault.util

import android.content.Context
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A stable, per-device ID used for the no-login referral system.
 *
 * Uses ANDROID_ID (tied to the OS install) instead of a random ID stored in this
 * app's own private storage. This matters because "app cloner" / dual-space apps
 * sandbox each clone's private storage separately, so a purely random ID would
 * reset on every clone — letting one phone farm unlimited referral codes. ANDROID_ID
 * is shared across clones on most (not all) devices, which closes that specific hole.
 *
 * Honest limitation: this is a deterrent, not a guarantee. Some cloning tools do
 * spoof ANDROID_ID too, and there's no way to fully solve this without phone
 * verification or real accounts — which conflicts with the "no login" requirement.
 */
@Singleton
class DeviceIdProvider @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    private val prefs = appContext.getSharedPreferences("morad_device", Context.MODE_PRIVATE)

    val deviceId: String by lazy {
        val androidId = try {
            Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            null
        }
        // "9774d56d682e549c" is a known bad/shared ANDROID_ID value seen on some
        // old or rooted devices — fall back to a stored random ID in that case.
        if (!androidId.isNullOrBlank() && androidId != "9774d56d682e549c") {
            androidId
        } else {
            prefs.getString(KEY_DEVICE_ID, null) ?: UUID.randomUUID().toString().also {
                prefs.edit().putString(KEY_DEVICE_ID, it).apply()
            }
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
