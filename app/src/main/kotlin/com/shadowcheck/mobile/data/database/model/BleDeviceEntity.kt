package com.shadowcheck.mobile.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shadowcheck.mobile.core.model.BleDevice

@Entity(tableName = "ble_devices")
data class BleDeviceEntity(
    @PrimaryKey val macAddress: String,
    val name: String?,
    val rssi: Int,
    val sessionId: String = "",
    val txPower: Int = 0,
    val isConnectable: Boolean = false,
    val serviceUuids: String = "",
    val manufacturerData: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val timestamp: Long,
    val firstSeen: Long = timestamp,
    val lastSeen: Long = timestamp,
    val source: String = ""
)

fun BleDeviceEntity.toDomainModel(): BleDevice = BleDevice(
    macAddress = macAddress,
    name = name.orEmpty(),
    rssi = rssi,
    timestamp = timestamp,
    sessionId = sessionId,
    txPower = txPower,
    isConnectable = isConnectable,
    serviceUuids = serviceUuids,
    manufacturerData = manufacturerData,
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    accuracy = accuracy,
    firstSeen = firstSeen,
    lastSeen = lastSeen,
    source = source
)

fun BleDevice.toEntity(): BleDeviceEntity = BleDeviceEntity(
    macAddress = macAddress,
    name = name,
    rssi = rssi,
    timestamp = timestamp,
    sessionId = sessionId,
    txPower = txPower,
    isConnectable = isConnectable,
    serviceUuids = serviceUuids,
    manufacturerData = manufacturerData,
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    accuracy = accuracy,
    firstSeen = firstSeen,
    lastSeen = lastSeen,
    source = source
)
