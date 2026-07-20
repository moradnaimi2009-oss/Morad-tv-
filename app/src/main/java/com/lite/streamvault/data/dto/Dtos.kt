package com.lite.streamvault.data.dto

import com.google.gson.annotations.SerializedName

data class ApiListEnvelope<T>(
    @SerializedName("ok") val ok: Boolean? = null,
    @SerializedName("data") val data: ApiItems<T>? = null
)

data class ApiItems<T>(
    @SerializedName("items") val items: List<T>? = null
)

data class ApiObjectEnvelope<T>(
    @SerializedName("ok") val ok: Boolean? = null,
    @SerializedName("data") val data: T? = null
)

data class SettingsDto(
    @SerializedName("appName") val appName: String? = null,
    @SerializedName("appVersion") val appVersion: String? = null,
    @SerializedName("appDescription") val appDescription: String? = null,
    @SerializedName("maintenanceMode") val maintenanceMode: Boolean? = null,
    @SerializedName("maintenanceMessage") val maintenanceMessage: String? = null,
    @SerializedName("forceUpdate") val forceUpdate: Boolean? = null,
    @SerializedName("showAds") val showAds: Boolean? = null,
    @SerializedName("enableChannels") val enableChannels: Boolean? = null,
    @SerializedName("enableMovies") val enableMovies: Boolean? = null,
    @SerializedName("enableAnime") val enableAnime: Boolean? = null,
    @SerializedName("updateEnabled") val updateEnabled: Boolean? = null,
    @SerializedName("updateVersion") val updateVersion: String? = null,
    @SerializedName("updateUrl") val updateUrl: String? = null,
    @SerializedName("updateMessage") val updateMessage: String? = null,
    @SerializedName("supportEmail") val supportEmail: String? = null,
    @SerializedName("privacyUrl") val privacyUrl: String? = null,
    @SerializedName("termsOfService") val termsOfService: String? = null
)

data class CategoryDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null
)

data class ChannelDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("streamUrl") val streamUrl: String? = null,
    @SerializedName(value = "logoUrl", alternate = ["logo"]) val logoUrl: String? = null,
    @SerializedName("categoryId") val categoryId: Int? = null,
    @SerializedName("categoryName") val categoryName: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("language") val language: String? = null,
    @SerializedName("isActive") val isActive: Boolean? = null
)

data class MovieDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("posterUrl") val posterUrl: String? = null,
    @SerializedName("streamUrl") val streamUrl: String? = null,
    @SerializedName("releaseYear") val releaseYear: String? = null,
    @SerializedName("categoryId") val categoryId: Int? = null,
    @SerializedName("categoryName") val categoryName: String? = null,
    @SerializedName("duration") val duration: String? = null
)

data class AnimeDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("posterUrl") val posterUrl: String? = null,
    @SerializedName("releaseYear") val releaseYear: String? = null,
    @SerializedName("categoryId") val categoryId: Int? = null,
    @SerializedName("categoryName") val categoryName: String? = null,
    @SerializedName("episodeCount") val episodeCount: Int? = null,
    @SerializedName("episodes") val episodes: List<AnimeEpisodeDto>? = null
)

data class AnimeEpisodeDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("animeId") val animeId: Int? = null,
    @SerializedName("anime_id") val animeIdSnake: Int? = null,
    @SerializedName("episodeNumber") val episodeNumber: Int? = null,
    @SerializedName("episode_number") val episodeNumberSnake: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("streamUrl") val streamUrl: String? = null,
    @SerializedName("stream_url") val streamUrlSnake: String? = null,
    @SerializedName("duration") val duration: String? = null
)

data class AdCampaignDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("network") val network: String? = null,
    @SerializedName("isActive") val isActive: Boolean? = null,
    @SerializedName("bannerId") val bannerId: String? = null,
    @SerializedName("interstitialId") val interstitialId: String? = null,
    @SerializedName("appId") val appId: String? = null,
    @SerializedName("priority") val priority: Int? = null
)
