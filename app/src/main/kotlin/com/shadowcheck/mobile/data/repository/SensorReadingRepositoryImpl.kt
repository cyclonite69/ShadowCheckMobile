package com.shadowcheck.mobile.data.repository

import com.shadowcheck.mobile.core.di.IoDispatcher
import com.shadowcheck.mobile.core.model.SensorReading
import com.shadowcheck.mobile.data.database.dao.SensorReadingDao
import com.shadowcheck.mobile.data.database.model.toDomainModel
import com.shadowcheck.mobile.data.database.model.toEntity
import com.shadowcheck.mobile.domain.repository.SensorReadingRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class SensorReadingRepositoryImpl @Inject constructor(
    private val sensorReadingDao: SensorReadingDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : SensorReadingRepository {

    override fun getRecentReadings(): Flow<List<SensorReading>> {
        return sensorReadingDao.getRecentReadings()
            .map { readings -> readings.map { it.toDomainModel() } }
            .flowOn(dispatcher)
    }

    override suspend fun insertReading(reading: SensorReading) = withContext(dispatcher) {
        sensorReadingDao.insertReading(reading.toEntity())
    }

    override suspend fun insertReadings(readings: List<SensorReading>) = withContext(dispatcher) {
        sensorReadingDao.insertReadings(readings.map { it.toEntity() })
    }

    override suspend fun deleteOlderThan(cutoffTime: Long) = withContext(dispatcher) {
        sensorReadingDao.deleteOlderThan(cutoffTime)
    }

    override suspend fun deleteAll() = withContext(dispatcher) {
        sensorReadingDao.deleteAll()
    }
}
