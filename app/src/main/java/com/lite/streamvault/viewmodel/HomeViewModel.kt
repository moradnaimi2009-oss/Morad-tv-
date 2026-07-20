package com.lite.streamvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lite.streamvault.data.repository.StreamVaultRepository
import com.lite.streamvault.domain.model.Anime
import com.lite.streamvault.domain.model.AppSettings
import com.lite.streamvault.domain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val movies: List<Movie> = emptyList(),
    val anime: List<Anime> = emptyList(),
    val settings: AppSettings = AppSettings()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StreamVaultRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    fun load(settings: AppSettings) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, settings = settings)
            val movies = if (settings.enableMovies) repository.getMovies() else emptyList()
            val anime = if (settings.enableAnime) repository.getAnime() else emptyList()
            _state.value = HomeUiState(
                isLoading = false,
                movies = movies,
                anime = anime,
                settings = settings
            )
        }
    }
}
