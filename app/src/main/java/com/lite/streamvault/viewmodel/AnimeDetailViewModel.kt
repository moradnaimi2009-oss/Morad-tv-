package com.lite.streamvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lite.streamvault.data.repository.StreamVaultRepository
import com.lite.streamvault.domain.model.Anime
import com.lite.streamvault.domain.model.AnimeEpisode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnimeDetailUiState(
    val isLoading: Boolean = true,
    val anime: Anime? = null,
    val episodes: List<AnimeEpisode> = emptyList()
)

@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    private val repository: StreamVaultRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnimeDetailUiState())
    val state: StateFlow<AnimeDetailUiState> = _state

    private var loadedForId: Int? = null

    fun load(animeId: Int) {
        if (loadedForId == animeId) return
        loadedForId = animeId
        viewModelScope.launch {
            _state.value = AnimeDetailUiState(isLoading = true)
            // Fetch the anime's own info and its episode list in parallel-ish (sequential is fine here,
            // both are lightweight REST calls) so the poster AND the episodes show up together.
            val anime = repository.getAnime().find { it.id == animeId }
            val episodes = repository.getEpisodes(animeId)
                .sortedBy { it.episodeNumber }
            _state.value = AnimeDetailUiState(
                isLoading = false,
                anime = anime?.copy(episodeCount = episodes.size),
                episodes = episodes
            )
        }
    }
}
