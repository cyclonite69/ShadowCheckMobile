package com.shadowcheck.mobile.models

data class WiFiFilters(
    val encryptionType: String? = null,
    val minSignalStrength: Int? = null,
    val band: String? = null
)