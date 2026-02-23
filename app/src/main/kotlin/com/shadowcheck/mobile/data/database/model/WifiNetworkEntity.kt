package com.shadowcheck.mobile.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shadowcheck.mobile.domain.model.WifiNetwork

@Entity(tableName = "wifi_networks")
data class WifiNetworkEntity(
    @PrimaryKey val bssid: String,
    val ssid: String,
    val capabilities: String,
    val frequency: Int,
    val level: Int,
    val timestamp: Long
)

fun WifiNetworkEntity.toDomainModel(): WifiNetwork = WifiNetwork(ssid, bssid, capabilities, frequency, level, timestamp)

fun WifiNetwork.toEntity(): WifiNetworkEntity = WifiNetworkEntity(bssid, ssid, capabilities, frequency, level, timestamp)
