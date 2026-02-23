package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.domain.model.HeatmapData
import com.shadowcheck.mobile.domain.usecase.GetAllBluetoothDevicesUseCase
import com.shadowcheck.mobile.domain.usecase.GetAllCellularTowersUseCase
import com.shadowcheck.mobile.domain.usecase.GetAllWifiNetworksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class HeatmapUiState(
    val wifiHeatmap: List<HeatmapData.HeatmapPoint> = emptyList(),
    val btHeatmap: List<HeatmapData.HeatmapPoint> = emptyList(),
    val cellHeatmap: List<HeatmapData.HeatmapPoint> = emptyList(),
    val selectedLayer: String = "wifi"
)

@HiltViewModel
class HeatmapViewModel @Inject constructor(
    private val getAllWifiNetworksUseCase: GetAllWifiNetworksUseCase,
    private val getAllBluetoothDevicesUseCase: GetAllBluetoothDevicesUseCase,
    private val getAllCellularTowersUseCase: GetAllCellularTowersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HeatmapUiState())
    val uiState: StateFlow<HeatmapUiState> = _uiState.asStateFlow()

    init {
        combine(
            getAllWifiNetworksUseCase(),
            getAllBluetoothDevicesUseCase(),
            getAllCellularTowersUseCase()
        ) { wifi, bt, cell ->
            HeatmapUiState(
                wifiHeatmap = HeatmapData.generateWiFiHeatmap(wifi),
                btHeatmap = HeatmapData.generateBluetoothHeatmap(bt),
                cellHeatmap = HeatmapData.generateCellularHeatmap(cell),
                selectedLayer = _uiState.value.selectedLayer
            )
        }.onEach { state ->
            _uiState.value = state
        }.launchIn(viewModelScope)
    }

    fun selectLayer(layer: String) = _uiState.update { it.copy(selectedLayer = layer) }
}
