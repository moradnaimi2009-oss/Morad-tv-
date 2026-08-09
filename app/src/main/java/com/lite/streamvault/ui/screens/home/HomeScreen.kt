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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.lite.streamvault.domain.model.Anime
import com.lite.streamvault.domain.model.AppSettings
import com.lite.streamvault.domain.model.Channel
import com.lite.streamvault.ui.components.ShimmerBox
import com.lite.streamvault.ui.theme.Blue400
import com.lite.streamvault.ui.theme.Blue500
import com.lite.streamvault.ui.theme.TextMuted
import com.lite.streamvault.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    settings: AppSettings,
    onChannelClick: (Channel) -> Unit,
    onAnimeClick: (Anime) -> Unit,
    onCartoonClick: (Anime) -> Unit,
    onSeeAllChannels: () -> Unit,
    onSeeAllAnime: () -> Unit,
    onSeeAllCartoons: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(settings) { viewModel.load(settings) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        if (state.isLoading) {
            item {
                Row(modifier = Modifier.padding(16.dp)) {
                    repeat(3) {
                        ShimmerBox(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(end = 8.dp)
                        )
                    }
                }
            }
        } else {
            if (state.channels.isNotEmpty()) {
                item { SectionHeader("📺 قنوات مباشرة", onSeeAllChannels) }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.channels.take(10)) { channel ->
                            ChannelCircle(channel) { onChannelClick(channel) }
                        }
                    }
                }
                item { Spacer(Modifier.padding(top = 8.dp)) }
            }

            if (state.anime.isNotEmpty()) {
                item { SectionHeader("🌸 أنمي", onSeeAllAnime) }
                item {
                    TwoColumnPreviewRow(state.anime.take(4)) { onAnimeClick(it) }
                }
            }

            if (state.cartoons.isNotEmpty()) {
                item { SectionHeader("🧸 كرتون", onSeeAllCartoons) }
                item {
                    TwoColumnPreviewRow(state.cartoons.take(4)) { onCartoonClick(it) }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "شوفي الكل",
            color = Blue400,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.clickable(onClick = onSeeAll)
        )
    }
}

// Shows items 2-per-row, wrapped in a simple 2-column grid preview (not scrollable
// on its own — this is just a small taste before "See All").
@Composable
private fun TwoColumnPreviewRow(items: List<Anime>, onClick: (Anime) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        AnimePreviewCard(item) { onClick(item) }
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AnimePreviewCard(anime: Anime, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (!anime.posterUrl.isNullOrBlank()) {
                AsyncImage(
                    model = anime.posterUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Text(
            text = anime.title,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )
    }
}

@Composable
private fun ChannelCircle(channel: Channel, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(76.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (!channel.logoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = channel.logoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.LiveTv,
                    contentDescription = null,
                    tint = Blue500,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = channel.name,
            color = TextMuted,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
