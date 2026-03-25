package com.shadowcheck.mobile.data.repository

import com.shadowcheck.mobile.data.ShadowCheckDatabase
import com.shadowcheck.mobile.domain.repository.SensorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensorRepositoryImpl @Inject constructor(
    private val database: ShadowCheckDatabase
) : SensorRepository {

    override suspend fun purgeAllSensorReadings() = withContext(Dispatchers.IO) {
        database.sensorReadingDao().deleteAll()
    }
}
