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

    private val _activeCampaign = MutableStateFlow<AdCampaign?>(null)
    val activeCampaign: StateFlow<AdCampaign?> = _activeCampaign

    fun configure(campaigns: List<AdCampaign>, showAds: Boolean, activity: Activity) {
        this.showAds = showAds
        if (!showAds || campaigns.isEmpty()) {
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

    // Lower "priority" number wins — lets you control which network shows first
    // straight from the ad_campaigns table without a new app release.
    private fun pickBestActive(campaigns: List<AdCampaign>): AdCampaign? =
        campaigns.filter { it.isActive }.minByOrNull { it.priority }
}
