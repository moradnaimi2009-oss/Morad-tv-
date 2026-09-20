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
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.lite.streamvault.util.Constants

class AdMobPlatform(private val config: AdConfig) : AdPlatform {

    @Volatile private var interstitial: InterstitialAd? = null
    @Volatile private var rewarded: RewardedAd? = null
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

                // Without this, a failed show (e.g. app not in foreground, network
                // hiccup) never calls onClosed() — the user gets stuck with no video
                // and no error, since only onAdDismissedFullScreenContent was handled.
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
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

    override fun loadRewarded() {
        val ctx = context ?: return
        val req = AdRequest.Builder().build()
        RewardedAd.load(
            ctx,
            Constants.ADMOB_REWARDED_ID,
            req,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) { rewarded = ad }
                override fun onAdFailedToLoad(err: LoadAdError) { rewarded = null }
            }
        )
    }

    override fun isRewardedReady(): Boolean = rewarded != null

    override fun showRewarded(activity: Activity, onReward: () -> Unit, onClosed: () -> Unit) {
        val ad = rewarded
        if (ad != null) {
            var earnedReward = false
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewarded = null
                    loadRewarded()
                    if (earnedReward) onReward()
                    onClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewarded = null
                    loadRewarded()
                    onClosed()
                }
            }
            ad.show(activity) { earnedReward = true }
        } else {
            onClosed()
        }
    }

    override fun loadBanner(container: ViewGroup) {
        // Banner handled by AndroidView in Compose — no-op here.
    }

    override fun destroy() {
        interstitial = null
        rewarded = null
    }
}
