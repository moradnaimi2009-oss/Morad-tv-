package com.lite.streamvault.ads

import android.app.Activity
import android.view.ViewGroup
import com.startapp.sdk.adsbase.Ad
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.StartAppSDK
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener
import com.startapp.sdk.adsbase.adlisteners.AdEventListener

// StartApp/Start.io only uses ONE App ID for the whole SDK (not separate
// banner/interstitial unit IDs like AdMob) — the ad format is chosen in code,
// not by a separate ID. We pass the App ID through AdConfig.appId.
class StartAppPlatform(private val config: AdConfig) : AdPlatform {

    private var activityRef: Activity? = null
    private var interstitialAd: StartAppAd? = null
    @Volatile private var interstitialReady = false

    override fun initialize(activity: Activity) {
        activityRef = activity
        val appId = config.appId?.takeIf { it.isNotBlank() }
        if (appId != null) {
            StartAppSDK.init(activity, appId, true)
        }
    }

    override fun loadInterstitial() {
        val activity = activityRef ?: return
        val ad = StartAppAd(activity)
        ad.loadAd(object : AdEventListener {
            override fun onReceiveAd(loadedAd: Ad) {
                interstitialAd = ad
                interstitialReady = true
            }

            override fun onFailedToReceiveAd(failedAd: Ad?) {
                interstitialReady = false
            }
        })
    }

    override fun showInterstitial(activity: Activity, onClosed: () -> Unit) {
        val ad = interstitialAd
        if (ad == null || !interstitialReady) {
            onClosed()
            return
        }
        interstitialReady = false
        ad.showAd(object : AdDisplayListener {
            override fun adHidden(hiddenAd: Ad?) {
                loadInterstitial() // preload the next one
                onClosed()
            }
            override fun adDisplayed(displayedAd: Ad?) {}
            override fun adClicked(clickedAd: Ad?) {}
            override fun adNotDisplayed(notDisplayedAd: Ad?) {
                onClosed()
            }
        })
    }

    override fun isInterstitialReady(): Boolean = interstitialReady

    override fun loadBanner(container: ViewGroup) {
        // Not wired into the Compose UI yet (the app's banner slot is AdMob-specific
        // for now) — interstitial is the priority since that's what was asked for.
    }

    override fun destroy() {
        activityRef = null
        interstitialAd = null
    }
}
