package com.shadowcheck.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.shadowcheck.mobile.data.database.model.WifiNetworkEntity
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
        ssid = this.ssid,
        bssid = this.bssid,
        level = this.level,
        capabilities = this.encryption,
        frequency = 0, // DTO does not provide frequency, default to 0
        timestamp = Instant.now().toEpochMilli() // Use current time for synced data
    )
}
