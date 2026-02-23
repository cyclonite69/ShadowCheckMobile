package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.domain.model.BluetoothDevice
import com.shadowcheck.mobile.domain.usecase.GetAllBluetoothDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class BluetoothListUiState(
    val devices: List<BluetoothDevice> = emptyList(),
    val distinctCount: Int = 0,
    val sightingsCounts: Map<String, Int> = emptyMap()
)

@HiltViewModel
class BluetoothListViewModel @Inject constructor(
    private val getAllBluetoothDevicesUseCase: GetAllBluetoothDevicesUseCase
) : ViewModel() {

    val uiState: StateFlow<BluetoothListUiState> = getAllBluetoothDevicesUseCase()
        .map { devices ->
            BluetoothListUiState(
                devices = devices,
                distinctCount = devices.distinctBy { it.macAddress }.size,
                sightingsCounts = devices.groupingBy { it.macAddress }.eachCount()
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BluetoothListUiState())
}
