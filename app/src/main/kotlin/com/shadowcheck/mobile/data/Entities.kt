package com.shadowcheck.mobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bluetooth_devices")
data class BluetoothDevice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val address: String,
    val name: String?,
    val rssi: Int,
    val deviceClass: Int = 0,
    val bondState: Int = 0,
    val deviceType: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val timestamp: Long,
    val firstSeen: Long = 0,
    val lastSeen: Long = 0,
    val source: String = ""
)

@Entity(tableName = "ble_devices")
data class BleDevice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val address: String,
    val name: String?,
    val rssi: Int,
    val txPower: Int = 0,
    val isConnectable: Boolean = false,
    val serviceUuids: String = "",
    val manufacturerData: String = "",
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val timestamp: Long,
    val firstSeen: Long = 0,
    val lastSeen: Long = 0,
    val source: String = ""
)

@Entity(tableName = "cellular_towers")
data class CellularTower(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cellId: Int,
    val lac: Int,
    val mcc: Int,
    val mnc: Int,
    val psc: Int = 0,
    val signalStrength: Int,
    val signalQuality: Int = 0,
    val networkType: String = "",
    val operatorName: String = "",
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val timestamp: Long,
    val firstSeen: Long = 0,
    val lastSeen: Long = 0,
    val source: String = ""
)

@Entity(tableName = "sensor_readings")
data class SensorReading(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sensorType: String,
    val valueX: Float,
    val valueY: Float = 0f,
    val valueZ: Float = 0f,
    val accuracy: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)

@Entity(tableName = "hardware_metadata")
data class HardwareMetadata(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val macAddress: String,
    val manufacturer: String = "",
    val model: String = "",
    val deviceType: String = "",
    val capabilities: String = "",
    val notes: String = "",
    val lastUpdated: Long
)

@Entity(tableName = "radio_manufacturers")
data class RadioManufacturer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ouiPrefix: String,
    val manufacturer: String,
    val deviceType: String = "",
    val notes: String = ""
)

@Entity(tableName = "geofences")
data class Geofence(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val isActive: Boolean = true,
    val notifyOnEntry: Boolean = true,
    val notifyOnExit: Boolean = true,
    val createdAt: Long
)

@Entity(tableName = "network_notes")
data class NetworkNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val networkId: String,
    val networkType: String,
    val note: String,
    val tags: String = "",
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "device_tags")
data class DeviceTag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deviceId: String,
    val tag: String,
    val createdAt: Long
)

@Entity(tableName = "api_tokens")
data class ApiToken(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val service: String,
    val token: String,
    val expiresAt: Long = 0,
    val createdAt: Long
)

@Entity(tableName = "api_usage")
data class ApiUsage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val service: String,
    val endpoint: String,
    val requestCount: Int,
    val lastUsed: Long
)

@Entity(tableName = "media_attachments")
data class MediaAttachment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val networkId: String,
    val networkType: String,
    val filePath: String,
    val mimeType: String,
    val createdAt: Long
)
