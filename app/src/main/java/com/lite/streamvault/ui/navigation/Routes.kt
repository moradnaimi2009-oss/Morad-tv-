package com.lite.streamvault.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val CHANNELS = "channels"
    const val MOVIES = "movies"
    const val ANIME = "anime"
    const val SEARCH = "search"
    const val MOVIE_DETAIL = "movie_detail/{movieId}"
    const val ANIME_DETAIL = "anime_detail/{animeId}"
    const val PLAYER = "player/{videoUrl}/{title}/{isLive}"

    fun movieDetail(id: Int) = "movie_detail/$id"
    fun animeDetail(id: Int) = "anime_detail/$id"
    fun player(videoUrl: String, title: String, isLive: Boolean) =
        "player/${java.net.URLEncoder.encode(videoUrl, "UTF-8")}/${java.net.URLEncoder.encode(title, "UTF-8")}/$isLive"
}
