package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.core.model.BluetoothDevice
import com.shadowcheck.mobile.core.model.CellularTower
import com.shadowcheck.mobile.core.model.LocationSample
import com.shadowcheck.mobile.core.model.ScanSession
import com.shadowcheck.mobile.domain.usecase.GetAllBluetoothDevicesUseCase
import com.shadowcheck.mobile.domain.usecase.GetAllCellularTowersUseCase
import com.shadowcheck.mobile.domain.usecase.GetAllScanSessionsUseCase
import com.shadowcheck.mobile.domain.usecase.GetRecentLocationSamplesUseCase
import com.shadowcheck.mobile.wifi.domain.usecase.GetAllWifiNetworksUseCase
import com.shadowcheck.mobile.wifi.model.WifiNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class MapUiState(
    val wifiNetworks: List<WifiNetwork> = emptyList(),
    val bluetoothDevices: List<BluetoothDevice> = emptyList(),
    val cellularTowers: List<CellularTower> = emptyList(),
    val locationSamples: List<LocationSample> = emptyList(),
    val sessions: List<ScanSession> = emptyList(),
    val selectedSessionId: String? = null,
    val playbackEnabled: Boolean = false,
    val playbackTimestamp: Long? = null,
    val playbackStart: Long? = null,
    val playbackEnd: Long? = null,
    val showWifi: Boolean = true,
    val showBluetooth: Boolean = true,
    val showCellular: Boolean = true
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAllWifiNetworksUseCase: GetAllWifiNetworksUseCase,
    private val getAllBluetoothDevicesUseCase: GetAllBluetoothDevicesUseCase,
    private val getAllCellularTowersUseCase: GetAllCellularTowersUseCase,
    private val getRecentLocationSamplesUseCase: GetRecentLocationSamplesUseCase,
    private val getAllScanSessionsUseCase: GetAllScanSessionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        combine(
            getAllWifiNetworksUseCase(),
            getAllBluetoothDevicesUseCase(),
            getAllCellularTowersUseCase(),
            getRecentLocationSamplesUseCase(),
            getAllScanSessionsUseCase()
        ) { wifi, bt, cell, locations, sessions ->
            val playbackEnabled = _uiState.value.playbackEnabled
            val selectedSessionId = _uiState.value.selectedSessionId ?: sessions.firstOrNull()?.sessionId
            val sessionLocations = selectedSessionId?.let { sessionId ->
                locations.filter { it.sessionId == sessionId }
            } ?: locations
            val start = sessionLocations.minOfOrNull { it.timestamp }
            val end = sessionLocations.maxOfOrNull { it.timestamp }
            val cutoff = if (playbackEnabled) _uiState.value.playbackTimestamp ?: end else null

            val filteredLocations = cutoff?.let { ts -> sessionLocations.filter { it.timestamp <= ts } } ?: sessionLocations
            val sessionWifi = selectedSessionId?.let { sessionId ->
                wifi.filter { it.sessionId == sessionId }
            } ?: wifi
            val sessionBt = selectedSessionId?.let { sessionId ->
                bt.filter { it.sessionId == sessionId }
            } ?: bt
            val sessionCell = selectedSessionId?.let { sessionId ->
                cell.filter { it.sessionId == sessionId }
            } ?: cell

            val filteredWifi = cutoff?.let { ts -> sessionWifi.filter { it.timestamp <= ts } } ?: sessionWifi
            val filteredBt = cutoff?.let { ts -> sessionBt.filter { it.timestamp <= ts } } ?: sessionBt
            val filteredCell = cutoff?.let { ts -> sessionCell.filter { it.timestamp <= ts } } ?: sessionCell

            MapUiState(
                wifiNetworks = filteredWifi,
                bluetoothDevices = filteredBt,
                cellularTowers = filteredCell,
                locationSamples = filteredLocations,
                sessions = sessions,
                selectedSessionId = selectedSessionId,
                playbackEnabled = playbackEnabled,
                playbackTimestamp = cutoff,
                playbackStart = start,
                playbackEnd = end,
                showWifi = _uiState.value.showWifi,
                showBluetooth = _uiState.value.showBluetooth,
                showCellular = _uiState.value.showCellular
            )
        }.onEach { state ->
            _uiState.value = state
        }.launchIn(viewModelScope)
    }

    fun toggleWifi() = _uiState.update { it.copy(showWifi = !it.showWifi) }
    fun toggleBluetooth() = _uiState.update { it.copy(showBluetooth = !it.showBluetooth) }
    fun toggleCellular() = _uiState.update { it.copy(showCellular = !it.showCellular) }

    fun setPlaybackEnabled(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(
                playbackEnabled = enabled,
                playbackTimestamp = if (enabled) state.playbackEnd else null
            )
        }
    }

    fun setPlaybackTimestamp(timestamp: Long) {
        _uiState.update { it.copy(playbackTimestamp = timestamp, playbackEnabled = true) }
    }

    fun jumpToLatest() {
        _uiState.update { it.copy(playbackEnabled = false, playbackTimestamp = null) }
    }

    fun selectSession(sessionId: String?) {
        _uiState.update {
            it.copy(
                selectedSessionId = sessionId,
                playbackTimestamp = null,
                playbackEnabled = false
            )
        }
    }
}
