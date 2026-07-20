package com.lite.streamvault.ads

import android.app.Activity
import android.view.ViewGroup
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds

class UnityPlatform(private val config: AdConfig) : AdPlatform {

    @Volatile private var initialized = false
    @Volatile private var ready = false

    private val interstitialId = config.interstitialId ?: "video"

    override fun initialize(activity: Activity) {
        if (initialized) return
        val gameId = config.appId?.takeIf { it.isNotBlank() } ?: return
        UnityAds.initialize(activity, gameId, false)
        initialized = true
    }

    override fun loadInterstitial() {
        UnityAds.load(interstitialId, object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(p0: String?) { ready = true }
            override fun onUnityAdsFailedToLoad(p0: String?, p1: UnityAds.UnityAdsLoadError?, p2: String?) { ready = false }
        })
    }

    override fun showInterstitial(activity: Activity, onClosed: () -> Unit) {
        if (!ready) { onClosed(); return }
        UnityAds.show(activity, interstitialId, object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(p0: String?, p1: UnityAds.UnityAdsShowError?, p2: String?) { onClosed() }
            override fun onUnityAdsShowStart(p0: String?) {}
            override fun onUnityAdsShowClick(p0: String?) {}
            override fun onUnityAdsShowComplete(p0: String?, p1: UnityAds.UnityAdsShowCompletionState?) {
                ready = false
                onClosed()
            }
        })
    }

    override fun isInterstitialReady(): Boolean = ready
    override fun loadBanner(container: ViewGroup) {}
    override fun destroy() {}
}
