package com.shadowcheck.mobile.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shadowcheck.mobile.core.model.SensorReading

@Entity(tableName = "sensor_readings")
data class SensorReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sensorType: String,
    val valueX: Float,
    val valueY: Float = 0f,
    val valueZ: Float = 0f,
    val accuracy: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val eventTimestampNanos: Long = 0L
)

fun SensorReadingEntity.toDomainModel(): SensorReading = SensorReading(
    sensorType = sensorType,
    valueX = valueX,
    valueY = valueY,
    valueZ = valueZ,
    accuracy = accuracy,
    latitude = latitude,
    longitude = longitude,
    timestamp = timestamp,
    eventTimestampNanos = eventTimestampNanos
)

fun SensorReading.toEntity(): SensorReadingEntity = SensorReadingEntity(
    sensorType = sensorType,
    valueX = valueX,
    valueY = valueY,
    valueZ = valueZ,
    accuracy = accuracy,
    latitude = latitude,
    longitude = longitude,
    timestamp = timestamp,
    eventTimestampNanos = eventTimestampNanos
)
