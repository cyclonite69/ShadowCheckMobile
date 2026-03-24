package com.shadowcheck.mobile.core.model

data class WifiNetwork(
    val bssid: String,
    val ssid: String,
    val frequency: Int,
    val signalLevel: Int,
    val capabilities: String,
    val timestamp: Long,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val channel: Int = 0,
    val channelWidth: Int = 0,
    val firstSeen: Long = timestamp,
    val lastSeen: Long = timestamp
)
