package com.lite.streamvault.data.remote

import com.lite.streamvault.data.dto.AdCampaignDto
import com.lite.streamvault.data.dto.AnimeDto
import com.lite.streamvault.data.dto.AnimeEpisodeDto
import com.lite.streamvault.data.dto.CartoonDto
import com.lite.streamvault.data.dto.CategoryDto
import com.lite.streamvault.data.dto.ChannelDto
import com.lite.streamvault.data.dto.LeaderboardRowDto
import com.lite.streamvault.data.dto.MovieDto
import com.lite.streamvault.data.dto.ReferralStatusDto
import com.lite.streamvault.data.dto.RedeemReferralResultDto
import com.lite.streamvault.data.dto.SettingRowDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    @GET("ad_campaigns?select=*")
    suspend fun getAdCampaigns(): List<AdCampaignDto>

    // Referral system — all writes go through Postgres RPC functions (security definer)
    // so a user can never edit their own referral_count directly via REST.
    @POST("rpc/ensure_referral_code")
    suspend fun ensureReferralCode(@Body body: Map<String, String>)

    @GET("referral_codes?select=referral_count,unlocked_restricted")
    suspend fun getReferralStatus(@Query("device_id") deviceIdFilter: String): List<ReferralStatusDto>

    @POST("rpc/redeem_referral")
    suspend fun redeemReferral(@Body body: Map<String, String>): RedeemReferralResultDto

    @GET("referral_codes?select=code,referral_count&order=referral_count.desc&limit=10")
    suspend fun getTopReferrers(): List<LeaderboardRowDto>
}
