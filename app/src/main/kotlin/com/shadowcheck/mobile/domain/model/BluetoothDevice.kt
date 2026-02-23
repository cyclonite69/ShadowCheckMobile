package com.shadowcheck.mobile.domain.model

data class BluetoothDevice(
    val macAddress: String,
    val name: String?,
    val type: Int, // e.g., BluetoothDevice.DEVICE_TYPE_CLASSIC
    val rssi: Int,
    val timestamp: Long
)
