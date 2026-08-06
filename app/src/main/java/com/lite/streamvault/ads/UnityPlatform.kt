package com.lite.streamvault.ads

import android.app.Activity
import android.view.ViewGroup
import com.unity3d.ads.IUnityAdsInitializationListener
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
        // UnityAds.initialize is ASYNC — loading an ad before it actually finishes
        // silently fails. So the first ad load only happens inside the completion
        // callback, not right after this call returns.
        UnityAds.initialize(activity, gameId, true, object : IUnityAdsInitializationListener {
            override fun onInitializationComplete() {
                initialized = true
                loadInterstitial()
            }
            override fun onInitializationFailed(
                error: UnityAds.UnityAdsInitializationError?,
                message: String?
            ) {
                initialized = false
            }
        })
    }

    override fun loadInterstitial() {
        if (!initialized) return
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
                loadInterstitial()
                onClosed()
            }
        })
    }

    override fun isInterstitialReady(): Boolean = ready

    override fun loadBanner(container: ViewGroup) {
        // Handled by a dedicated Compose view (UnityBannerAdView) instead.
    }
    override fun destroy() {}
}
