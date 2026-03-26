package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.core.model.BluetoothDevice
import com.shadowcheck.mobile.domain.repository.BluetoothDeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class BluetoothDetailUiState(
    val device: BluetoothDevice? = null,
    val sightings: List<BluetoothDevice> = emptyList(),
    val avgSignal: Int = 0,
    val minSignal: Int = 0,
    val maxSignal: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class BluetoothDetailViewModel @Inject constructor(
    private val bluetoothDeviceRepository: BluetoothDeviceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val macAddress: String = savedStateHandle["macAddress"] ?: ""

    val uiState: StateFlow<BluetoothDetailUiState> = bluetoothDeviceRepository
        .getDevicesByMacAddress(macAddress)
        .map { devices ->
            val sorted = devices.sortedBy { it.timestamp }
            BluetoothDetailUiState(
                device = sorted.lastOrNull(),
                sightings = sorted,
                avgSignal = if (sorted.isNotEmpty()) sorted.map { it.rssi }.average().toInt() else 0,
                minSignal = sorted.minOfOrNull { it.rssi } ?: 0,
                maxSignal = sorted.maxOfOrNull { it.rssi } ?: 0,
                isLoading = false
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            BluetoothDetailUiState()
        )
}
