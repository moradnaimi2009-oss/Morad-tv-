package com.lite.streamvault.ads

data class AdConfig(
    val network: String,
    val appId: String?,
    val bannerId: String?,
    val interstitialId: String?,
    val rewardedId: String? = null
) {
    companion object {
        const val ADMOB = "admob"
        const val APPLOVIN = "applovin"
        const val STARTAPP = "startapp"
        const val UNITY = "unity"
        const val ADSTERRA = "adsterra"
    }
}
