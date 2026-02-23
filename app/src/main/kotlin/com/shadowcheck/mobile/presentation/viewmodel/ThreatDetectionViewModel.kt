package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.domain.model.SurveillanceDetector
import com.shadowcheck.mobile.domain.usecase.GetAllBluetoothDevicesUseCase
import com.shadowcheck.mobile.domain.usecase.GetAllWifiNetworksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class Threat(
    val id: String,
    val type: String,
    val severity: String,
    val title: String,
    val description: String,
    val timestamp: Long,
    val bssid: String = "",
    val signalStrength: Int = 0
)

data class ThreatDetectionUiState(
    val threats: List<Threat> = emptyList(),
    val isScanning: Boolean = false
)

@HiltViewModel
class ThreatDetectionViewModel @Inject constructor(
    private val getAllWifiNetworksUseCase: GetAllWifiNetworksUseCase,
    private val getAllBluetoothDevicesUseCase: GetAllBluetoothDevicesUseCase,
    private val surveillanceDetector: SurveillanceDetector
) : ViewModel() {

    private val _uiState = MutableStateFlow(ThreatDetectionUiState())
    val uiState: StateFlow<ThreatDetectionUiState> = _uiState.asStateFlow()

    init {
        startScanning()
    }

    fun toggleScanning() {
        _uiState.update { it.copy(isScanning = !it.isScanning) }
        if (_uiState.value.isScanning) {
            startScanning()
        }
    }

    private fun startScanning() {
        combine(
            getAllWifiNetworksUseCase(),
            getAllBluetoothDevicesUseCase()
        ) { wifi, bt ->
            surveillanceDetector.detectThreats(wifi, bt)
        }.map { detections ->
            detections.mapIndexed { index, detection ->
                Threat(
                    id = index.toString(),
                    type = detection.type.name,
                    severity = detection.severity.name,
                    title = detection.type.name.replace("_", " "),
                    description = "Detected ${detection.type.name}",
                    timestamp = System.currentTimeMillis(),
                    bssid = "",
                    signalStrength = 0
                )
            }
        }.onEach { threats ->
            _uiState.update { it.copy(threats = threats) }
            delay(5000)
        }.launchIn(viewModelScope)
    }
}
