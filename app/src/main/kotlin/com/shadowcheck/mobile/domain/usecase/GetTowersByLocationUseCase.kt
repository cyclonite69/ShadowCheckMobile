package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.domain.model.CellularTower
import com.shadowcheck.mobile.domain.repository.CellularTowerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class GetTowersByLocationUseCase @Inject constructor(
    private val cellularTowerRepository: CellularTowerRepository
) {
    operator fun invoke(lat: Double, lng: Double, radiusKm: Double): Flow<List<CellularTower>> {
        return cellularTowerRepository.getTowersByLocation(lat, lng, radiusKm)
    }
}
