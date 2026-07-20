package com.lite.streamvault.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lite.streamvault.domain.model.Anime
import com.lite.streamvault.domain.model.AnimeEpisode
import com.lite.streamvault.domain.model.Movie
import com.lite.streamvault.ui.components.GlassmorphismCard
import com.lite.streamvault.ui.theme.Blue400
import com.lite.streamvault.ui.theme.Blue500
import com.lite.streamvault.ui.theme.DarkBg
import com.lite.streamvault.ui.theme.TextMuted
import com.lite.streamvault.ui.theme.TextSecondary

@Composable
fun MovieDetailScreen(
    movie: Movie,
    onBack: () -> Unit,
    onPlay: (Movie) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.75f)
                ) {
                    if (!movie.posterUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = movie.posterUrl,
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
                                        colors = listOf(Blue500.copy(alpha = 0.3f), DarkBg)
                                    )
                                )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, DarkBg)
                                )
                            )
                    )
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            }
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = movie.title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = Blue400, modifier = Modifier.size(16.dp))
                        Text(
                            text = " ${movie.releaseYear}",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.width(12.dp))
                        Icon(Icons.Filled.Category, contentDescription = null, tint = Blue400, modifier = Modifier.size(16.dp))
                        Text(
                            text = " ${movie.categoryName ?: ""}",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    NativeAdPlaceholder()
                    Spacer(Modifier.height(12.dp))
                    Text("Synopsis", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = movie.description.ifBlank { "No description available." },
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Blue500)
                            .clickable { onPlay(movie) },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
                            Text("Watch Now", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun AnimeDetailScreen(
    anime: Anime,
    episodes: List<AnimeEpisode>,
    onBack: () -> Unit,
    onEpisodeClick: (AnimeEpisode, String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.8f)
                ) {
                    if (!anime.posterUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = anime.posterUrl,
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
                                        colors = listOf(Blue500.copy(alpha = 0.3f), DarkBg)
                                    )
                                )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, DarkBg)
                                )
                            )
                    )
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            }
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = anime.title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = Blue400, modifier = Modifier.size(16.dp))
                        Text(
                            text = " ${anime.releaseYear}",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "${anime.episodeCount} Episodes",
                            color = Blue400,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    NativeAdPlaceholder()
                    Spacer(Modifier.height(12.dp))
                    Text("Synopsis", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = anime.description.ifBlank { "No description available." },
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(Modifier.height(20.dp))
                    Text("Episodes", color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                }
            }
            items(episodes) { ep ->
                EpisodeItem(
                    episode = ep,
                    animeTitle = anime.title,
                    onClick = { onEpisodeClick(ep, "${anime.title} - EP ${ep.episodeNumber}") }
                )
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun EpisodeItem(
    episode: AnimeEpisode,
    animeTitle: String,
    onClick: () -> Unit
) {
    GlassmorphismCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        cornerRadius = 16
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Blue500.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${episode.episodeNumber}",
                    color = Blue400,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = episode.title ?: "Episode ${episode.episodeNumber}",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Filled.PlayCircle,
                contentDescription = "Play",
                tint = Blue400,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun NativeAdPlaceholder() {
    GlassmorphismCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        cornerRadius = 16,
        alpha = 0.06f
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Ad", color = TextMuted, style = MaterialTheme.typography.labelMedium)
        }
    }
}
