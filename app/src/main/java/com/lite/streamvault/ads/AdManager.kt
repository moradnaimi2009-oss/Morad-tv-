package com.lite.streamvault.ads

import android.app.Activity
import android.view.ViewGroup
import com.lite.streamvault.domain.model.AdCampaign
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor() {

    @Volatile private var platform: AdPlatform = NoOpAdPlatform()
    @Volatile private var initialized = false
    @Volatile private var showAds = true

    // Set once from the referral status at app start (100 successful referrals).
    // Wins over everything else below — a remote "showAds=true" flag or a live
    // campaign must never re-enable ads for a user who already earned removal.
    @Volatile private var adsRemovedLifetime = false

    // Minimum gap between two forced interstitials. Showing them back-to-back earns
    // a little extra short-term revenue but drives churn/uninstalls, which costs far
    // more long-term revenue than it gains — this keeps interstitials monetizing
    // without stacking on top of each other.
    private val minIntervalBetweenInterstitialsMs = 45_000L
    @Volatile private var lastInterstitialShownAt = 0L

    private val _activeCampaign = MutableStateFlow<AdCampaign?>(null)
    val activeCampaign: StateFlow<AdCampaign?> = _activeCampaign

    fun setAdsRemovedLifetime(removed: Boolean) {
        adsRemovedLifetime = removed
        if (removed) {
            platform = NoOpAdPlatform()
            _activeCampaign.value = null
        }
    }

    fun configure(campaigns: List<AdCampaign>, showAds: Boolean, activity: Activity) {
        this.showAds = showAds
        if (adsRemovedLifetime || !showAds || campaigns.isEmpty()) {
            platform = NoOpAdPlatform()
            _activeCampaign.value = null
            return
        }
        val chosen = pickBestActive(campaigns)
        _activeCampaign.value = chosen
        if (chosen == null) {
            platform = NoOpAdPlatform()
            return
        }
        val config = AdConfig(
            network = chosen.network,
            appId = chosen.appId,
            bannerId = chosen.bannerId,
            interstitialId = chosen.interstitialId
        )
        platform = when (chosen.network.lowercase()) {
            AdConfig.ADMOB -> AdMobPlatform(config)
            AdConfig.APPLOVIN -> AppLovinPlatform(config)
            AdConfig.STARTAPP -> StartAppPlatform(config)
            AdConfig.UNITY -> UnityPlatform(config)
            else -> NoOpAdPlatform()
        }
        platform.initialize(activity)
        platform.loadInterstitial()
        platform.loadRewarded()
        initialized = true
    }

    fun showInterstitial(activity: Activity, onClosed: () -> Unit) {
        if (adsRemovedLifetime || !showAds) { onClosed(); return }
        val now = System.currentTimeMillis()
        if (now - lastInterstitialShownAt < minIntervalBetweenInterstitialsMs) {
            onClosed()
            return
        }
        lastInterstitialShownAt = now
        platform.showInterstitial(activity, onClosed)
    }

    fun loadBanner(container: ViewGroup) {
        if (adsRemovedLifetime || !showAds) return
        platform.loadBanner(container)
    }

    // Purely opt-in: only ever triggered by the user tapping something like "شاهد
    // إعلان لدعم التطبيق". Never called automatically, so it adds ad revenue without
    // adding a single unwanted interruption.
    fun isRewardedAvailable(): Boolean = !adsRemovedLifetime && showAds && platform.isRewardedReady()

    fun showRewarded(activity: Activity, onReward: () -> Unit, onClosed: () -> Unit) {
        if (adsRemovedLifetime || !showAds) { onClosed(); return }
        platform.showRewarded(activity, onReward, onClosed)
    }

    fun isInitialized(): Boolean = initialized

    // Called every time the app comes back to the foreground (see MainActivity).
    // If the interstitial/rewarded ad failed to (re)load while the app was in the
    // background — a dropped network call has no automatic retry — this makes sure
    // we try again now, instead of leaving the app with "no ad ready" forever.
    fun onAppResumed() {
        if (adsRemovedLifetime || !showAds) return
        if (!platform.isInterstitialReady()) platform.loadInterstitial()
        if (!platform.isRewardedReady()) platform.loadRewarded()
    }

    fun destroy() {
        platform.destroy()
        initialized = false
    }

    // Lower "priority" number wins — lets you control which network shows first
    // straight from the ad_campaigns table without a new app release.
    private fun pickBestActive(campaigns: List<AdCampaign>): AdCampaign? =
        campaigns.filter { it.isActive }.minByOrNull { it.priority }
}
