package com.shadowcheck.mobile.network.dto

import com.google.gson.annotations.SerializedName

// Network search response
data class WigleNetworkSearchResponse(
    val success: Boolean,
    val totalResults: Int,
    val search_after: Long?,
    val results: List<WigleNetwork>
)

// Individual network result
data class WigleNetwork(
    val netid: String,
    val ssid: String,
    val qos: Int,
    val channel: Int,
    val bcninterval: Int,
    val freenet: String?,
    val dhcp: String?,
    val paynet: String?,
    val userfound: Boolean,
    val encryption: String,
    val country: String?,
    val region: String?,
    val city: String?,
    val housenumber: String?,
    val road: String?,
    val postalcode: String?,
    val trilat: Double,
    val trilong: Double,
    val lasttime: String,
    val lastupdt: String,
    val type: String
)

// User stats
data class WigleUserStats(
    val success: Boolean,
    val user: String?,
    val statistics: WigleStats?
)

data class WigleStats(
    val discoveredWiFiGPS: Long = 0,
    val discoveredWiFiGPSPercent: Double = 0.0,
    val discoveredWiFi: Long = 0,
    val discoveredCellGPS: Long = 0,
    val discoveredCell: Long = 0,
    val discoveredBtGPS: Long = 0,
    val discoveredBt: Long = 0,
    val totalWiFiLocations: Long = 0,
    val last: String? = null,
    val prevRank: Int = 0,
    val prevMonthRank: Int = 0,
    val monthRank: Int = 0,
    val rank: Int = 0,
    val eventMonthCount: Int = 0,
    val eventPrevMonthCount: Int = 0,
    val first: String? = null,
    val genDiscovered: Long = 0,
    val genTotal: Long = 0,
    val btDiscovered: Long = 0,
    val btTotal: Long = 0
)

// Upload response
data class WigleUploadResponse(
    val success: Boolean,
    val message: String?,
    val transid: String?,
    val observer: String?
)

// Transaction status
data class WigleTransactionStatus(
    val success: Boolean,
    val results: List<WigleTransaction>
)

data class WigleTransaction(
    val transid: String,
    val username: String,
    val firstTime: String,
    val lastupdt: String,
    val fileName: String,
    val fileSize: Long,
    val fileLines: Int,
    val status: String,
    val discoveredGps: Int,
    val discovered: Int,
    val total: Int,
    val totalGps: Int,
    val totalLocations: Int,
    val percentDone: Double,
    val timeParsing: Long,
    val genDiscovered: Int,
    val genTotal: Int,
    val btDiscovered: Int,
    val btTotal: Int
)
