package com.shadowcheck.mobile.core.model

data class BluetoothDevice(
    val macAddress: String,
    val name: String,
    val rssi: Int,
    val timestamp: Long,
    val sessionId: String = "",
    val deviceType: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val deviceClass: Int = 0,
    val bondState: Int = 0,
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val firstSeen: Long = timestamp,
    val lastSeen: Long = timestamp,
    val source: String = ""
)
