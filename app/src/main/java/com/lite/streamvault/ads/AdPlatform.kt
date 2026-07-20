package com.lite.streamvault.ads

import android.app.Activity
import android.view.ViewGroup

interface AdPlatform {
    fun initialize(activity: Activity)
    fun loadInterstitial()
    fun showInterstitial(activity: Activity, onClosed: () -> Unit)
    fun isInterstitialReady(): Boolean
    fun loadBanner(container: ViewGroup)
    fun destroy()
}
