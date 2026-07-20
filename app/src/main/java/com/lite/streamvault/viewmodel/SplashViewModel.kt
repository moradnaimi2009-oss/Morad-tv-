package com.lite.streamvault.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lite.streamvault.ads.AdManager
import com.lite.streamvault.data.repository.StreamVaultRepository
import com.lite.streamvault.domain.model.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashState {
    data object Loading : SplashState()
    data class MaintenanceMode(val message: String) : SplashState()
    data class UpdateAvailable(
        val message: String,
        val url: String,
        val forceUpdate: Boolean,
        val settings: AppSettings
    ) : SplashState()
    data class Ready(val settings: AppSettings) : SplashState()
    data class Error(val message: String) : SplashState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: StreamVaultRepository,
    private val adManager: AdManager
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state

    fun start(activity: Activity?) {
        viewModelScope.launch {
            val start = System.currentTimeMillis()
            try {
                val settings = repository.getSettings()

                if (settings.maintenanceMode) {
                    _state.value = SplashState.MaintenanceMode(settings.maintenanceMessage)
                    return@launch
                }

                // Initialize ads in parallel.
                if (activity != null && settings.showAds) {
                    launch {
                        runCatching {
                            val ads = repository.getAds()
                            adManager.configure(ads, settings.showAds, activity)
                        }
                    }
                }

                // Update check.
                if (settings.updateEnabled &&
                    settings.updateVersion.isNotBlank() &&
                    isUpdateAvailable(settings.appVersion, settings.updateVersion)
                ) {
                    _state.value = SplashState.UpdateAvailable(
                        message = settings.updateMessage,
                        url = settings.updateUrl,
                        forceUpdate = settings.forceUpdate,
                        settings = settings
                    )
                    return@launch
                }

                // Ensure minimum splash duration of 2s.
                val elapsed = System.currentTimeMillis() - start
                if (elapsed < 2000) delay(2000 - elapsed)
                _state.value = SplashState.Ready(settings)
            } catch (e: Exception) {
                _state.value = SplashState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun isUpdateAvailable(current: String, latest: String): Boolean {
        val c = current.split(".").map { it.toIntOrNull() ?: 0 }
        val l = latest.split(".").map { it.toIntOrNull() ?: 0 }
        for (i in 0 until maxOf(c.size, l.size)) {
            val cv = c.getOrElse(i) { 0 }
            val lv = l.getOrElse(i) { 0 }
            if (lv > cv) return true
            if (lv < cv) return false
        }
        return false
    }
}
