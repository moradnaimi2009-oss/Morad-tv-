package com.lite.streamvault.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.startapp.sdk.ads.banner.Banner

@Composable
fun StartAppBannerView(activity: Activity, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { Banner(activity) }
    )
}
