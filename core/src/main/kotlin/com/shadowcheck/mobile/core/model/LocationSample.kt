package com.shadowcheck.mobile.core.model

data class LocationSample(
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
