package com.lite.streamvault.ads

import android.app.Activity
import android.view.ViewGroup
import com.lite.streamvault.BuildConfig
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds

class UnityPlatform(private val config: AdConfig) : AdPlatform {

    @Volatile private var initialized = false
    @Volatile private var ready = false
    @Volatile private var rewardedReady = false

    // Placement names must match the ones created in the Unity dashboard
    // (Monetization > Placements). Interstitial comes from `interstitial_id` in the
    // ad_campaigns row; rewarded from `rewarded_id` (default name if empty).
    private val interstitialId = config.interstitialId ?: "Interstitial_Android"
    private val rewardedId = config.rewardedId?.takeIf { it.isNotBlank() } ?: "Rewarded_Android"

    override fun initialize(activity: Activity) {
        if (initialized) return
        val gameId = config.appId?.takeIf { it.isNotBlank() } ?: return
        // UnityAds.initialize is ASYNC — loading an ad before it actually finishes
        // silently fails. So the first ad load only happens inside the completion
        // callback, not right after this call returns.
        // testMode is only on for debug builds: in test mode Unity serves fake ads
        // that earn nothing, so release builds must use real ads.
        UnityAds.initialize(activity, gameId, BuildConfig.DEBUG, object : IUnityAdsInitializationListener {
            override fun onInitializationComplete() {
                initialized = true
                loadInterstitial()
                loadRewarded()
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

    override fun loadRewarded() {
        if (!initialized) return
        UnityAds.load(rewardedId, object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(p0: String?) { rewardedReady = true }
            override fun onUnityAdsFailedToLoad(p0: String?, p1: UnityAds.UnityAdsLoadError?, p2: String?) { rewardedReady = false }
        })
    }

    override fun isRewardedReady(): Boolean = rewardedReady

    override fun showRewarded(activity: Activity, onReward: () -> Unit, onClosed: () -> Unit) {
        if (!rewardedReady) { onClosed(); return }
        UnityAds.show(activity, rewardedId, object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(p0: String?, p1: UnityAds.UnityAdsShowError?, p2: String?) { onClosed() }
            override fun onUnityAdsShowStart(p0: String?) {}
            override fun onUnityAdsShowClick(p0: String?) {}
            override fun onUnityAdsShowComplete(p0: String?, state: UnityAds.UnityAdsShowCompletionState?) {
                rewardedReady = false
                loadRewarded()
                if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) onReward()
                onClosed()
            }
        })
    }

    override fun loadBanner(container: ViewGroup) {
        // Handled by a dedicated Compose view (UnityBannerAdView) instead.
    }
    override fun destroy() {}
}
