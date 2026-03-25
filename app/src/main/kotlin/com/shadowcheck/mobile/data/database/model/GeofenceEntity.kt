package com.shadowcheck.mobile.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shadowcheck.mobile.core.model.Geofence

@Entity(tableName = "geofences")
data class GeofenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val isActive: Boolean = true,
    val notifyOnEntry: Boolean = true,
    val notifyOnExit: Boolean = true,
    val createdAt: Long
)

fun GeofenceEntity.toDomainModel(): Geofence = Geofence(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    radius = radius,
    isActive = isActive,
    notifyOnEntry = notifyOnEntry,
    notifyOnExit = notifyOnExit,
    createdAt = createdAt
)

fun Geofence.toEntity(): GeofenceEntity = GeofenceEntity(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    radius = radius,
    isActive = isActive,
    notifyOnEntry = notifyOnEntry,
    notifyOnExit = notifyOnExit,
    createdAt = createdAt
)
