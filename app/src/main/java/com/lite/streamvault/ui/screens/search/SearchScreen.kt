package com.lite.streamvault.ui.screens.search

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.lite.streamvault.domain.model.SearchResult
import com.lite.streamvault.ui.components.ShimmerBox
import com.lite.streamvault.ui.theme.AnimeBadge
import com.lite.streamvault.ui.theme.Blue400
import com.lite.streamvault.ui.theme.ChannelBadge
import com.lite.streamvault.ui.theme.MovieBadge
import com.lite.streamvault.ui.theme.TextMuted
import com.lite.streamvault.viewmodel.SearchViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onAnimeClick: (Int) -> Unit,
    onChannelClick: (com.lite.streamvault.domain.model.Channel) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 2.dp, bottom = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = {
                            textFieldValue = it
                            viewModel.updateQuery(it.text)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
                        cursorBrush = androidx.compose.ui.graphics.Brush.linearGradient(listOf(Blue400, Blue400)),
                        singleLine = true,
                        decorationBox = { inner ->
                            if (textFieldValue.text.isEmpty()) {
                                Text(
                                    text = "Search movies, anime, channels...",
                                    color = TextMuted,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            inner()
                        }
                    )
                    if (textFieldValue.text.isNotEmpty()) {
                        IconButton(onClick = {
                            textFieldValue = TextFieldValue("")
                            viewModel.updateQuery("")
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        if (state.query.isBlank()) {
            EmptyState(
                title = "Search across all content",
                subtitle = "Find movies, anime, and live channels"
            )
        } else if (state.results.isEmpty() && !state.isLoading) {
            EmptyState(
                title = "No results found",
                subtitle = "Try a different search term"
            )
        } else {
            Text(
                text = "${state.results.size} results for \"${state.query}\"",
                color = TextMuted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.results) { result ->
                    SearchResultCard(
                        result = result,
                        onMovieClick = onMovieClick,
                        onAnimeClick = onAnimeClick,
                        onChannelClick = onChannelClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    result: SearchResult,
    onMovieClick: (Int) -> Unit,
    onAnimeClick: (Int) -> Unit,
    onChannelClick: (com.lite.streamvault.domain.model.Channel) -> Unit
) {
    data class CardData(
        val title: String,
        val poster: String?,
        val badgeColor: Color,
        val label: String,
        val onClick: () -> Unit
    )
    val cd = when (result) {
        is SearchResult.MovieResult -> CardData(
            title = result.movie.title,
            poster = result.movie.posterUrl,
            badgeColor = MovieBadge,
            label = "MOVIE",
            onClick = { onMovieClick(result.movie.id) }
        )
        is SearchResult.AnimeResult -> CardData(
            title = result.anime.title,
            poster = result.anime.posterUrl,
            badgeColor = AnimeBadge,
            label = "ANIME",
            onClick = { onAnimeClick(result.anime.id) }
        )
        is SearchResult.ChannelResult -> CardData(
            title = result.channel.name,
            poster = result.channel.logoUrl,
            badgeColor = ChannelBadge,
            label = "CHANNEL",
            onClick = { onChannelClick(result.channel) }
        )
    }
    val (title, poster, badgeColor, label, onClick) = cd

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.67f)
                .clip(RoundedCornerShape(12.dp))
        ) {
            if (!poster.isNullOrBlank()) {
                AsyncImage(
                    model = poster,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                ShimmerBox(Modifier.fillMaxSize())
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.Search,
                contentDescription = null,
                tint = TextMuted.copy(alpha = 0.4f),
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = subtitle,
                color = TextMuted,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
