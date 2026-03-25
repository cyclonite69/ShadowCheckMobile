package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.domain.repository.SensorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val scanInterval: Int = 5,
    val autoSync: Boolean = false,
    val showNotifications: Boolean = true,
    val keepScreenOn: Boolean = false
)

sealed class PurgeState {
    data object Idle : PurgeState()
    data object InProgress : PurgeState()
    data object Success : PurgeState()
    data class Error(val msg: String) : PurgeState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sensorRepository: SensorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _purgeState = MutableStateFlow<PurgeState>(PurgeState.Idle)
    val purgeState: StateFlow<PurgeState> = _purgeState.asStateFlow()

    fun updateScanInterval(interval: Int) {
        _uiState.update { it.copy(scanInterval = interval) }
    }

    fun toggleAutoSync() {
        _uiState.update { it.copy(autoSync = !it.autoSync) }
    }

    fun toggleNotifications() {
        _uiState.update { it.copy(showNotifications = !it.showNotifications) }
    }

    fun toggleKeepScreenOn() {
        _uiState.update { it.copy(keepScreenOn = !it.keepScreenOn) }
    }

    fun purgeSensorData() {
        viewModelScope.launch {
            _purgeState.value = PurgeState.InProgress
            runCatching {
                sensorRepository.purgeAllSensorReadings()
            }.onSuccess {
                _purgeState.value = PurgeState.Success
            }.onFailure { throwable ->
                _purgeState.value = PurgeState.Error(
                    throwable.message ?: "Failed to purge sensor data"
                )
            }
        }
    }
}
