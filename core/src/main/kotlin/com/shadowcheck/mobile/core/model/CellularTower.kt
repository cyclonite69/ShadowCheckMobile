package com.shadowcheck.mobile.core.model

data class CellularTower(
    val cellId: Int,
    val mcc: Int,
    val mnc: Int,
    val lac: Int,
    val signalStrength: Int,
    val timestamp: Long,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val psc: Int = 0,
    val signalQuality: Int = 0,
    val networkType: String = "",
    val operatorName: String = "",
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val firstSeen: Long = timestamp,
    val lastSeen: Long = timestamp,
    val source: String = ""
)
