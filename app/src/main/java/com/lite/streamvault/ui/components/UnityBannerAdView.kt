package com.lite.streamvault.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

@Composable
fun UnityBannerAdView(activity: Activity, placementId: String, modifier: Modifier = Modifier) {
    val holder = remember { arrayOfNulls<BannerView>(1) }

    // Release the banner when it leaves the screen (otherwise it leaks the Activity).
    DisposableEffect(Unit) {
        onDispose {
            holder[0]?.destroy()
            holder[0] = null
        }
    }

    AndroidView(
        modifier = modifier.fillMaxWidth().height(50.dp),
        factory = {
            BannerView(activity, placementId, UnityBannerSize(320, 50)).apply {
                holder[0] = this
                load()
            }
        }
    )
}
