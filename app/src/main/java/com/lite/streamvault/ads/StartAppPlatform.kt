package com.lite.streamvault.ads

import android.app.Activity
import android.view.ViewGroup
import com.startapp.sdk.adsbase.StartAppAd

class StartAppPlatform(private val config: AdConfig) : AdPlatform {

    @Volatile private var initialized = false

    override fun initialize(activity: Activity) {
        if (initialized) return
        // StartApp init normally happens via AndroidManifest SDK key.
        initialized = true
    }

    override fun loadInterstitial() {}

    override fun showInterstitial(activity: Activity, onClosed: () -> Unit) {
        try {
            StartAppAd(activity).showAd { onClosed() }
        } catch (_: Throwable) { onClosed() }
    }

    override fun isInterstitialReady(): Boolean = true
    override fun loadBanner(container: ViewGroup) {}
    override fun destroy() {}
}
