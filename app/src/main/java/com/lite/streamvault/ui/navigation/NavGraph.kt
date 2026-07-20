package com.lite.streamvault.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lite.streamvault.domain.model.Anime
import com.lite.streamvault.domain.model.AppSettings
import com.lite.streamvault.domain.model.Channel
import com.lite.streamvault.domain.model.Movie
import com.lite.streamvault.ui.screens.anime.AnimeScreen
import com.lite.streamvault.ui.screens.channels.ChannelsScreen
import com.lite.streamvault.ui.screens.detail.AnimeDetailScreen
import com.lite.streamvault.ui.screens.detail.MovieDetailScreen
import com.lite.streamvault.ui.screens.home.HomeScreen
import com.lite.streamvault.ui.screens.movies.MoviesScreen
import com.lite.streamvault.ui.screens.player.PlayerScreen
import com.lite.streamvault.ui.screens.search.SearchScreen
import com.lite.streamvault.ui.screens.splash.SplashScreen
import java.net.URLDecoder

@Composable
fun NavGraph(
    navController: NavHostController,
    settings: AppSettings,
    onSettingsReady: (AppSettings) -> Unit,
    onPlayWithInterstitial: (String, String, Boolean) -> Unit
) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(onReady = { s ->
                onSettingsReady(s)
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Routes.HOME) {
            HomeScreen(
                settings = settings,
                onMovieClick = { movie -> navController.navigate(Routes.movieDetail(movie.id)) },
                onAnimeClick = { anime -> navController.navigate(Routes.animeDetail(anime.id)) }
            )
        }

        composable(Routes.CHANNELS) {
            ChannelsScreen(onChannelClick = { ch ->
                onPlayWithInterstitial(ch.streamUrl, ch.name, true)
            })
        }

        composable(Routes.MOVIES) {
            MoviesScreen(onMovieClick = { movie ->
                navController.navigate(Routes.movieDetail(movie.id))
            })
        }

        composable(Routes.ANIME) {
            AnimeScreen(onAnimeClick = { anime ->
                navController.navigate(Routes.animeDetail(anime.id))
            })
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onMovieClick = { id -> navController.navigate(Routes.movieDetail(id)) },
                onAnimeClick = { id -> navController.navigate(Routes.animeDetail(id)) },
                onChannelClick = { ch -> onPlayWithInterstitial(ch.streamUrl, ch.name, true) }
            )
        }

        composable(
            route = Routes.MOVIE_DETAIL,
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("movieId") ?: 0
            val movie = Movie(id = id, title = "", streamUrl = "")
            MovieDetailScreen(
                movie = movie,
                onBack = { navController.popBackStack() },
                onPlay = { m -> onPlayWithInterstitial(m.streamUrl, m.title, false) }
            )
        }

        composable(
            route = Routes.ANIME_DETAIL,
            arguments = listOf(navArgument("animeId") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("animeId") ?: 0
            val anime = Anime(id = id, title = "")
            AnimeDetailScreen(
                anime = anime,
                episodes = emptyList(),
                onBack = { navController.popBackStack() },
                onEpisodeClick = { ep, title -> onPlayWithInterstitial(ep.streamUrl, title, false) }
            )
        }

        composable(
            route = Routes.PLAYER,
            arguments = listOf(
                navArgument("videoUrl") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("isLive") { type = NavType.BoolType }
            )
        ) { entry ->
            val url = URLDecoder.decode(entry.arguments?.getString("videoUrl") ?: "", "UTF-8")
            val title = URLDecoder.decode(entry.arguments?.getString("title") ?: "", "UTF-8")
            val isLive = entry.arguments?.getBoolean("isLive") ?: false
            PlayerScreen(
                videoUrl = url,
                title = title,
                isLive = isLive,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
