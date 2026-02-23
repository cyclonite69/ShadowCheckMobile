package com.shadowcheck.mobile.core.model

data class ThreatDetection(
    val type: ThreatType,
    val severity: ThreatSeverity,
    val description: String,
    val timestamp: Long,
    val location: Location? = null
)

data class Location(
    val latitude: Double,
    val longitude: Double
)
