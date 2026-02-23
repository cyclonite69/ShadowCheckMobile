package com.shadowcheck.mobile.core.model

data class CellularTower(
    val cellId: Int,
    val mcc: Int,
    val mnc: Int,
    val lac: Int,
    val signalStrength: Int,
    val timestamp: Long,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
