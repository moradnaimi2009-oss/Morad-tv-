package com.lite.streamvault.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

@Composable
fun UnityBannerAdView(activity: Activity, placementId: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth().height(50.dp),
        factory = {
            BannerView(activity, placementId, UnityBannerSize(320, 50)).apply { load() }
        }
    )
}
