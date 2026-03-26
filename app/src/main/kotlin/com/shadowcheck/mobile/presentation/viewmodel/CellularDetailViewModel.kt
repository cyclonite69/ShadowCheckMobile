package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.core.model.CellularTower
import com.shadowcheck.mobile.domain.repository.CellularTowerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class CellularDetailUiState(
    val tower: CellularTower? = null,
    val sightings: List<CellularTower> = emptyList(),
    val avgSignal: Int = 0,
    val minSignal: Int = 0,
    val maxSignal: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class CellularDetailViewModel @Inject constructor(
    private val cellularTowerRepository: CellularTowerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cellId: Int = savedStateHandle["cellId"] ?: -1

    val uiState: StateFlow<CellularDetailUiState> = cellularTowerRepository
        .getTowersByCellId(cellId)
        .map { towers ->
            val sorted = towers.sortedBy { it.timestamp }
            CellularDetailUiState(
                tower = sorted.lastOrNull(),
                sightings = sorted,
                avgSignal = if (sorted.isNotEmpty()) sorted.map { it.signalStrength }.average().toInt() else 0,
                minSignal = sorted.minOfOrNull { it.signalStrength } ?: 0,
                maxSignal = sorted.maxOfOrNull { it.signalStrength } ?: 0,
                isLoading = false
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            CellularDetailUiState()
        )
}
