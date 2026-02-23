package com.shadowcheck.mobile.domain.model

data class UnifiedSighting(
    val id: String,
    val name: String,
    val type: RadioType,
    val signalLevel: Int,
    val frequency: Int? = null,
    val channel: Int? = null,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val timestamp: Long,
    val capabilities: String = "",
    val channelWidth: Int? = null,
    val standard: String? = null,
    val centerFreq0: Int? = null,
    val centerFreq1: Int? = null,
    val isPasspoint: Boolean = false,
    val operator: String? = null,
    val venue: String? = null,
    val is80211mc: Boolean = false,
    val deviceClass: Int? = null,
    val cellId: Int? = null,
    val lac: Int? = null,
    val mcc: Int? = null,
    val mnc: Int? = null
)
