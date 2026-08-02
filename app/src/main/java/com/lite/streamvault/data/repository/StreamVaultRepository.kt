package com.lite.streamvault.data.repository

import android.util.Log
import com.google.gson.JsonSyntaxException
import com.lite.streamvault.data.remote.ContentMapper.toAppSettings
import com.lite.streamvault.data.remote.ContentMapper.toDomain
import com.lite.streamvault.data.remote.StreamVaultApi
import com.lite.streamvault.domain.model.AdCampaign
import com.lite.streamvault.domain.model.Anime
import com.lite.streamvault.domain.model.AnimeEpisode
import com.lite.streamvault.domain.model.AppSettings
import com.lite.streamvault.domain.model.Category
import com.lite.streamvault.domain.model.Channel
import com.lite.streamvault.domain.model.Movie
import com.lite.streamvault.domain.model.LeaderboardEntry
import com.lite.streamvault.domain.model.ReferralStatus
import javax.inject.Inject
import javax.inject.Singleton

interface StreamVaultRepository {
    suspend fun getSettings(): AppSettings
    suspend fun getCategories(type: String? = null): List<Category>
    suspend fun getChannels(): List<Channel>
    suspend fun getMovies(): List<Movie>
    suspend fun getAnime(): List<Anime>
    suspend fun getCartoons(): List<Anime>
    suspend fun getEpisodes(animeId: Int): List<AnimeEpisode>
    suspend fun getCartoonEpisodes(cartoonId: Int): List<AnimeEpisode>
    suspend fun getAds(): List<AdCampaign>
    suspend fun ensureReferralCode(deviceId: String, code: String)
    suspend fun getReferralStatus(deviceId: String): ReferralStatus
    suspend fun redeemReferral(code: String, newDeviceId: String): Pair<Boolean, String>
    suspend fun getTopReferrers(): List<LeaderboardEntry>
}

@Singleton
class StreamVaultRepositoryImpl @Inject constructor(
    private val api: StreamVaultApi
) : StreamVaultRepository {

    private companion object { const val TAG = "StreamVaultRepo" }

    override suspend fun getSettings(): AppSettings = safeCall(
        default = AppSettings(),
        block = { api.getSettings().toAppSettings() }
    )

    override suspend fun getCategories(type: String?): List<Category> = safeCall(
        default = emptyList(),
        block = { api.getCategories().filter { type == null || it.type == type }.map { it.toDomain() } }
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

    override suspend fun getCartoons(): List<Anime> = safeCall(
        default = emptyList(),
        block = { api.getCartoons().map { it.toDomain() } }
    )

    override suspend fun getEpisodes(animeId: Int): List<AnimeEpisode> = safeCall(
        default = emptyList(),
        block = { api.getEpisodes("eq.$animeId").map { it.toDomain() } }
    )

    override suspend fun getCartoonEpisodes(cartoonId: Int): List<AnimeEpisode> = safeCall(
        default = emptyList(),
        block = { api.getCartoonEpisodes("eq.$cartoonId").map { it.toDomain() } }
    )

    // فعّلنا الجدول: يجيب الحملات الفعالة، الأولوية الأقل رقم = تُختار أول
    override suspend fun getAds(): List<AdCampaign> = safeCall(
        default = emptyList(),
        block = { api.getAdCampaigns().map { it.toDomain() }.filter { it.isActive }.sortedBy { it.priority } }
    )

    override suspend fun ensureReferralCode(deviceId: String, code: String) = safeCall(
        default = Unit,
        block = { api.ensureReferralCode(mapOf("p_device_id" to deviceId, "p_code" to code)) }
    )

    override suspend fun getReferralStatus(deviceId: String): ReferralStatus = safeCall(
        default = ReferralStatus(),
        block = {
            val row = api.getReferralStatus("eq.$deviceId").firstOrNull()
            ReferralStatus(
                referralCount = row?.referralCount ?: 0,
                unlockedRestricted = row?.unlockedRestricted ?: false
            )
        }
    )

    override suspend fun redeemReferral(code: String, newDeviceId: String): Pair<Boolean, String> = safeCall(
        default = false to "error",
        block = {
            val result = api.redeemReferral(mapOf("p_code" to code, "p_new_device_id" to newDeviceId))
            result.success to (result.message ?: "")
        }
    )

    override suspend fun getTopReferrers(): List<LeaderboardEntry> = safeCall(
        default = emptyList(),
        block = { api.getTopReferrers().map { LeaderboardEntry(it.code, it.referralCount) } }
    )

    private inline fun <T> safeCall(default: T, block: () -> T): T = try {
        block()
    } catch (e: JsonSyntaxException) {
        Log.e(TAG, "JSON parse error", e)
        default
    } catch (e: Exception) {
        Log.e(TAG, "API call failed", e)
        default
    }
}
