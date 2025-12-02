package com.shadowcheck.mobile.domain.usecase.cellular

import com.shadowcheck.mobile.domain.model.CellularTower
import com.shadowcheck.mobile.domain.repository.CellularTowerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCellularTowersUseCase @Inject constructor(
    private val repository: CellularTowerRepository
) {
    operator fun invoke(): Flow<List<CellularTower>> = repository.getAllTowers()
}

class GetTowersByLocationUseCase @Inject constructor(
    private val repository: CellularTowerRepository
) {
    operator fun invoke(lat: Double, lng: Double, radiusKm: Double): Flow<List<CellularTower>> {
        return repository.getTowersByLocation(lat, lng, radiusKm)
    }
}

class InsertCellularTowerUseCase @Inject constructor(
    private val repository: CellularTowerRepository
) {
    suspend operator fun invoke(tower: CellularTower) = repository.insertTower(tower)
}
