package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.core.model.BluetoothDevice
import com.shadowcheck.mobile.core.model.CellularTower
import com.shadowcheck.mobile.domain.usecase.GetAllBluetoothDevicesUseCase
import com.shadowcheck.mobile.domain.usecase.GetAllCellularTowersUseCase
import com.shadowcheck.mobile.wifi.domain.usecase.GetAllWifiNetworksUseCase
import com.shadowcheck.mobile.wifi.model.WifiNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class MapUiState(
    val wifiNetworks: List<WifiNetwork> = emptyList(),
    val bluetoothDevices: List<BluetoothDevice> = emptyList(),
    val cellularTowers: List<CellularTower> = emptyList(),
    val showWifi: Boolean = true,
    val showBluetooth: Boolean = true,
    val showCellular: Boolean = true
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAllWifiNetworksUseCase: GetAllWifiNetworksUseCase,
    private val getAllBluetoothDevicesUseCase: GetAllBluetoothDevicesUseCase,
    private val getAllCellularTowersUseCase: GetAllCellularTowersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        combine(
            getAllWifiNetworksUseCase(),
            getAllBluetoothDevicesUseCase(),
            getAllCellularTowersUseCase()
        ) { wifi, bt, cell ->
            MapUiState(
                wifiNetworks = wifi,
                bluetoothDevices = bt,
                cellularTowers = cell,
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
}
