package com.lite.streamvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lite.streamvault.data.repository.StreamVaultRepository
import com.lite.streamvault.domain.model.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<SearchResult> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: StreamVaultRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state

    init { loadAll() }

    private fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // Preload all datasets for local filtering.
            repository.getMovies()
            repository.getAnime()
            repository.getChannels()
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun updateQuery(q: String) {
        _state.value = _state.value.copy(query = q)
        viewModelScope.launch {
            if (q.isBlank()) {
                _state.value = _state.value.copy(results = emptyList())
                return@launch
            }
            val movies = repository.getMovies().filter {
                it.title.contains(q, ignoreCase = true)
            }.map { SearchResult.MovieResult(it) }
            val anime = repository.getAnime().filter {
                it.title.contains(q, ignoreCase = true)
            }.map { SearchResult.AnimeResult(it) }
            val channels = repository.getChannels().filter {
                it.name.contains(q, ignoreCase = true)
            }.map { SearchResult.ChannelResult(it) }
            _state.value = _state.value.copy(results = movies + anime + channels)
        }
    }
}
