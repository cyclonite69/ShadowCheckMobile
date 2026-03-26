package com.shadowcheck.mobile.core.model

data class BleDevice(
    val macAddress: String,
    val name: String,
    val rssi: Int,
    val timestamp: Long,
    val sessionId: String = "",
    val txPower: Int = 0,
    val isConnectable: Boolean = false,
    val serviceUuids: String = "",
    val manufacturerData: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val firstSeen: Long = timestamp,
    val lastSeen: Long = timestamp,
    val source: String = ""
)
