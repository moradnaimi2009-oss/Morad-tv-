package com.lite.streamvault.ads

import android.app.Activity
import android.view.ViewGroup
import com.lite.streamvault.domain.model.AdCampaign
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor() {

    @Volatile private var platform: AdPlatform = NoOpAdPlatform()
    @Volatile private var initialized = false
    @Volatile private var showAds = true

    fun configure(campaigns: List<AdCampaign>, showAds: Boolean, activity: Activity) {
        this.showAds = showAds
        if (!showAds || campaigns.isEmpty()) {
            platform = NoOpAdPlatform()
            return
        }
        val chosen = pickFirstActive(campaigns)
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
        initialized = true
    }

    fun showInterstitial(activity: Activity, onClosed: () -> Unit) {
        if (!showAds) { onClosed(); return }
        platform.showInterstitial(activity, onClosed)
    }

    fun loadBanner(container: ViewGroup) {
        if (!showAds) return
        platform.loadBanner(container)
    }

    fun isInitialized(): Boolean = initialized

    fun destroy() {
        platform.destroy()
        initialized = false
    }

    private fun pickFirstActive(campaigns: List<AdCampaign>): AdCampaign? {
        val priority = listOf(AdConfig.ADMOB, AdConfig.APPLOVIN, AdConfig.STARTAPP, AdConfig.UNITY)
        val active = campaigns.filter { it.isActive }
        for (network in priority) {
            val match = active.firstOrNull { it.network.lowercase() == network }
            if (match != null) return match
        }
        return active.firstOrNull()
    }
}
