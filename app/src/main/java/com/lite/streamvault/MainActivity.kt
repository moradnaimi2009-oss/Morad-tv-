package com.lite.streamvault

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lite.streamvault.ads.AdManager
import com.lite.streamvault.domain.model.AppSettings
import com.lite.streamvault.ui.components.BannerAdView
import com.lite.streamvault.ui.navigation.NavGraph
import com.lite.streamvault.ui.navigation.Routes
import com.lite.streamvault.ui.theme.Blue400
import com.lite.streamvault.ui.theme.Blue500
import com.lite.streamvault.ui.theme.DarkBg
import com.lite.streamvault.ui.theme.DarkCard
import com.lite.streamvault.ui.theme.MoradTvTheme
import com.lite.streamvault.ui.theme.TextMuted
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var adManager: AdManager

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            MoradTvTheme {
                AppRoot(
                    adManager = adManager,
                    activity = this
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adManager.destroy()
    }
}

@Composable
private fun AppRoot(
    adManager: AdManager,
    activity: ComponentActivity
) {
    val navController = rememberNavController()
    var settings by remember { mutableStateOf(AppSettings()) }

    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    val showChrome = currentRoute in setOf(
        Routes.HOME, Routes.CHANNELS, Routes.MOVIES, Routes.ANIME
    )

    fun playWithInterstitial(videoUrl: String, title: String, isLive: Boolean) {
        adManager.showInterstitial(activity) {
            navController.navigate(Routes.player(videoUrl, title, isLive))
        }
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            if (showChrome) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val title = when (currentRoute) {
                        Routes.HOME -> settings.appName
                        Routes.CHANNELS -> "Live Channels"
                        Routes.MOVIES -> "Movies"
                        Routes.ANIME -> "Anime"
                        else -> "Morad TV"
                    }
                    Text(
                        text = title,
                        color = Blue400,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { navController.navigate(Routes.SEARCH) }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                }
            }
        },
        bottomBar = {
            if (showChrome) {
                Column {
                    BannerAdView(
                        adUnitId = com.lite.streamvault.util.Constants.ADMOB_BANNER_ID,
                        showAds = settings.showAds
                    )
                    NavigationBar(
                        containerColor = DarkCard,
                        tonalElevation = 0.dp,
                        modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    ) {
                        val items = buildList {
                            add(Triple(Routes.HOME, "Home", Icons.Filled.Home))
                            if (settings.enableChannels) add(Triple(Routes.CHANNELS, "Channels", Icons.Filled.LiveTv))
                            if (settings.enableMovies) add(Triple(Routes.MOVIES, "Movies", Icons.Filled.Movie))
                            if (settings.enableAnime) add(Triple(Routes.ANIME, "Anime", Icons.Filled.PlayArrow))
                        }
                        items.forEach { (route, label, icon) ->
                            val selected = currentRoute == route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(route) {
                                        popUpTo(Routes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Blue400,
                                    selectedTextColor = Blue400,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = Blue500.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
                .padding(padding)
        ) {
            NavGraph(
                navController = navController,
                settings = settings,
                onSettingsReady = { settings = it },
                onPlayWithInterstitial = ::playWithInterstitial
            )
        }
    }
}
