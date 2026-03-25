package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import com.shadowcheck.mobile.wifi.domain.repository.WifiNetworkRepository
import com.shadowcheck.mobile.wifi.model.WifiNetwork
import javax.inject.Inject

data class NetworkDetailUiState(
    val network: WifiNetwork? = null,
    val sightings: List<WifiNetwork> = emptyList(),
    val avgSignal: Int = 0,
    val minSignal: Int = 0,
    val maxSignal: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class NetworkDetailViewModel @Inject constructor(
    private val wifiNetworkRepository: WifiNetworkRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bssid: String = savedStateHandle.get<String>("bssid") ?: ""

    val uiState: StateFlow<NetworkDetailUiState> = wifiNetworkRepository
        .getNetworksByBssid(bssid)
        .map { networks ->
            val sorted = networks.sortedBy { it.timestamp }
            NetworkDetailUiState(
                network = sorted.firstOrNull(),
                sightings = sorted,
                avgSignal = if (sorted.isNotEmpty()) sorted.map { it.signalLevel }.average().toInt() else 0,
                minSignal = sorted.minOfOrNull { it.signalLevel } ?: 0,
                maxSignal = sorted.maxOfOrNull { it.signalLevel } ?: 0,
                isLoading = false
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NetworkDetailUiState())
}
