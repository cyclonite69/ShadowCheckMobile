package com.shadowcheck.mobile.domain.model

data class CellularTower(
    val cellId: Int,
    val lac: Int, // Location Area Code
    val mcc: Int, // Mobile Country Code
    val mnc: Int, // Mobile Network Code
    val signalStrength: Int,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)
