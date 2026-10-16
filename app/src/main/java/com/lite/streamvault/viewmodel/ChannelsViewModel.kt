package com.lite.streamvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lite.streamvault.data.repository.StreamVaultRepository
import com.lite.streamvault.domain.model.Channel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChannelsUiState(
    val isLoading: Boolean = true,
    val channels: List<Channel> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "All"
)

@HiltViewModel
class ChannelsViewModel @Inject constructor(
    private val repository: StreamVaultRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChannelsUiState())
    val state: StateFlow<ChannelsUiState> = _state

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val channels = repository.getChannels()
            val categories = listOf("All") + channels.mapNotNull { it.categoryName }.distinct()
            _state.value = ChannelsUiState(
                isLoading = false,
                channels = channels,
                categories = categories
            )
        }
    }

    fun selectCategory(category: String) {
        _state.value = _state.value.copy(selectedCategory = category)
    }
}
