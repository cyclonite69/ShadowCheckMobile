package com.shadowcheck.mobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wifi_networks")
data class WifiNetwork(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bssid: String,
    val ssid: String,
    val frequency: Int,
    val signalLevel: Int,
    val capabilities: String = "",
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
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val altitudeBarometric: Double = 0.0,
    val accuracy: Float = 0f,
    val speed: Float = 0f,
    val bearing: Float = 0f,
    val timestamp: Long,
    val firstSeen: Long = 0,
    val lastSeen: Long = 0,
    val source: String = ""
)
