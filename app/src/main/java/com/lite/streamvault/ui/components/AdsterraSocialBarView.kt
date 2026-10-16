package com.lite.streamvault.ui.components

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.webkit.WebView
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Renders an Adsterra "Social Bar" ad unit — a single self-contained script tag that
 * draws its own floating widget. Unlike the banner, it has no fixed iframe size of its
 * own, so this is hosted in a small, corner-positioned WebView rather than a full-screen
 * one: a full-screen transparent WebView would silently block touches everywhere on top
 * of the rest of the app's UI, since Android WebViews can't let clicks "pass through"
 * their transparent areas.
 *
 * [scriptUrl] is the single <script src="..."> URL from the Adsterra dashboard for this
 * Social Bar ad unit.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AdsterraSocialBarView(
    modifier: Modifier = Modifier,
    scriptUrl: String,
    showAds: Boolean
) {
    if (!showAds || scriptUrl.isBlank()) return

    val html = """
        <html>
        <head>
            <style>
                html, body { margin: 0; padding: 0; background: transparent; }
            </style>
        </head>
        <body>
            <script src="$scriptUrl"></script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = modifier.size(64.dp),
        factory = { ctx ->
            WebView(ctx).apply {
                setBackgroundColor(AndroidColor.TRANSPARENT)
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                loadDataWithBaseURL("https://morad-tv.app/", html, "text/html", "utf-8", null)
            }
        }
    )
}
