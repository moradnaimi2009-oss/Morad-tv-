package com.lite.streamvault.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lite.streamvault.domain.model.AppSettings
import com.lite.streamvault.domain.model.Channel
import com.lite.streamvault.ui.components.ShimmerBox
import com.lite.streamvault.ui.screens.anime.AnimeScreen
import com.lite.streamvault.ui.screens.cartoons.CartoonsScreen
import com.lite.streamvault.ui.screens.channels.ChannelsScreen
import com.lite.streamvault.ui.screens.detail.AnimeDetailScreen
import com.lite.streamvault.ui.screens.detail.MovieDetailScreen
import com.lite.streamvault.ui.screens.home.HomeScreen
import com.lite.streamvault.ui.screens.movies.MoviesScreen
import com.lite.streamvault.ui.screens.mylist.MyListScreen
import com.lite.streamvault.ui.screens.player.PlayerScreen
import com.lite.streamvault.ui.screens.referral.ReferralScreen
import com.lite.streamvault.ui.screens.search.SearchScreen
import com.lite.streamvault.ui.screens.splash.SplashScreen
import com.lite.streamvault.viewmodel.AnimeDetailViewModel
import com.lite.streamvault.viewmodel.CartoonDetailViewModel
import com.lite.streamvault.viewmodel.MovieDetailViewModel
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

        composable(Routes.CARTOONS) {
            CartoonsScreen(
                onCartoonClick = { cartoon ->
                    navController.navigate(Routes.cartoonDetail(cartoon.id))
                },
                onLockedClick = { navController.navigate(Routes.REFERRAL) }
            )
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
            val viewModel: MovieDetailViewModel = hiltViewModel()
            LaunchedEffect(id) { viewModel.load(id) }
            val state by viewModel.state.collectAsState()
            val movie = state.movie

            when {
                movie != null -> MovieDetailScreen(
                    movie = movie,
                    onBack = { navController.popBackStack() },
                    onPlay = { m -> onPlayWithInterstitial(m.streamUrl, m.title, false) }
                )
                else -> DetailLoadingPlaceholder()
            }
        }

        composable(
            route = Routes.ANIME_DETAIL,
            arguments = listOf(navArgument("animeId") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("animeId") ?: 0
            val viewModel: AnimeDetailViewModel = hiltViewModel()
            LaunchedEffect(id) { viewModel.load(id) }
            val state by viewModel.state.collectAsState()
            val anime = state.anime

            when {
                anime != null -> AnimeDetailScreen(
                    anime = anime,
                    episodes = state.episodes,
                    onBack = { navController.popBackStack() },
                    onEpisodeClick = { ep, title -> onPlayWithInterstitial(ep.streamUrl, title, false) }
                )
                else -> DetailLoadingPlaceholder()
            }
        }

        composable(
            route = Routes.CARTOON_DETAIL,
            arguments = listOf(navArgument("cartoonId") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("cartoonId") ?: 0
            val viewModel: CartoonDetailViewModel = hiltViewModel()
            LaunchedEffect(id) { viewModel.load(id) }
            val state by viewModel.state.collectAsState()
            val cartoon = state.anime

            when {
                cartoon != null -> AnimeDetailScreen(
                    anime = cartoon,
                    episodes = state.episodes,
                    contentType = "cartoon",
                    unlockedRestricted = state.unlockedRestricted,
                    onBack = { navController.popBackStack() },
                    onEpisodeClick = { ep, title ->
                        if (ep.isRestricted && !state.unlockedRestricted) {
                            navController.navigate(Routes.REFERRAL)
                        } else {
                            onPlayWithInterstitial(ep.streamUrl, title, false)
                        }
                    }
                )
                else -> DetailLoadingPlaceholder()
            }
        }

        composable(Routes.REFERRAL) {
            ReferralScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.MY_LIST) {
            MyListScreen(
                onBack = { navController.popBackStack() },
                onMovieClick = { id -> navController.navigate(Routes.movieDetail(id)) },
                onAnimeClick = { id -> navController.navigate(Routes.animeDetail(id)) },
                onCartoonClick = { id -> navController.navigate(Routes.cartoonDetail(id)) }
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

@Composable
private fun DetailLoadingPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ShimmerBox(
            modifier = Modifier.fillMaxSize()
        )
    }
}
