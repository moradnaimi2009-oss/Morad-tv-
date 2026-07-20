package com.lite.streamvault.ads

import android.app.Activity
import android.view.ViewGroup

class StartAppPlatform(private val config: AdConfig) : AdPlatform {
    @Volatile private var initialized = false
    override fun initialize(activity: Activity) { initialized = true }
    override fun loadInterstitial() {}
    override fun showInterstitial(activity: Activity, onClosed: () -> Unit) { onClosed() }
    override fun isInterstitialReady(): Boolean = true
    override fun loadBanner(container: ViewGroup) {}
    override fun destroy() {}
}
