package com.shadowcheck.mobile.wifi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shadowcheck.mobile.wifi.model.WifiNetwork

@Entity(tableName = "wifi_networks")
data class WifiNetworkEntity(
    @PrimaryKey val bssid: String,
    val ssid: String,
    val capabilities: String,
    val frequency: Int,
    val signalLevel: Int,
    val timestamp: Long,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

fun WifiNetworkEntity.toModel(): WifiNetwork = WifiNetwork(
    ssid = ssid,
    bssid = bssid,
    capabilities = capabilities,
    frequency = frequency,
    signalLevel = signalLevel,
    timestamp = timestamp,
    latitude = latitude,
    longitude = longitude
)

fun WifiNetwork.toEntity(): WifiNetworkEntity = WifiNetworkEntity(
    bssid = bssid,
    ssid = ssid,
    capabilities = capabilities,
    frequency = frequency,
    signalLevel = signalLevel,
    timestamp = timestamp,
    latitude = latitude,
    longitude = longitude
)
