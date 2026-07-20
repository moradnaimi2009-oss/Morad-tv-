package com.lite.streamvault.data.repository

import android.util.Log
import com.google.gson.JsonSyntaxException
import com.lite.streamvault.data.remote.ContentMapper.toDomain
import com.lite.streamvault.data.remote.StreamVaultApi
import com.lite.streamvault.domain.model.AdCampaign
import com.lite.streamvault.domain.model.Anime
import com.lite.streamvault.domain.model.AnimeEpisode
import com.lite.streamvault.domain.model.AppSettings
import com.lite.streamvault.domain.model.Category
import com.lite.streamvault.domain.model.Channel
import com.lite.streamvault.domain.model.Movie
import javax.inject.Inject
import javax.inject.Singleton

interface StreamVaultRepository {
    suspend fun getSettings(): AppSettings
    suspend fun getCategories(type: String? = null): List<Category>
    suspend fun getChannels(): List<Channel>
    suspend fun getMovies(): List<Movie>
    suspend fun getAnime(): List<Anime>
    suspend fun getEpisodes(animeId: Int): List<AnimeEpisode>
    suspend fun getAds(): List<AdCampaign>
}

@Singleton
class StreamVaultRepositoryImpl @Inject constructor(
    private val api: StreamVaultApi
) : StreamVaultRepository {

    private companion object {
        const val TAG = "StreamVaultRepo"
    }

    override suspend fun getSettings(): AppSettings = safeCall(
        default = AppSettings(),
        block = { api.getSettings().toDomain() }
    )

    override suspend fun getCategories(type: String?): List<Category> = safeCall(
        default = emptyList(),
        block = { api.getCategories(type).map { it.toDomain() } }
    )

    override suspend fun getChannels(): List<Channel> = safeCall(
        default = emptyList(),
        block = { api.getChannels().map { it.toDomain() } }
    )

    override suspend fun getMovies(): List<Movie> = safeCall(
        default = emptyList(),
        block = { api.getMovies().map { it.toDomain() } }
    )

    override suspend fun getAnime(): List<Anime> = safeCall(
        default = emptyList(),
        block = { api.getAnime().map { it.toDomain() } }
    )

    override suspend fun getEpisodes(animeId: Int): List<AnimeEpisode> = safeCall(
        default = emptyList(),
        block = { api.getEpisodes(animeId).map { it.toDomain() } }
    )

    override suspend fun getAds(): List<AdCampaign> = safeCall(
        default = emptyList(),
        block = { api.getAds().map { it.toDomain() } }
    )

    private inline fun <T> safeCall(default: T, block: () -> T): T = try {
        block()
    } catch (e: JsonSyntaxException) {
        Log.e(TAG, "JSON parse error (server returned HTML?)", e)
        default
    } catch (e: Exception) {
        Log.e(TAG, "API call failed", e)
        default
    }
}
