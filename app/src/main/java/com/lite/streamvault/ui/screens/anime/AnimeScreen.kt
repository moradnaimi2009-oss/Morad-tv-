package com.lite.streamvault.ui.screens.anime

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.lite.streamvault.domain.model.Anime
import com.lite.streamvault.ui.components.ShimmerBox
import com.lite.streamvault.ui.components.ShimmerPosterCard
import com.lite.streamvault.ui.screens.home.PosterCard
import com.lite.streamvault.ui.theme.Blue500
import com.lite.streamvault.ui.theme.Blue700
import com.lite.streamvault.viewmodel.AnimeViewModel
import androidx.compose.foundation.layout.Arrangement

@Composable
fun AnimeScreen(
    onAnimeClick: (Anime) -> Unit,
    viewModel: AnimeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.load() }

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
            items(3) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) { Box(Modifier.width(140.dp)) { ShimmerPosterCard() } }
                }
            }
        } else {
            val chunks = state.anime.chunked(7)
            chunks.forEach { chunk ->
                val hero = chunk.first()
                val grid = chunk.drop(1)
                item {
                    HeroAnime(hero) { onAnimeClick(hero) }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        grid.forEach { anime ->
                            Box(modifier = Modifier.weight(1f)) {
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
                        repeat(3 - grid.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroAnime(anime: Anime, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(20.dp))
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
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(Blue700.copy(alpha = 0.3f), Blue500.copy(alpha = 0.1f))
                        )
                    )
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
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
                text = anime.title,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (anime.episodeCount > 0) {
                Text(
                    text = "${anime.episodeCount} Episodes",
                    color = Blue500,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        if (anime.episodeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Blue500.copy(alpha = 0.9f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${anime.episodeCount} Episodes",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
