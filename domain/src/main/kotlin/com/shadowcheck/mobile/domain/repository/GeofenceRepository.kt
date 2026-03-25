package com.shadowcheck.mobile.domain.repository

import com.shadowcheck.mobile.core.model.Geofence
import kotlinx.coroutines.flow.Flow

interface GeofenceRepository {
    fun getGeofences(): Flow<List<Geofence>>
    suspend fun upsertGeofence(geofence: Geofence)
    suspend fun deleteGeofence(geofence: Geofence)
}
