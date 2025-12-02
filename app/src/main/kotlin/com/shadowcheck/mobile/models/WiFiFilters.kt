package com.shadowcheck.mobile.models

import com.shadowcheck.mobile.data.WifiNetwork

data class WiFiFilters(
    val searchQuery: String = "",
    val minSignalStrength: Int = -100,
    val maxSignalStrength: Int = 0,
    val onlyWithLocation: Boolean = false,
    val maxAgeHours: Int? = null,
    val show2_4GHz: Boolean = true,
    val show5GHz: Boolean = true,
    val show6GHz: Boolean = true,
    val showOpen: Boolean = true,
    val showWEP: Boolean = true,
    val showWPA: Boolean = true,
    val showWPA2: Boolean = true,
    val showWPA3: Boolean = true,
    val showWiFi4: Boolean = true,
    val showWiFi5: Boolean = true,
    val showWiFi6: Boolean = true,
    val showWiFi6E: Boolean = true,
    val showWiFi7: Boolean = true
) {
    fun matches(network: WifiNetwork): Boolean {
        // Search query
        if (searchQuery.isNotEmpty()) {
            val query = searchQuery.lowercase()
            if (!network.ssid.lowercase().contains(query) &&
                !network.bssid.lowercase().contains(query)) {
                return false
            }
        }
        
        // Signal strength
        if (network.signalLevel < minSignalStrength || network.signalLevel > maxSignalStrength) {
            return false
        }
        
        // Location filter
        if (onlyWithLocation && (network.latitude == 0.0 && network.longitude == 0.0)) {
            return false
        }
        
        // Age filter
        maxAgeHours?.let { hours ->
            val cutoff = System.currentTimeMillis() - (hours * 3600 * 1000)
            if (network.timestamp < cutoff) return false
        }
        
        // Frequency bands
        when {
            network.frequency in 2400..2500 && !show2_4GHz -> return false
            network.frequency in 5000..5900 && !show5GHz -> return false
            network.frequency in 5925..7125 && !show6GHz -> return false
        }
        
        // Security
        val caps = network.capabilities.uppercase()
        when {
            caps.contains("WPA3") && !showWPA3 -> return false
            caps.contains("WPA2") && !showWPA2 -> return false
            caps.contains("WPA") && !showWPA -> return false
            caps.contains("WEP") && !showWEP -> return false
            !caps.contains("WPA") && !caps.contains("WEP") && !showOpen -> return false
        }
        
        // WiFi standards
        val std = network.standard.uppercase()
        when {
            std.contains("7") && !showWiFi7 -> return false
            std.contains("6E") && !showWiFi6E -> return false
            std.contains("6") && !showWiFi6 -> return false
            std.contains("5") || std.contains("AC") && !showWiFi5 -> return false
            std.contains("4") || std.contains("N") && !showWiFi4 -> return false
        }
        
        return true
    }
}
