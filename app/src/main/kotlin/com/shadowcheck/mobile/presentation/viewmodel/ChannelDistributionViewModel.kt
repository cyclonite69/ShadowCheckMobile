package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.domain.model.WifiNetwork
import com.shadowcheck.mobile.domain.usecase.GetAllWifiNetworksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class ChannelDistributionUiState(
    val networks: List<WifiNetwork> = emptyList(),
    val channelCounts: Map<Int, Int> = emptyMap()
)

@HiltViewModel
class ChannelDistributionViewModel @Inject constructor(
    private val getAllWifiNetworksUseCase: GetAllWifiNetworksUseCase
) : ViewModel() {

    val uiState: StateFlow<ChannelDistributionUiState> = getAllWifiNetworksUseCase()
        .map { networks ->
            val channelCounts = networks
                .mapNotNull { getChannelFromFreq(it.frequency) }
                .groupingBy { it }
                .eachCount()
            ChannelDistributionUiState(
                networks = networks,
                channelCounts = channelCounts
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChannelDistributionUiState())

    private fun getChannelFromFreq(freq: Int): Int? = when (freq) {
        in 2412..2484 -> (freq - 2412) / 5 + 1
        in 5170..5825 -> (freq - 5170) / 5 + 34
        else -> null
    }
}
