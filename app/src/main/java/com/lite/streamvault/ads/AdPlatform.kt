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

    // Optional revenue channel: a short video the user watches BY CHOICE (e.g. tapping
    // "support the app") in exchange for a reward. Unlike the interstitial, this is never
    // forced — it only plays when the user explicitly asks for it, so it adds incremental
    // revenue without adding any unwanted interruption.
    // Default: not supported on this network, resolves immediately with no reward.
    fun loadRewarded() {}
    fun isRewardedReady(): Boolean = false
    fun showRewarded(activity: Activity, onReward: () -> Unit, onClosed: () -> Unit) { onClosed() }
}
