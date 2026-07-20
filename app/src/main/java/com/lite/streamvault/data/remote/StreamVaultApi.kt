package com.lite.streamvault.data.remote

import com.lite.streamvault.data.dto.AdCampaignDto
import com.lite.streamvault.data.dto.AnimeDto
import com.lite.streamvault.data.dto.AnimeEpisodeDto
import com.lite.streamvault.data.dto.ApiListEnvelope
import com.lite.streamvault.data.dto.ApiObjectEnvelope
import com.lite.streamvault.data.dto.CategoryDto
import com.lite.streamvault.data.dto.ChannelDto
import com.lite.streamvault.data.dto.MovieDto
import com.lite.streamvault.data.dto.SettingsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface StreamVaultApi {

    @GET("settings")
    suspend fun getSettings(): ApiObjectEnvelope<SettingsDto>

    @GET("categories")
    suspend fun getCategories(@Query("type") type: String? = null): ApiListEnvelope<CategoryDto>

    @GET("channels")
    suspend fun getChannels(): ApiListEnvelope<ChannelDto>

    @GET("movies")
    suspend fun getMovies(): ApiListEnvelope<MovieDto>

    @GET("anime")
    suspend fun getAnime(@Query("id") id: Int? = null): ApiListEnvelope<AnimeDto>

    @GET("episodes")
    suspend fun getEpisodes(@Query("anime_id") animeId: Int): ApiListEnvelope<AnimeEpisodeDto>

    @GET("ads")
    suspend fun getAds(): ApiListEnvelope<AdCampaignDto>
}
