package com.shadowcheck.mobile.domain.repository

import com.shadowcheck.mobile.core.model.CellularTower
import kotlinx.coroutines.flow.Flow

interface CellularTowerRepository {
    fun getAllTowers(): Flow<List<CellularTower>>
    fun getTowerByCellId(cellId: Int): Flow<CellularTower?>
    suspend fun insertTower(tower: CellularTower): Long
    suspend fun updateTower(tower: CellularTower)
    fun getTowersByLocation(lat: Double, lon: Double, radiusKm: Double): Flow<List<CellularTower>>
}
