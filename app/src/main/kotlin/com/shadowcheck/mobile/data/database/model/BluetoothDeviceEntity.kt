package com.shadowcheck.mobile.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shadowcheck.mobile.domain.model.BluetoothDevice

@Entity(tableName = "bluetooth_devices")
data class BluetoothDeviceEntity(
    @PrimaryKey val macAddress: String,
    val name: String?,
    val type: Int,
    val rssi: Int,
    val timestamp: Long
)

fun BluetoothDeviceEntity.toDomainModel(): BluetoothDevice = BluetoothDevice(macAddress, name, type, rssi, timestamp)

fun BluetoothDevice.toEntity(): BluetoothDeviceEntity = BluetoothDeviceEntity(macAddress, name, type, rssi, timestamp)
