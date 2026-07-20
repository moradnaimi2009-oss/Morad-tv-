package com.lite.streamvault.data.remote

import com.lite.streamvault.data.dto.AdCampaignDto
import com.lite.streamvault.data.dto.AnimeDto
import com.lite.streamvault.data.dto.AnimeEpisodeDto
import com.lite.streamvault.data.dto.CategoryDto
import com.lite.streamvault.data.dto.ChannelDto
import com.lite.streamvault.data.dto.MovieDto
import com.lite.streamvault.data.dto.SettingsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface StreamVaultApi {

    @GET("settings")
    suspend fun getSettings(): SettingsDto

    @GET("categories")
    suspend fun getCategories(@Query("type") type: String? = null): List<CategoryDto>

    @GET("channels")
    suspend fun getChannels(): List<ChannelDto>

    @GET("movies")
    suspend fun getMovies(): List<MovieDto>

    @GET("anime")
    suspend fun getAnime(@Query("id") id: Int? = null): List<AnimeDto>

    @GET("episodes")
    suspend fun getEpisodes(@Query("anime_id") animeId: Int): List<AnimeEpisodeDto>

    @GET("ads")
    suspend fun getAds(): List<AdCampaignDto>
}
