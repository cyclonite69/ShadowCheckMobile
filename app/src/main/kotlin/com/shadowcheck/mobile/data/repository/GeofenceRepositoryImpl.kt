package com.shadowcheck.mobile.data.repository

import com.shadowcheck.mobile.core.model.Geofence
import com.shadowcheck.mobile.data.database.dao.GeofenceDao
import com.shadowcheck.mobile.data.database.model.toDomainModel
import com.shadowcheck.mobile.data.database.model.toEntity
import com.shadowcheck.mobile.domain.repository.GeofenceRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class GeofenceRepositoryImpl @Inject constructor(
    private val geofenceDao: GeofenceDao
) : GeofenceRepository {

    override fun getGeofences(): Flow<List<Geofence>> {
        return geofenceDao.getAllGeofences().map { geofences ->
            geofences.map { it.toDomainModel() }
        }
    }

    override suspend fun upsertGeofence(geofence: Geofence) {
        geofenceDao.insertGeofence(geofence.toEntity())
    }

    override suspend fun deleteGeofence(geofence: Geofence) {
        geofenceDao.deleteGeofence(geofence.toEntity())
    }
}
