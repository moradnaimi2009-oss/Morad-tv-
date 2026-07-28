package com.lite.streamvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lite.streamvault.data.repository.StreamVaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Reuses AnimeUiState (same shape: isLoading + a list of Anime-shaped items) since
// Cartoons and Anime are modeled as the same domain type — only the data source differs.
@HiltViewModel
class CartoonsViewModel @Inject constructor(
    private val repository: StreamVaultRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnimeUiState())
    val state: StateFlow<AnimeUiState> = _state

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val cartoons = repository.getCartoons()
            _state.value = AnimeUiState(isLoading = false, anime = cartoons)
        }
    }
}
