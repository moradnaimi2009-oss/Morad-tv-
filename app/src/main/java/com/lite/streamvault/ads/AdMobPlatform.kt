package com.lite.streamvault.ads

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.lite.streamvault.util.Constants

class AdMobPlatform(private val config: AdConfig) : AdPlatform {

    @Volatile private var interstitial: InterstitialAd? = null
    @Volatile private var initialized = false
    @Volatile private var context: Context? = null

    override fun initialize(activity: Activity) {
        if (initialized) return
        context = activity.applicationContext
        MobileAds.initialize(activity) { initialized = true }
    }

    override fun loadInterstitial() {
        val ctx = context ?: return
        val id = config.interstitialId?.takeIf { it.isNotBlank() } ?: Constants.ADMOB_INTERSTITIAL_ID
        val req = AdRequest.Builder().build()
        InterstitialAd.load(
            ctx,
            id,
            req,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) { interstitial = ad }
                override fun onAdFailedToLoad(err: LoadAdError) { interstitial = null }
            }
        )
    }

    override fun showInterstitial(activity: Activity, onClosed: () -> Unit) {
        val ad = interstitial
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitial = null
                    loadInterstitial()
                    onClosed()
                }
            }
            ad.show(activity)
        } else {
            onClosed()
        }
    }

    override fun isInterstitialReady(): Boolean = interstitial != null

    override fun loadBanner(container: ViewGroup) {
        // Banner handled by AndroidView in Compose — no-op here.
    }

    override fun destroy() {
        interstitial = null
    }
}
