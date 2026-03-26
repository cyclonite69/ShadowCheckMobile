package com.shadowcheck.mobile.data.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.shadowcheck.mobile.core.model.LocationSample

@Entity(
    tableName = "location_samples",
    indices = [Index(value = ["timestamp"])]
)
data class LocationSampleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String = "",
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val altitude: Double? = null,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val bearing: Float? = null,
    val provider: String = "",
    val elapsedRealtimeNanos: Long = 0L,
    val verticalAccuracyMeters: Float? = null
)

fun LocationSampleEntity.toDomainModel(): LocationSample = LocationSample(
    sessionId = sessionId,
    latitude = latitude,
    longitude = longitude,
    timestamp = timestamp,
    altitude = altitude,
    accuracy = accuracy,
    speed = speed,
    bearing = bearing,
    provider = provider,
    elapsedRealtimeNanos = elapsedRealtimeNanos,
    verticalAccuracyMeters = verticalAccuracyMeters
)

fun LocationSample.toEntity(): LocationSampleEntity = LocationSampleEntity(
    sessionId = sessionId,
    latitude = latitude,
    longitude = longitude,
    timestamp = timestamp,
    altitude = altitude,
    accuracy = accuracy,
    speed = speed,
    bearing = bearing,
    provider = provider,
    elapsedRealtimeNanos = elapsedRealtimeNanos,
    verticalAccuracyMeters = verticalAccuracyMeters
)
