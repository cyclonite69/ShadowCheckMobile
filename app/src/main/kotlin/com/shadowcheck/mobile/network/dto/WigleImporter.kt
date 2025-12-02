package com.shadowcheck.mobile.network.dto

import com.shadowcheck.mobile.data.WifiNetwork
import com.shadowcheck.mobile.data.WifiNetworkDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WigleImporter(
    private val wifiDao: WifiNetworkDao
) {
    
    suspend fun importFromWigle(networks: List<WigleNetwork>) = withContext(Dispatchers.IO) {
        val wifiNetworks = networks.map { wigleNet ->
            WifiNetwork(
                bssid = wigleNet.netid,
                ssid = wigleNet.ssid,
                frequency = channelToFrequency(wigleNet.channel),
                signalLevel = wigleNet.qos,
                capabilities = wigleNet.encryption,
                channel = wigleNet.channel,
                latitude = wigleNet.trilat,
                longitude = wigleNet.trilong,
                timestamp = parseWigleTime(wigleNet.lasttime),
                source = "wigle"
            )
        }
        
        wifiDao.insertAll(wifiNetworks)
    }
    
    private fun channelToFrequency(channel: Int): Int {
        return when (channel) {
            in 1..14 -> 2407 + (channel * 5)
            in 36..165 -> 5000 + (channel * 5)
            else -> 0
        }
    }
    
    private fun parseWigleTime(timeStr: String): Long {
        return try {
            // WiGLE format: "2024-12-01T08:00:00.000Z"
            java.time.Instant.parse(timeStr).toEpochMilli()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}
