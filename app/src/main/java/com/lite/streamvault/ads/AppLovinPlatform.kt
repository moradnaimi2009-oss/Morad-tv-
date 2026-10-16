package com.lite.streamvault.ads

import android.app.Activity
import android.view.ViewGroup
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.sdk.AppLovinSdk

class AppLovinPlatform(private val config: AdConfig) : AdPlatform {

    @Volatile private var initialized = false
    private var interstitialAd: MaxInterstitialAd? = null
    private var pendingOnClosed: (() -> Unit)? = null

    override fun initialize(activity: Activity) {
        if (initialized) return
        try {
            AppLovinSdk.getInstance(activity).initializeSdk { initialized = true }
        } catch (_: Throwable) {
            initialized = true
        }

        val unitId = config.interstitialId?.takeIf { it.isNotBlank() } ?: return
        val ad = MaxInterstitialAd(unitId, activity)
        ad.setListener(object : MaxAdListener {
            override fun onAdLoaded(maxAd: MaxAd) {}
            override fun onAdDisplayed(maxAd: MaxAd) {}
            override fun onAdHidden(maxAd: MaxAd) {
                ad.loadAd()
                pendingOnClosed?.invoke()
                pendingOnClosed = null
            }
            override fun onAdClicked(maxAd: MaxAd) {}
            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
            override fun onAdDisplayFailed(maxAd: MaxAd, error: MaxError) {
                pendingOnClosed?.invoke()
                pendingOnClosed = null
            }
        })
        interstitialAd = ad
        ad.loadAd()
    }

    override fun loadInterstitial() {
        interstitialAd?.loadAd()
    }

    override fun showInterstitial(activity: Activity, onClosed: () -> Unit) {
        val ad = interstitialAd
        if (ad != null && ad.isReady) {
            pendingOnClosed = onClosed
            ad.showAd()
        } else {
            onClosed()
        }
    }

    override fun isInterstitialReady(): Boolean = interstitialAd?.isReady ?: false

    override fun loadBanner(container: ViewGroup) {
        // Handled by a dedicated Compose view (AppLovinBannerView) instead.
    }

    override fun destroy() {
        interstitialAd?.destroy()
        interstitialAd = null
    }
}
