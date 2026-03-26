package com.shadowcheck.mobile.data.repository

import com.shadowcheck.mobile.core.di.IoDispatcher
import com.shadowcheck.mobile.core.model.LocationSample
import com.shadowcheck.mobile.data.database.dao.LocationSampleDao
import com.shadowcheck.mobile.data.database.model.toDomainModel
import com.shadowcheck.mobile.data.database.model.toEntity
import com.shadowcheck.mobile.domain.repository.LocationSampleRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class LocationSampleRepositoryImpl @Inject constructor(
    private val locationSampleDao: LocationSampleDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : LocationSampleRepository {

    override fun getRecentSamples(limit: Int): Flow<List<LocationSample>> {
        return locationSampleDao.getRecentSamples(limit)
            .map { samples -> samples.map { it.toDomainModel() } }
            .flowOn(dispatcher)
    }

    override fun getSamplesForSession(sessionId: String): Flow<List<LocationSample>> {
        return locationSampleDao.getSamplesForSession(sessionId)
            .map { samples -> samples.map { it.toDomainModel() } }
            .flowOn(dispatcher)
    }

    override fun getSamplesBetween(startTime: Long, endTime: Long): Flow<List<LocationSample>> {
        return locationSampleDao.getSamplesBetween(startTime, endTime)
            .map { samples -> samples.map { it.toDomainModel() } }
            .flowOn(dispatcher)
    }

    override suspend fun insertSample(sample: LocationSample) = withContext(dispatcher) {
        locationSampleDao.insertSample(sample.toEntity())
    }

    override suspend fun insertSamples(samples: List<LocationSample>) = withContext(dispatcher) {
        locationSampleDao.insertSamples(samples.map { it.toEntity() })
    }

    override suspend fun deleteOlderThan(cutoffTime: Long) = withContext(dispatcher) {
        locationSampleDao.deleteOlderThan(cutoffTime)
    }
}
