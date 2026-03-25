package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.domain.usecase.GetAllBluetoothDevicesUseCase
import com.shadowcheck.mobile.domain.usecase.GetAllCellularTowersUseCase
import com.shadowcheck.mobile.wifi.domain.usecase.GetAllWifiNetworksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class StatsUiState(
    val wifiUnique: Int = 0,
    val wifiTotal: Int = 0,
    val btUnique: Int = 0,
    val btTotal: Int = 0,
    val cellUnique: Int = 0,
    val cellTotal: Int = 0,
    val strongestSignal: Int = 0,
    val weakestSignal: Int = 0,
    val avgSignal: Int = 0
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getAllWifiNetworksUseCase: GetAllWifiNetworksUseCase,
    private val getAllBluetoothDevicesUseCase: GetAllBluetoothDevicesUseCase,
    private val getAllCellularTowersUseCase: GetAllCellularTowersUseCase
) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = combine(
        getAllWifiNetworksUseCase(),
        getAllBluetoothDevicesUseCase(),
        getAllCellularTowersUseCase()
    ) { wifi, bt, cell ->
        StatsUiState(
            wifiUnique = wifi.distinctBy { it.bssid }.size,
            wifiTotal = wifi.size,
            btUnique = bt.distinctBy { it.macAddress }.size,
            btTotal = bt.size,
            cellUnique = cell.distinctBy { it.cellId }.size,
            cellTotal = cell.size,
            strongestSignal = wifi.maxOfOrNull { it.signalLevel } ?: 0,
            weakestSignal = wifi.minOfOrNull { it.signalLevel } ?: 0,
            avgSignal = if (wifi.isNotEmpty()) wifi.map { it.signalLevel }.average().toInt() else 0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsUiState())
}
