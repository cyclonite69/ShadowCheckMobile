package com.shadowcheck.mobile.core.model

data class SensorReading(
    val sensorType: String,
    val valueX: Float,
    val valueY: Float = 0f,
    val valueZ: Float = 0f,
    val accuracy: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)
