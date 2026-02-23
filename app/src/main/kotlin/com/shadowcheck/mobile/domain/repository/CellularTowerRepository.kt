package com.shadowcheck.mobile.domain.repository

import com.shadowcheck.mobile.domain.model.CellularTower
import kotlinx.coroutines.flow.Flow

interface CellularTowerRepository {
    fun getAllTowers(): Flow<List<CellularTower>>

    /**
     * Retrieves a [Flow] of cellular towers within a specified geographical radius from a given location.
     *
     * @param lat The latitude of the center point.
     * @param lng The longitude of the center point.
     * @param radiusKm The radius in kilometers to search for towers.
     * @return A [Flow] emitting a list of [CellularTower] objects within the specified area.
     */
    fun getTowersByLocation(lat: Double, lng: Double, radiusKm: Double): Flow<List<CellularTower>>

    suspend fun insertTower(tower: CellularTower): Long
    suspend fun updateTower(tower: CellularTower)
}
