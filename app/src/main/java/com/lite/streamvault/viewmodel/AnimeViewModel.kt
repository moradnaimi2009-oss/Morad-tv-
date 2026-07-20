package com.lite.streamvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lite.streamvault.data.repository.StreamVaultRepository
import com.lite.streamvault.domain.model.Anime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnimeUiState(
    val isLoading: Boolean = true,
    val anime: List<Anime> = emptyList()
)

@HiltViewModel
class AnimeViewModel @Inject constructor(
    private val repository: StreamVaultRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnimeUiState())
    val state: StateFlow<AnimeUiState> = _state

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val anime = repository.getAnime()
            _state.value = AnimeUiState(isLoading = false, anime = anime)
        }
    }
}
