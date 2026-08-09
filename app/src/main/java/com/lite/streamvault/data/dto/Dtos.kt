package com.lite.streamvault.data.dto

import com.google.gson.annotations.SerializedName

data class ChannelDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("category_id") val categoryId: Long? = null,
    @SerializedName("logo") val logo: String? = null,
    @SerializedName("stream_url") val streamUrl: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("language") val language: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("views") val views: Int = 0
)

data class MovieDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("title") val title: String? = null,
    @SerializedName("category_id") val categoryId: Long? = null,
    @SerializedName("poster") val poster: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("year") val year: Int? = null,
    @SerializedName("duration") val duration: Int? = null,
    @SerializedName("video_url") val videoUrl: String? = null,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("views") val views: Int = 0
)

data class AnimeDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("title") val title: String? = null,
    @SerializedName("category_id") val categoryId: Long? = null,
    @SerializedName("poster") val poster: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("trailer_url") val trailerUrl: String? = null,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("views") val views: Int = 0
)

data class CartoonDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("title") val title: String? = null,
    @SerializedName("category_id") val categoryId: Long? = null,
    @SerializedName("poster") val poster: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("trailer_url") val trailerUrl: String? = null,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("views") val views: Int = 0,
    @SerializedName("is_restricted") val isRestricted: Boolean = false
)

data class AnimeEpisodeDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("anime_id") val animeId: Long? = null,
    @SerializedName("cartoon_id") val cartoonId: Long? = null,
    @SerializedName("episode_number") val episodeNumber: Int = 0,
    @SerializedName("season") val season: Int = 1,
    @SerializedName("title") val title: String? = null,
    @SerializedName("video_url") val videoUrl: String? = null,
    @SerializedName("is_restricted") val isRestricted: Boolean = false
)

data class CategoryDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("icon") val icon: String? = null
)

data class SettingRowDto(
    @SerializedName("key") val key: String,
    @SerializedName("value") val value: String?
)

data class ReferralStatusDto(
    @SerializedName("referral_count") val referralCount: Int = 0,
    @SerializedName("unlocked_restricted") val unlockedRestricted: Boolean = false
)

data class RedeemReferralResultDto(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("message") val message: String? = null,
    @SerializedName("referral_count") val referralCount: Int = 0
)

data class AdCampaignDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("network") val network: String? = null,
    @SerializedName("is_active") val isActive: Boolean = false,
    @SerializedName("banner_id") val bannerId: String? = null,
    @SerializedName("interstitial_id") val interstitialId: String? = null,
    @SerializedName("app_id") val appId: String? = null,
    @SerializedName("priority") val priority: Int = 0
)

data class LeaderboardRowDto(
    @SerializedName("code") val code: String = "",
    @SerializedName("referral_count") val referralCount: Int = 0
)
