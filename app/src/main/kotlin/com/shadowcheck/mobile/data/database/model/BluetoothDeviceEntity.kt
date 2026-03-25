package com.shadowcheck.mobile.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shadowcheck.mobile.core.model.BluetoothDevice

@Entity(tableName = "bluetooth_devices")
data class BluetoothDeviceEntity(
    @PrimaryKey val macAddress: String,
    val name: String?,
    val type: Int,
    val rssi: Int,
    val timestamp: Long
)

fun BluetoothDeviceEntity.toDomainModel(): BluetoothDevice = BluetoothDevice(
    macAddress = macAddress,
    name = name.orEmpty(),
    rssi = rssi,
    timestamp = timestamp,
    deviceType = type
)

fun BluetoothDevice.toEntity(): BluetoothDeviceEntity = BluetoothDeviceEntity(
    macAddress = macAddress,
    name = name,
    type = deviceType,
    rssi = rssi,
    timestamp = timestamp
)
