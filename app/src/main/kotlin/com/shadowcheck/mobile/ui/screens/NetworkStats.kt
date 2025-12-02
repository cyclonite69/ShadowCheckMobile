package com.shadowcheck.mobile.ui.screens

data class NetworkStats(
    val wifiUnique: Int,
    val wifiTotal: Int,
    val btUnique: Int,
    val btTotal: Int,
    val cellUnique: Int,
    val cellTotal: Int,
    val bleUnique: Int,
    val bleTotal: Int,
    val totalScans: Int,
    val lastScanTime: String,
    val scanDuration: String,
    val avgSignalStrength: Int,
    val maxDistance: Int,
    val minDistance: Int
)
