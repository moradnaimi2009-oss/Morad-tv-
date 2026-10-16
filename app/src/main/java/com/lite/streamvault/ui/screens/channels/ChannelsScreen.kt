package com.lite.streamvault.ui.screens.channels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.lite.streamvault.domain.model.Channel
import com.lite.streamvault.ui.components.ShimmerBox
import com.lite.streamvault.ui.components.ShimmerLine
import com.lite.streamvault.ui.theme.Blue400
import com.lite.streamvault.ui.theme.Blue500
import com.lite.streamvault.ui.theme.TextMuted
import com.lite.streamvault.viewmodel.ChannelsViewModel

@Composable
fun ChannelsScreen(
    onChannelClick: (Channel) -> Unit,
    viewModel: ChannelsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.categories) { category ->
                val selected = state.selectedCategory == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (selected) Blue500 else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { viewModel.selectCategory(category) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = category,
                        color = if (selected) Color.White else TextMuted,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        val filtered = if (state.selectedCategory == "All") state.channels
        else state.channels.filter { it.categoryName == state.selectedCategory }

        // 3 per row with a big, prominent logo tile (was previously a single-column list
        // with a small 60x40dp logo).
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (state.isLoading) {
                items(9) { ChannelTileShimmer() }
            } else {
                items(filtered) { channel ->
                    ChannelTile(channel) { onChannelClick(channel) }
                }
            }
        }
    }
}

@Composable
private fun ChannelTile(channel: Channel, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
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
                        .padding(10.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.LiveTv,
                    contentDescription = null,
                    tint = Blue400,
                    modifier = Modifier.size(36.dp)
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(Blue500.copy(alpha = 0.9f))
                    .padding(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        Text(
            text = channel.name,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )
    }
}

@Composable
private fun ChannelTileShimmer() {
    Column(modifier = Modifier.fillMaxWidth()) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        )
        ShimmerLine(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(top = 8.dp)
                .height(10.dp)
        )
    }
}
