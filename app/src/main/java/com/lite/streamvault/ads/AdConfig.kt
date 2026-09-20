package com.lite.streamvault.ads

data class AdConfig(
    val network: String,
    val appId: String?,
    val bannerId: String?,
    val interstitialId: String?
) {
    companion object {
        const val ADMOB = "admob"
        const val APPLOVIN = "applovin"
        const val STARTAPP = "startapp"
        const val UNITY = "unity"
    }
}
