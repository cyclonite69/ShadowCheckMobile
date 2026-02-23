package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.core.model.CellularTower
import com.shadowcheck.mobile.domain.repository.CellularTowerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCellularTowersUseCase @Inject constructor(
    private val repository: CellularTowerRepository
) {
    operator fun invoke(): Flow<List<CellularTower>> {
        return repository.getAllTowers()
    }
}
