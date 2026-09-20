package com.lite.streamvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lite.streamvault.data.repository.StreamVaultRepository
import com.lite.streamvault.util.DeviceIdProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Reuses AnimeDetailUiState — same shape, different source (cartoons + their episodes
// filtered by cartoon_id instead of anime_id).
@HiltViewModel
class CartoonDetailViewModel @Inject constructor(
    private val repository: StreamVaultRepository,
    private val deviceIdProvider: DeviceIdProvider
) : ViewModel() {

    private val _state = MutableStateFlow(AnimeDetailUiState())
    val state: StateFlow<AnimeDetailUiState> = _state

    private var loadedForId: Int? = null

    fun load(cartoonId: Int) {
        if (loadedForId == cartoonId) return
        loadedForId = cartoonId
        viewModelScope.launch {
            _state.value = AnimeDetailUiState(isLoading = true)
            val cartoon = repository.getCartoons().find { it.id == cartoonId }
            val episodes = repository.getCartoonEpisodes(cartoonId)
                .sortedBy { it.episodeNumber }
            val referral = repository.getReferralStatus(deviceIdProvider.deviceId)
            _state.value = AnimeDetailUiState(
                isLoading = false,
                anime = cartoon?.copy(episodeCount = episodes.size),
                episodes = episodes,
                unlockedRestricted = referral.unlockedRestricted
            )
        }
    }
}
