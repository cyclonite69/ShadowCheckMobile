package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.domain.usecase.GetAllBluetoothDevicesUseCase
import com.shadowcheck.mobile.domain.usecase.GetAllCellularTowersUseCase
import com.shadowcheck.mobile.wifi.domain.usecase.GetAllWifiNetworksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class HomeUiState(
    val wifiUnique: Int = 0,
    val wifiTotal: Int = 0,
    val btUnique: Int = 0,
    val btTotal: Int = 0,
    val cellUnique: Int = 0,
    val cellTotal: Int = 0,
    val isScanning: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllWifiNetworksUseCase: GetAllWifiNetworksUseCase,
    private val getAllBluetoothDevicesUseCase: GetAllBluetoothDevicesUseCase,
    private val getAllCellularTowersUseCase: GetAllCellularTowersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        combine(
            getAllWifiNetworksUseCase(),
            getAllBluetoothDevicesUseCase(),
            getAllCellularTowersUseCase()
        ) { wifi, bt, cell ->
            HomeUiState(
                wifiUnique = wifi.distinctBy { it.bssid }.size,
                wifiTotal = wifi.size,
                btUnique = bt.distinctBy { it.macAddress }.size,
                btTotal = bt.size,
                cellUnique = cell.distinctBy { it.cellId }.size,
                cellTotal = cell.size,
                isScanning = _uiState.value.isScanning
            )
        }.onEach { state ->
            _uiState.value = state
        }.launchIn(viewModelScope)
    }

    fun setScanning(scanning: Boolean) {
        _uiState.update { it.copy(isScanning = scanning) }
    }
}
