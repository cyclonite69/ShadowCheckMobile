package com.shadowcheck.mobile.core.model

data class HardwareMetadata(
    val macAddress: String,
    val manufacturer: String = "",
    val model: String = "",
    val deviceType: String = "",
    val capabilities: String = "",
    val notes: String = "",
    val lastUpdated: Long
)
