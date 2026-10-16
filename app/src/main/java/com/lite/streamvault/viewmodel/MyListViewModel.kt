package com.lite.streamvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lite.streamvault.data.repository.StreamVaultRepository
import com.lite.streamvault.domain.model.Anime
import com.lite.streamvault.domain.model.Movie
import com.lite.streamvault.util.LocalLibraryStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyListUiState(
    val isLoading: Boolean = true,
    val movies: List<Movie> = emptyList(),
    val anime: List<Anime> = emptyList(),
    val cartoons: List<Anime> = emptyList()
)

@HiltViewModel
class MyListViewModel @Inject constructor(
    private val repository: StreamVaultRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MyListUiState())
    val state: StateFlow<MyListUiState> = _state

    fun load(library: LocalLibraryStore) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val favorites = library.getFavoriteSet()
            val movieIds = favorites.filter { it.startsWith("movie:") }.map { it.removePrefix("movie:") }
            val animeIds = favorites.filter { it.startsWith("anime:") }.map { it.removePrefix("anime:") }
            val cartoonIds = favorites.filter { it.startsWith("cartoon:") }.map { it.removePrefix("cartoon:") }

            val movies = if (movieIds.isEmpty()) emptyList() else
                repository.getMovies().filter { it.id.toString() in movieIds }
            val anime = if (animeIds.isEmpty()) emptyList() else
                repository.getAnime().filter { it.id.toString() in animeIds }
            val cartoons = if (cartoonIds.isEmpty()) emptyList() else
                repository.getCartoons().filter { it.id.toString() in cartoonIds }

            _state.value = MyListUiState(isLoading = false, movies = movies, anime = anime, cartoons = cartoons)
        }
    }
}
