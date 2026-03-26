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
    val timestamp: Long,
    val sessionId: String = "",
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

fun BluetoothDeviceEntity.toDomainModel(): BluetoothDevice = BluetoothDevice(
    macAddress = macAddress,
    name = name.orEmpty(),
    rssi = rssi,
    timestamp = timestamp,
    sessionId = sessionId,
    deviceType = type,
    latitude = latitude,
    longitude = longitude,
    deviceClass = deviceClass,
    bondState = bondState,
    altitude = altitude,
    accuracy = accuracy,
    firstSeen = firstSeen,
    lastSeen = lastSeen,
    source = source
)

fun BluetoothDevice.toEntity(): BluetoothDeviceEntity = BluetoothDeviceEntity(
    macAddress = macAddress,
    name = name,
    type = deviceType,
    rssi = rssi,
    timestamp = timestamp,
    sessionId = sessionId,
    latitude = latitude,
    longitude = longitude,
    deviceClass = deviceClass,
    bondState = bondState,
    altitude = altitude,
    accuracy = accuracy,
    firstSeen = firstSeen,
    lastSeen = lastSeen,
    source = source
)
