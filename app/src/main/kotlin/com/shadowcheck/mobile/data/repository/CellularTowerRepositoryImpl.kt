package com.shadowcheck.mobile.data.repository

import com.shadowcheck.mobile.core.di.IoDispatcher
import com.shadowcheck.mobile.core.model.CellularTower
import com.shadowcheck.mobile.data.database.dao.CellularTowerDao
import com.shadowcheck.mobile.data.database.model.toDomainModel
import com.shadowcheck.mobile.data.database.model.toEntity
import com.shadowcheck.mobile.domain.repository.CellularTowerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Singleton
class CellularTowerRepositoryImpl @Inject constructor(
    private val cellularTowerDao: CellularTowerDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : CellularTowerRepository {

    override fun getAllTowers(): Flow<List<CellularTower>> {
        return cellularTowerDao.getAllTowers().map { entities ->
            entities.map { it.toDomainModel() }
        }.flowOn(dispatcher)
    }

    override fun getTowersByCellId(cellId: Int): Flow<List<CellularTower>> {
        return cellularTowerDao.getTowersByCellId(cellId)
            .map { entities -> entities.map { it.toDomainModel() } }
            .flowOn(dispatcher)
    }

    override fun getTowerByCellId(cellId: Int): Flow<CellularTower?> {
        return cellularTowerDao.getTowerByCellId(cellId)
            .map { it?.toDomainModel() }
            .flowOn(dispatcher)
    }

    override fun getTowersByLocation(lat: Double, lng: Double, radiusKm: Double): Flow<List<CellularTower>> {
        return cellularTowerDao.getAllTowers().map { towers ->
            towers.filter { tower ->
                calculateHaversineDistance(lat, lng, tower.latitude, tower.longitude) <= radiusKm
            }.map { it.toDomainModel() }
        }.flowOn(dispatcher)
    }

    override suspend fun insertTower(tower: CellularTower): Long = withContext(dispatcher) {
        cellularTowerDao.insertTower(tower.toEntity())
    }

    override suspend fun insertTowers(towers: List<CellularTower>) = withContext(dispatcher) {
        cellularTowerDao.insertTowers(towers.map { it.toEntity() })
    }

    override suspend fun updateTower(tower: CellularTower) = withContext(dispatcher) {
        cellularTowerDao.updateTower(tower.toEntity())
    }

    /**
     * Calculates the distance between two geographical points in kilometers using the Haversine formula.
     *
     * @param lat1 Latitude of the first point.
     * @param lon1 Longitude of the first point.
     * @param lat2 Latitude of the second point.
     * @param lon2 Longitude of the second point.
     * @return The distance in kilometers.
     */
    private fun calculateHaversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusKm * c
    }
}
