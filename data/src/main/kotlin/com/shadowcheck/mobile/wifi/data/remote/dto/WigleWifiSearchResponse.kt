package com.shadowcheck.mobile.wifi.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.shadowcheck.mobile.wifi.data.local.entity.WifiNetworkEntity
import java.time.Instant

data class WigleWifiSearchResponse(
    val success: Boolean,
    val results: List<WigleNetworkDto>
)

data class WigleNetworkDto(
    @SerializedName("trilat") val trilat: Double,
    @SerializedName("trilong") val trilong: Double,
    val ssid: String,
    val bssid: String,
    val channel: Int,
    val encryption: String,
    val lastupdt: String,
    @SerializedName("qos") val level: Int
)

fun WigleNetworkDto.toEntity(): WifiNetworkEntity {
    return WifiNetworkEntity(
        ssid = ssid,
        bssid = bssid,
        capabilities = encryption,
        frequency = 0,
        signalLevel = level,
        timestamp = Instant.now().toEpochMilli(),
        latitude = trilat,
        longitude = trilong
    )
}
