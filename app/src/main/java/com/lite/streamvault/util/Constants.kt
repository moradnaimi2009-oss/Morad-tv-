package com.lite.streamvault.util

object Constants {
    // Base URL of the remote control panel API.
    // IMPORTANT: use the permanent Netlify production URL — NOT a deploy-preview hash URL.
    const val BASE_URL: String = "https://delicate-starburst-a02af3.netlify.app/"

    const val USER_AGENT: String =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    const val API_PATH: String = "api/"

    // Ad networks
    const val ADMOB_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    const val ADMOB_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    const val APPLOVIN_BANNER_ID = "YOUR_APPLOVIN_BANNER"
    const val APPLOVIN_INTERSTITIAL_ID = "YOUR_APPLOVIN_INTERSTITIAL"
    const val STARTAPP_APP_ID = "YOUR_STARTAPP_APP_ID"
    const val UNITY_GAME_ID = "YOUR_UNITY_GAME_ID"

    // Player
    const val PLAYER_SEEKBAR_KEY = "player_seekbar"
}
