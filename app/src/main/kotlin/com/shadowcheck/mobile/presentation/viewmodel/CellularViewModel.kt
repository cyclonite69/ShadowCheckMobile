package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.core.model.CellularTower
import com.shadowcheck.mobile.domain.usecase.GetAllCellularTowersUseCase
import com.shadowcheck.mobile.domain.usecase.GetTowersByLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CellularViewModel @Inject constructor(
    private val getAllCellularTowersUseCase: GetAllCellularTowersUseCase,
    private val getTowersByLocationUseCase: GetTowersByLocationUseCase
) : ViewModel() {

    private val _towers = MutableStateFlow<List<CellularTower>>(emptyList())
    val towers: StateFlow<List<CellularTower>> = _towers.asStateFlow()

    init {
        loadAllTowers()
    }

    fun loadAllTowers() {
        getAllCellularTowersUseCase()
            .onEach { result ->
                _towers.value = result
            }
            .launchIn(viewModelScope)
    }

    fun findTowersNearby(lat: Double, lon: Double, radiusKm: Double) {
        getTowersByLocationUseCase(lat, lon, radiusKm)
            .onEach { result ->
                _towers.value = result
            }
            .launchIn(viewModelScope)
    }
}
