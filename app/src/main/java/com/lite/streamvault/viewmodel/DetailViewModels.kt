package com.lite.streamvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lite.streamvault.data.repository.StreamVaultRepository
import com.lite.streamvault.domain.model.Anime
import com.lite.streamvault.domain.model.AnimeEpisode
import com.lite.streamvault.domain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnimeDetailState(
    val anime: Anime? = null,
    val episodes: List<AnimeEpisode> = emptyList(),
    val isLoading: Boolean = true
)

// Actually fetches the anime (by id) and its real episode list from the
// repository, instead of the hardcoded empty placeholders previously used.
@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    private val repository: StreamVaultRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnimeDetailState())
    val state: StateFlow<AnimeDetailState> = _state.asStateFlow()

    fun load(animeId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val anime = repository.getAnime().find { it.id == animeId }
            val episodes = repository.getEpisodes(animeId)
            _state.value = AnimeDetailState(anime = anime, episodes = episodes, isLoading = false)
        }
    }
}

data class MovieDetailState(
    val movie: Movie? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: StreamVaultRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MovieDetailState())
    val state: StateFlow<MovieDetailState> = _state.asStateFlow()

    fun load(movieId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val movie = repository.getMovies().find { it.id == movieId }
            _state.value = MovieDetailState(movie = movie, isLoading = false)
        }
    }
}
