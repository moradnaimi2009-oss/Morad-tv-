package com.lite.streamvault.ads

import android.app.Activity
import android.view.ViewGroup
import com.applovin.sdk.AppLovinSdk

class AppLovinPlatform(private val config: AdConfig) : AdPlatform {

    @Volatile private var initialized = false

    override fun initialize(activity: Activity) {
        if (initialized) return
        try {
            AppLovinSdk.getInstance(activity).initializeSdk { initialized = true }
        } catch (_: Throwable) { initialized = true }
    }

    override fun loadInterstitial() { /* AppLovin full SDK wiring deferred */ }
    override fun showInterstitial(activity: Activity, onClosed: () -> Unit) { onClosed() }
    override fun isInterstitialReady(): Boolean = false
    override fun loadBanner(container: ViewGroup) {}
    override fun destroy() {}
}
