package com.shadowcheck.mobile.domain.model

data class WifiNetwork(
    val ssid: String,
    val bssid: String,
    val capabilities: String,
    val frequency: Int,
    val level: Int,
    val timestamp: Long
)
