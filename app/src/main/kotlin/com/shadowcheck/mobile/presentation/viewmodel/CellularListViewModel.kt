package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.domain.model.CellularTower
import com.shadowcheck.mobile.domain.usecase.GetAllCellularTowersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CellularListUiState(
    val towers: List<CellularTower> = emptyList(),
    val distinctCount: Int = 0,
    val sightingsCounts: Map<Int, Int> = emptyMap()
)

@HiltViewModel
class CellularListViewModel @Inject constructor(
    private val getAllCellularTowersUseCase: GetAllCellularTowersUseCase
) : ViewModel() {

    val uiState: StateFlow<CellularListUiState> = getAllCellularTowersUseCase()
        .map { towers ->
            CellularListUiState(
                towers = towers,
                distinctCount = towers.distinctBy { it.cellId }.size,
                sightingsCounts = towers.groupingBy { it.cellId }.eachCount()
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CellularListUiState())
}
