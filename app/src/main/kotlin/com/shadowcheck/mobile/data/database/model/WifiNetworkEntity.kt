package com.shadowcheck.mobile.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shadowcheck.mobile.core.model.WifiNetwork

@Entity(tableName = "wifi_networks")
data class WifiNetworkEntity(
    @PrimaryKey val bssid: String,
    val ssid: String,
    val capabilities: String,
    val frequency: Int,
    val level: Int,
    val timestamp: Long
)

fun WifiNetworkEntity.toDomainModel(): WifiNetwork =
    WifiNetwork(
        ssid = ssid,
        bssid = bssid,
        capabilities = capabilities,
        frequency = frequency,
        signalLevel = level,
        timestamp = timestamp
    )

fun WifiNetwork.toEntity(): WifiNetworkEntity =
    WifiNetworkEntity(
        bssid = bssid,
        ssid = ssid,
        capabilities = capabilities,
        frequency = frequency,
        level = signalLevel,
        timestamp = timestamp
    )
