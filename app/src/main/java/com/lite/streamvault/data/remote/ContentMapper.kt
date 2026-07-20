package com.lite.streamvault.data.remote

import com.lite.streamvault.data.dto.AdCampaignDto
import com.lite.streamvault.data.dto.AnimeDto
import com.lite.streamvault.data.dto.AnimeEpisodeDto
import com.lite.streamvault.data.dto.CategoryDto
import com.lite.streamvault.data.dto.ChannelDto
import com.lite.streamvault.data.dto.MovieDto
import com.lite.streamvault.data.dto.SettingsDto
import com.lite.streamvault.domain.model.AdCampaign
import com.lite.streamvault.domain.model.Anime
import com.lite.streamvault.domain.model.AnimeEpisode
import com.lite.streamvault.domain.model.AppSettings
import com.lite.streamvault.domain.model.Category
import com.lite.streamvault.domain.model.Channel
import com.lite.streamvault.domain.model.Movie
import com.lite.streamvault.util.Constants

object ContentMapper {

    fun getFullUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        if (path.startsWith("http://") || path.startsWith("https://")) return path
        val base = Constants.BASE_URL.trimEnd('/')
        return "$base/${path.trimStart('/')}"
    }

    fun SettingsDto.toDomain(): AppSettings = AppSettings(
        appName = appName ?: "Morad TV",
        appVersion = appVersion ?: "1.0.0",
        appDescription = appDescription ?: "",
        maintenanceMode = maintenanceMode ?: false,
        maintenanceMessage = maintenanceMessage ?: "",
        forceUpdate = forceUpdate ?: false,
        showAds = showAds ?: true,
        enableChannels = enableChannels ?: true,
        enableMovies = enableMovies ?: true,
        enableAnime = enableAnime ?: true,
        updateEnabled = updateEnabled ?: false,
        updateVersion = updateVersion ?: "",
        updateUrl = updateUrl ?: "",
        updateMessage = updateMessage ?: "",
        supportEmail = supportEmail ?: "",
        privacyUrl = privacyUrl ?: "",
        termsOfService = termsOfService ?: ""
    )

    fun CategoryDto.toDomain(): Category = Category(
        id = id ?: 0,
        name = name ?: "",
        type = type ?: "",
        imageUrl = getFullUrl(imageUrl)
    )

    fun ChannelDto.toDomain(): Channel = Channel(
        id = id ?: 0,
        name = name ?: "",
        streamUrl = streamUrl ?: "",
        logoUrl = getFullUrl(logoUrl),
        categoryId = categoryId,
        categoryName = categoryName,
        country = country,
        language = language,
        isActive = isActive ?: true
    )

    fun MovieDto.toDomain(): Movie = Movie(
        id = id ?: 0,
        title = title ?: "",
        description = description ?: "",
        posterUrl = getFullUrl(posterUrl),
        streamUrl = streamUrl ?: "",
        releaseYear = releaseYear ?: "",
        categoryId = categoryId,
        categoryName = categoryName,
        duration = duration ?: ""
    )

    fun AnimeDto.toDomain(): Anime = Anime(
        id = id ?: 0,
        title = title ?: "",
        description = description ?: "",
        posterUrl = getFullUrl(posterUrl),
        releaseYear = releaseYear ?: "",
        categoryId = categoryId,
        categoryName = categoryName,
        episodeCount = episodeCount ?: (episodes?.size ?: 0)
    )

    fun AnimeEpisodeDto.toDomain(): AnimeEpisode = AnimeEpisode(
        id = id ?: 0,
        animeId = animeId ?: animeIdSnake ?: 0,
        episodeNumber = episodeNumber ?: episodeNumberSnake ?: 0,
        title = title,
        streamUrl = streamUrl ?: streamUrlSnake ?: "",
        duration = duration ?: ""
    )

    fun AdCampaignDto.toDomain(): AdCampaign = AdCampaign(
        id = id ?: 0,
        network = network ?: "",
        isActive = isActive ?: false,
        bannerId = bannerId,
        interstitialId = interstitialId,
        appId = appId,
        priority = priority ?: 0
    )
}
