package com.shadowcheck.mobile.domain.model

data class ThreatDetection(
    val type: ThreatType,
    val severity: ThreatSeverity,
    val title: String,
    val description: String,
    val confidence: Float,
    val timestamp: Long,
    val affectedDevices: List<String> = emptyList()
)
