package com.shadowcheck.mobile.core.model

data class BluetoothDevice(
    val macAddress: String,
    val name: String,
    val rssi: Int,
    val timestamp: Long,
    val deviceType: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
