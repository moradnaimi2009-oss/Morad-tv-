package com.lite.streamvault.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.applovin.mediation.ads.MaxAdView

@Composable
fun AppLovinBannerView(activity: Activity, adUnitId: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth().height(50.dp),
        factory = {
            MaxAdView(adUnitId, activity).apply { loadAd() }
        }
    )
}
