package com.lite.streamvault.ui.components

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Renders an Adsterra "banner" ad unit (a simple atOptions + invoke.js pair) inside a
 * WebView. Adsterra has no native Android SDK — this is their documented embedding
 * method, just wrapped in an HTML shell so WebView can load it.
 *
 * [scriptKey] is the value of atOptions.key, and [invokeJsUrl] is the matching
 * "invoke.js" URL — both come from the Adsterra dashboard for this specific ad unit.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AdsterraBannerView(
    modifier: Modifier = Modifier,
    scriptKey: String,
    invokeJsUrl: String,
    width: Int = 320,
    height: Int = 50,
    showAds: Boolean
) {
    if (!showAds || scriptKey.isBlank() || invokeJsUrl.isBlank()) return

    val html = """
        <html>
        <head>
            <style>
                html, body {
                    margin: 0; padding: 0; background: transparent;
                    display: flex; justify-content: center; align-items: center;
                    overflow: hidden;
                }
            </style>
        </head>
        <body>
            <script type="text/javascript">
                atOptions = {
                    'key' : '$scriptKey',
                    'format' : 'iframe',
                    'height' : $height,
                    'width' : $width,
                    'params' : {}
                };
            </script>
            <script src="$invokeJsUrl"></script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = modifier.fillMaxWidth().height(height.dp),
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
