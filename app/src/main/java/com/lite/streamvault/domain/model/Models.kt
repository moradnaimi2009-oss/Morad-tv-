package com.lite.streamvault.domain.model

data class AppSettings(
    val appName: String = "Morad TV",
    val appVersion: String = "1.0.0",
    val appDescription: String = "",
    val maintenanceMode: Boolean = false,
    val maintenanceMessage: String = "",
    val forceUpdate: Boolean = false,
    val showAds: Boolean = true,
    val enableChannels: Boolean = true,
    val enableMovies: Boolean = true,
    val enableAnime: Boolean = true,
    val updateEnabled: Boolean = false,
    val updateVersion: String = "",
    val updateUrl: String = "",
    val updateMessage: String = "",
    val supportEmail: String = "",
    val privacyUrl: String = "",
    val termsOfService: String = ""
)

data class Category(
    val id: Int,
    val name: String,
    val type: String,
    val imageUrl: String? = null
)

data class Channel(
    val id: Int,
    val name: String,
    val streamUrl: String,
    val logoUrl: String? = null,
    val categoryId: Int? = null,
    val categoryName: String? = null,
    val country: String? = null,
    val language: String? = null,
    val isActive: Boolean = true
)

data class Movie(
    val id: Int,
    val title: String,
    val description: String = "",
    val posterUrl: String? = null,
    val streamUrl: String = "",
    val releaseYear: String = "",
    val categoryId: Int? = null,
    val categoryName: String? = null,
    val duration: String = ""
)

data class Anime(
    val id: Int,
    val title: String,
    val description: String = "",
    val posterUrl: String? = null,
    val releaseYear: String = "",
    val categoryId: Int? = null,
    val categoryName: String? = null,
    val episodeCount: Int = 0
)

data class AnimeEpisode(
    val id: Int,
    val animeId: Int,
    val episodeNumber: Int,
    val title: String? = null,
    val streamUrl: String = "",
    val duration: String = ""
)

data class AdCampaign(
    val id: Int,
    val network: String,
    val isActive: Boolean,
    val bannerId: String? = null,
    val interstitialId: String? = null,
    val appId: String? = null,
    val priority: Int = 0
)

sealed class SearchResult {
    data class MovieResult(val movie: Movie) : SearchResult()
    data class AnimeResult(val anime: Anime) : SearchResult()
    data class ChannelResult(val channel: Channel) : SearchResult()
}
