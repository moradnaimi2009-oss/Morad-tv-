package com.lite.streamvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lite.streamvault.data.repository.StreamVaultRepository
import com.lite.streamvault.domain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieDetailUiState(
    val isLoading: Boolean = true,
    val movie: Movie? = null
)

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: StreamVaultRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MovieDetailUiState())
    val state: StateFlow<MovieDetailUiState> = _state

    private var loadedForId: Int? = null

    fun load(movieId: Int) {
        if (loadedForId == movieId) return
        loadedForId = movieId
        viewModelScope.launch {
            _state.value = MovieDetailUiState(isLoading = true)
            val movie = repository.getMovies().find { it.id == movieId }
            _state.value = MovieDetailUiState(isLoading = false, movie = movie)
        }
    }
}
