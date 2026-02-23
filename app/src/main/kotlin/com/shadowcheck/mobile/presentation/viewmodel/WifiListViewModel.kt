package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.domain.usecase.GetAllWifiNetworksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class WifiListUiState(
    val networks: List<com.shadowcheck.mobile.domain.model.WifiNetwork> = emptyList(),
    val distinctCount: Int = 0,
    val sightingsCounts: Map<String, Int> = emptyMap()
)

@HiltViewModel
class WifiListViewModel @Inject constructor(
    private val getAllWifiNetworksUseCase: GetAllWifiNetworksUseCase
) : ViewModel() {

    val uiState: StateFlow<WifiListUiState> = getAllWifiNetworksUseCase()
        .map { networks ->
            WifiListUiState(
                networks = networks,
                distinctCount = networks.distinctBy { it.bssid }.size,
                sightingsCounts = networks.groupingBy { it.bssid }.eachCount()
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WifiListUiState())
}
