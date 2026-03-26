package com.shadowcheck.mobile.domain.repository

import com.shadowcheck.mobile.core.model.LocationSample
import kotlinx.coroutines.flow.Flow

interface LocationSampleRepository {
    fun getRecentSamples(limit: Int = 1000): Flow<List<LocationSample>>
    fun getSamplesForSession(sessionId: String): Flow<List<LocationSample>>
    fun getSamplesBetween(startTime: Long, endTime: Long): Flow<List<LocationSample>>
    suspend fun insertSample(sample: LocationSample)
    suspend fun insertSamples(samples: List<LocationSample>)
    suspend fun deleteOlderThan(cutoffTime: Long)
}
