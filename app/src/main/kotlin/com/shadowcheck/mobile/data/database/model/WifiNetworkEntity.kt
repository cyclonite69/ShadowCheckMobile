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
    val timestamp: Long,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val channel: Int = 0,
    val channelWidth: Int = 0,
    val centerFreq0: Int = 0,
    val centerFreq1: Int = 0,
    val standard: String = "",
    val maxDataRate: Int = 0,
    val is80211mc: Boolean = false,
    val isPasspoint: Boolean = false,
    val operatorFriendlyName: String = "",
    val venueName: String = "",
    val rcoi: String = "",
    val vendorOui: String = "",
    val vendorName: String = "",
    val altitude: Double = 0.0,
    val altitudeBarometric: Double = 0.0,
    val accuracy: Float = 0f,
    val speed: Float = 0f,
    val bearing: Float = 0f,
    val firstSeen: Long = timestamp,
    val lastSeen: Long = timestamp,
    val source: String = ""
)

fun WifiNetworkEntity.toDomainModel(): WifiNetwork =
    WifiNetwork(
        bssid = bssid,
        ssid = ssid,
        frequency = frequency,
        signalLevel = level,
        capabilities = capabilities,
        timestamp = timestamp,
        latitude = latitude,
        longitude = longitude,
        channel = channel,
        channelWidth = channelWidth,
        centerFreq0 = centerFreq0,
        centerFreq1 = centerFreq1,
        standard = standard,
        maxDataRate = maxDataRate,
        is80211mc = is80211mc,
        isPasspoint = isPasspoint,
        operatorFriendlyName = operatorFriendlyName,
        venueName = venueName,
        rcoi = rcoi,
        vendorOui = vendorOui,
        vendorName = vendorName,
        altitude = altitude,
        altitudeBarometric = altitudeBarometric,
        accuracy = accuracy,
        speed = speed,
        bearing = bearing,
        firstSeen = firstSeen,
        lastSeen = lastSeen,
        source = source
    )

fun WifiNetwork.toEntity(): WifiNetworkEntity =
    WifiNetworkEntity(
        bssid = bssid,
        ssid = ssid,
        capabilities = capabilities,
        frequency = frequency,
        level = signalLevel,
        timestamp = timestamp,
        latitude = latitude,
        longitude = longitude,
        channel = channel,
        channelWidth = channelWidth,
        centerFreq0 = centerFreq0,
        centerFreq1 = centerFreq1,
        standard = standard,
        maxDataRate = maxDataRate,
        is80211mc = is80211mc,
        isPasspoint = isPasspoint,
        operatorFriendlyName = operatorFriendlyName,
        venueName = venueName,
        rcoi = rcoi,
        vendorOui = vendorOui,
        vendorName = vendorName,
        altitude = altitude,
        altitudeBarometric = altitudeBarometric,
        accuracy = accuracy,
        speed = speed,
        bearing = bearing,
        firstSeen = firstSeen,
        lastSeen = lastSeen,
        source = source
    )
