package com.lite.streamvault.data.remote

import com.lite.streamvault.data.dto.AnimeDto
import com.lite.streamvault.data.dto.AnimeEpisodeDto
import com.lite.streamvault.data.dto.CategoryDto
import com.lite.streamvault.data.dto.ChannelDto
import com.lite.streamvault.data.dto.MovieDto
import com.lite.streamvault.data.dto.SettingRowDto
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
        val base = Constants.SUPABASE_URL.trimEnd('/')
        val cleanPath = path.trimStart('/')
        // Supabase Storage public objects live under storage/v1/object/public/<bucket>/<path>.
        // If the stored value already includes that prefix, don't add it twice.
        return if (cleanPath.startsWith("storage/v1/object/public/")) {
            "$base/$cleanPath"
        } else {
            "$base/storage/v1/object/public/$cleanPath"
        }
    }

    fun List<SettingRowDto>.toAppSettings(): AppSettings {
        val map = this.associate { it.key to (it.value ?: "") }
        return AppSettings(
            appName = map["app_name"] ?: "Morad TV",
            appVersion = map["app_version"] ?: "1.0.0",
            appDescription = map["app_description"] ?: "",
            maintenanceMode = map["maintenance_mode"] == "true",
            maintenanceMessage = map["maintenance_message"] ?: "",
            forceUpdate = map["force_update"] == "true",
            showAds = map["show_ads"] != "false",
            enableChannels = map["enable_channels"] != "false",
            enableMovies = map["enable_movies"] != "false",
            enableAnime = map["enable_anime"] != "false",
            updateEnabled = map["update_enabled"] == "true",
            updateVersion = map["update_version"] ?: "",
            updateUrl = map["update_url"] ?: "",
            updateMessage = map["update_message"] ?: "",
            supportEmail = map["support_email"] ?: "",
            privacyUrl = map["privacy_url"] ?: "",
            termsOfService = map["terms_of_service"] ?: ""
        )
    }

    fun CategoryDto.toDomain(): Category = Category(
        id = id.toInt(),
        name = name ?: "",
        type = type ?: "",
        imageUrl = getFullUrl(icon)
    )

    fun ChannelDto.toDomain(): Channel = Channel(
        id = id.toInt(),
        name = name ?: "",
        streamUrl = streamUrl ?: "",
        logoUrl = getFullUrl(logo),
        categoryId = categoryId?.toInt(),
        categoryName = "",
        country = country,
        language = language,
        isActive = isActive
    )

    fun MovieDto.toDomain(): Movie = Movie(
        id = id.toInt(),
        title = title ?: "",
        description = description ?: "",
        posterUrl = getFullUrl(poster),
        streamUrl = videoUrl ?: "",
        releaseYear = year?.toString() ?: "",
        categoryId = categoryId?.toInt(),
        categoryName = "",
        duration = duration?.toString() ?: ""
    )

    fun AnimeDto.toDomain(): Anime = Anime(
        id = id.toInt(),
        title = title ?: "",
        description = description ?: "",
        posterUrl = getFullUrl(poster),
        releaseYear = "",
        categoryId = categoryId?.toInt(),
        categoryName = "",
        episodeCount = 0
    )

    fun AnimeEpisodeDto.toDomain(): AnimeEpisode = AnimeEpisode(
        id = id.toInt(),
        animeId = (animeId ?: cartoonId ?: 0).toInt(),
        episodeNumber = episodeNumber,
        title = title ?: "",
        streamUrl = videoUrl ?: "",
        duration = ""
    )
}
