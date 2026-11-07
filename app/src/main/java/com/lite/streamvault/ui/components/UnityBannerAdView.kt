package com.lite.streamvault.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import kotlinx.coroutines.delay

@Composable
fun UnityBannerAdView(activity: Activity, placementId: String, modifier: Modifier = Modifier) {
    val holder = remember { arrayOfNulls<BannerView>(1) }

    // Unity's SDK initializes asynchronously (started from the splash screen). Creating the
    // banner before init finishes makes load() fail silently and it never retries, so wait
    // here until the SDK reports it is ready, and only then build the banner.
    var sdkReady by remember { mutableStateOf(UnityAds.isInitialized()) }
    LaunchedEffect(Unit) {
        while (!sdkReady) {
            delay(500)
            sdkReady = UnityAds.isInitialized()
        }
    }

    // Release the banner when it leaves the screen (otherwise it leaks the Activity).
    DisposableEffect(Unit) {
        onDispose {
            holder[0]?.destroy()
            holder[0] = null
        }
    }

    Box(modifier = modifier.fillMaxWidth().height(50.dp)) {
        if (sdkReady) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    BannerView(activity, placementId, UnityBannerSize(320, 50)).apply {
                        holder[0] = this
                        load()
                    }
                }
            )
        }
    }
}
