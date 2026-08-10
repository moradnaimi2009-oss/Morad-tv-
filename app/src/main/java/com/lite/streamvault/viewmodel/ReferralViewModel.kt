package com.lite.streamvault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lite.streamvault.data.repository.StreamVaultRepository
import com.lite.streamvault.domain.model.LeaderboardEntry
import com.lite.streamvault.util.DeviceIdProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReferralUiState(
    val isLoading: Boolean = true,
    val myCode: String = "",
    val referralCount: Int = 0,
    val unlocked: Boolean = false,
    val goal: Int = 10,
    val redeemInput: String = "",
    val redeemMessage: String? = null,
    val redeemSuccess: Boolean? = null,
    val isRedeeming: Boolean = false,
    val leaderboard: List<LeaderboardEntry> = emptyList()
)

@HiltViewModel
class ReferralViewModel @Inject constructor(
    private val repository: StreamVaultRepository,
    private val deviceIdProvider: DeviceIdProvider
) : ViewModel() {

    private val _state = MutableStateFlow(ReferralUiState(myCode = deviceIdProvider.referralCode))
    val state: StateFlow<ReferralUiState> = _state

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.ensureReferralCode(deviceIdProvider.deviceId, deviceIdProvider.referralCode)
            val status = repository.getReferralStatus(deviceIdProvider.deviceId)
            val top = repository.getTopReferrers()
            _state.value = _state.value.copy(
                isLoading = false,
                referralCount = status.referralCount,
                unlocked = status.unlockedRestricted,
                goal = status.goal,
                leaderboard = top
            )
        }
    }

    fun onRedeemInputChange(value: String) {
        _state.value = _state.value.copy(redeemInput = value, redeemMessage = null, redeemSuccess = null)
    }

    fun redeem() {
        val code = _state.value.redeemInput.trim().uppercase()
        if (code.isBlank() || code == deviceIdProvider.referralCode) {
            _state.value = _state.value.copy(
                redeemMessage = if (code.isBlank()) null else "ما تقدر تستخدم كودك أنت",
                redeemSuccess = false
            )
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isRedeeming = true)
            val (success, message) = repository.redeemReferral(code, deviceIdProvider.deviceId)
            val friendly = when {
                success -> "تم بنجاح! 🎉"
                message == "self_referral" -> "ما تقدر تستخدم كودك أنت"
                message == "already_used" -> "تم استخدام كود دعوة من قبل، لا يمكن التكرار"
                message == "invalid_code" -> "الكود غير صحيح"
                else -> "صار خطأ، حاول مرة ثانية"
            }
            _state.value = _state.value.copy(
                isRedeeming = false,
                redeemMessage = friendly,
                redeemSuccess = success,
                redeemInput = if (success) "" else _state.value.redeemInput
            )
        }
    }
}
