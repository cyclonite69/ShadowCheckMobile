package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SettingsUiState(
    val scanInterval: Int = 5,
    val autoSync: Boolean = false,
    val showNotifications: Boolean = true,
    val keepScreenOn: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

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
}
