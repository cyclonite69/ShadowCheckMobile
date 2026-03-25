package com.shadowcheck.mobile.domain.repository

interface SensorRepository {
    suspend fun purgeAllSensorReadings()
}
