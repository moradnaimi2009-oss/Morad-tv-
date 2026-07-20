package com.lite.streamvault.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.lite.streamvault.domain.model.Anime
import com.lite.streamvault.domain.model.AppSettings
import com.lite.streamvault.domain.model.Movie
import com.lite.streamvault.ui.components.ShimmerBox
import com.lite.streamvault.ui.components.ShimmerPosterCard
import com.lite.streamvault.ui.theme.Blue400
import com.lite.streamvault.ui.theme.Blue500
import com.lite.streamvault.ui.theme.Blue700
import com.lite.streamvault.ui.theme.TextMuted
import com.lite.streamvault.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    settings: AppSettings,
    onMovieClick: (Movie) -> Unit,
    onAnimeClick: (Anime) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(settings) { viewModel.load(settings) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        if (state.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(20.dp))
                ) { ShimmerBox(Modifier.fillMaxSize()) }
            }
            item {
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(2),
                    modifier = Modifier.height(440.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(6) { ShimmerPosterCard() }
                }
            }
        } else {
            // Hero pager — interleave top 4 movies + top 3 anime, capped at 7.
            val heroMovies = state.movies.take(4)
            val heroAnime = state.anime.take(3)
            val heroes: List<HeroItem> = buildList {
                val max = maxOf(heroMovies.size, heroAnime.size)
                for (i in 0 until max) {
                    if (i < heroMovies.size) add(HeroItem.Movie(heroMovies[i]))
                    if (i < heroAnime.size) add(HeroItem.Anime(heroAnime[i]))
                }
            }.take(7)

            if (heroes.isNotEmpty()) {
                item { HeroPager(heroes, onMovieClick, onAnimeClick) }
            }

            if (settings.enableMovies && state.movies.isNotEmpty()) {
                item {
                    SectionHeader(icon = Icons.Filled.Movie, title = "Movies")
                }
                item {
                    LazyHorizontalGrid(
                        rows = GridCells.Fixed(3),
                        modifier = Modifier.height(640.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(state.movies) { movie ->
                            PosterCard(
                                title = movie.title,
                                subtitle = movie.releaseYear,
                                posterUrl = movie.posterUrl,
                                onClick = { onMovieClick(movie) }
                            )
                        }
                    }
                }
            }

            if (settings.enableAnime && state.anime.isNotEmpty()) {
                item {
                    SectionHeader(icon = Icons.Filled.Movie, title = "Anime")
                }
                item {
                    LazyHorizontalGrid(
                        rows = GridCells.Fixed(3),
                        modifier = Modifier.height(640.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(state.anime) { anime ->
                            PosterCard(
                                title = anime.title,
                                subtitle = anime.categoryName ?: "",
                                posterUrl = anime.posterUrl,
                                episodeBadge = anime.episodeCount.takeIf { it > 0 }
                                    ?.let { "$it EP" },
                                onClick = { onAnimeClick(anime) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private sealed class HeroItem {
    data class Movie(val movie: com.lite.streamvault.domain.model.Movie) : HeroItem()
    data class Anime(val anime: com.lite.streamvault.domain.model.Anime) : HeroItem()
}

@Composable
private fun HeroPager(
    heroes: List<HeroItem>,
    onMovieClick: (Movie) -> Unit,
    onAnimeClick: (Anime) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { heroes.size })
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val hero = heroes[page]
            val title = when (hero) {
                is HeroItem.Movie -> hero.movie.title
                is HeroItem.Anime -> hero.anime.title
            }
            val poster = when (hero) {
                is HeroItem.Movie -> hero.movie.posterUrl
                is HeroItem.Anime -> hero.anime.posterUrl
            }
            val category = when (hero) {
                is HeroItem.Movie -> hero.movie.categoryName ?: "Movie"
                is HeroItem.Anime -> hero.anime.categoryName ?: "Anime"
            }
            HeroCard(
                title = title,
                category = category,
                posterUrl = poster,
                onClick = {
                    when (hero) {
                        is HeroItem.Movie -> onMovieClick(hero.movie)
                        is HeroItem.Anime -> onAnimeClick(hero.anime)
                    }
                }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(heroes.size) { i ->
                val selected = pagerState.currentPage == i
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (selected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (selected) Blue400 else Color.White.copy(alpha = 0.3f))
                )
            }
        }
    }
}

@Composable
private fun HeroCard(
    title: String,
    category: String,
    posterUrl: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        if (!posterUrl.isNullOrBlank()) {
            AsyncImage(
                model = posterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Blue700.copy(alpha = 0.3f), Blue500.copy(alpha = 0.1f))
                        )
                    )
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = category.uppercase(),
                color = Blue400,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Blue500.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Blue400, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PosterCard(
    title: String,
    subtitle: String,
    posterUrl: String?,
    episodeBadge: String? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.67f)
                .clip(RoundedCornerShape(14.dp))
        ) {
            if (!posterUrl.isNullOrBlank()) {
                AsyncImage(
                    model = posterUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Blue700.copy(alpha = 0.3f), Blue500.copy(alpha = 0.1f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Movie, contentDescription = null, tint = Color.White.copy(alpha = 0.5f))
                }
            }
            if (episodeBadge != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Blue500.copy(alpha = 0.9f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = episodeBadge,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                color = TextMuted,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
