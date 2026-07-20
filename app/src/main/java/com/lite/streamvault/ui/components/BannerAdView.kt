package com.lite.streamvault.ui.components

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String,
    showAds: Boolean
) {
    if (!showAds || adUnitId.isBlank()) return
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = { ctx ->
            com.google.android.gms.ads.AdView(ctx).apply {
                setAdSize(com.google.android.gms.ads.AdSize.BANNER)
                this.adUnitId = adUnitId
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                loadAd(com.google.android.gms.ads.AdRequest.Builder().build())
            }
        }
    )
}
