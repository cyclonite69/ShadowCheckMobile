package com.shadowcheck.mobile.wifi.model

data class WifiNetwork(
    val ssid: String,
    val bssid: String,
    val capabilities: String,
    val frequency: Int,
    val signalLevel: Int,
    val timestamp: Long,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
