package com.shadowcheck.mobile.domain.repository

import com.shadowcheck.mobile.core.model.SensorReading
import kotlinx.coroutines.flow.Flow

interface SensorReadingRepository {
    fun getRecentReadings(): Flow<List<SensorReading>>
    suspend fun insertReading(reading: SensorReading)
    suspend fun deleteOlderThan(cutoffTime: Long)
    suspend fun deleteAll()
}
