package com.shadowcheck.mobile.data.repository

import android.content.Context
import com.shadowcheck.mobile.core.model.Geofence
import com.shadowcheck.mobile.data.ShadowCheckDatabase
import com.shadowcheck.mobile.domain.repository.GeofenceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class GeofenceRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : GeofenceRepository {

    private val database = ShadowCheckDatabase.getDatabase(context)

    override fun getGeofences(): Flow<List<Geofence>> {
        return database.geofenceDao().getAllFlow().map { geofences ->
            geofences.map { entity ->
                Geofence(
                    id = entity.id,
                    name = entity.name,
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                    radius = entity.radius,
                    isActive = entity.isActive,
                    notifyOnEntry = entity.notifyOnEntry,
                    notifyOnExit = entity.notifyOnExit,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override suspend fun upsertGeofence(geofence: Geofence) {
        database.geofenceDao().insert(
            com.shadowcheck.mobile.data.Geofence(
                id = geofence.id,
                name = geofence.name,
                latitude = geofence.latitude,
                longitude = geofence.longitude,
                radius = geofence.radius,
                isActive = geofence.isActive,
                notifyOnEntry = geofence.notifyOnEntry,
                notifyOnExit = geofence.notifyOnExit,
                createdAt = geofence.createdAt
            )
        )
    }

    override suspend fun deleteGeofence(geofence: Geofence) {
        database.geofenceDao().delete(
            com.shadowcheck.mobile.data.Geofence(
                id = geofence.id,
                name = geofence.name,
                latitude = geofence.latitude,
                longitude = geofence.longitude,
                radius = geofence.radius,
                isActive = geofence.isActive,
                notifyOnEntry = geofence.notifyOnEntry,
                notifyOnExit = geofence.notifyOnExit,
                createdAt = geofence.createdAt
            )
        )
    }
}
