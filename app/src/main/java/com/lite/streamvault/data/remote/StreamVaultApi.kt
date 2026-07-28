package com.lite.streamvault.data.remote

import com.lite.streamvault.data.dto.AnimeDto
import com.lite.streamvault.data.dto.AnimeEpisodeDto
import com.lite.streamvault.data.dto.CartoonDto
import com.lite.streamvault.data.dto.CategoryDto
import com.lite.streamvault.data.dto.ChannelDto
import com.lite.streamvault.data.dto.MovieDto
import com.lite.streamvault.data.dto.SettingRowDto
import retrofit2.http.GET
import retrofit2.http.Query

interface StreamVaultApi {
    @GET("channels?select=*&is_active=eq.true")
    suspend fun getChannels(): List<ChannelDto>

    @GET("movies?select=*&is_active=eq.true")
    suspend fun getMovies(): List<MovieDto>

    @GET("anime?select=*&is_active=eq.true")
    suspend fun getAnime(): List<AnimeDto>

    @GET("cartoons?select=*&is_active=eq.true")
    suspend fun getCartoons(): List<CartoonDto>

    @GET("episodes?select=*")
    suspend fun getEpisodes(@Query("anime_id") animeIdFilter: String? = null): List<AnimeEpisodeDto>

    @GET("episodes?select=*")
    suspend fun getCartoonEpisodes(@Query("cartoon_id") cartoonIdFilter: String): List<AnimeEpisodeDto>

    @GET("categories?select=*")
    suspend fun getCategories(): List<CategoryDto>

    @GET("settings?select=*")
    suspend fun getSettings(): List<SettingRowDto>
}
