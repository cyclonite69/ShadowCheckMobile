package com.shadowcheck.mobile.models

import com.shadowcheck.mobile.data.CellularTower

data class CellularFilters(
    val searchQuery: String = "",
    val minSignalStrength: Int = -120,
    val maxSignalStrength: Int = 0,
    val onlyWithLocation: Boolean = false,
    val maxAgeHours: Int? = null,
    val showGSM: Boolean = true,
    val showCDMA: Boolean = true,
    val showLTE: Boolean = true,
    val show5G: Boolean = true
) {
    fun matches(tower: CellularTower): Boolean {
        // Search query
        if (searchQuery.isNotEmpty()) {
            val query = searchQuery.lowercase()
            if (!tower.operatorName.lowercase().contains(query) &&
                !tower.cellId.toString().contains(query)) {
                return false
            }
        }
        
        // Signal strength
        if (tower.signalStrength < minSignalStrength || tower.signalStrength > maxSignalStrength) {
            return false
        }
        
        // Location filter
        if (onlyWithLocation && (tower.latitude == 0.0 && tower.longitude == 0.0)) {
            return false
        }
        
        // Age filter
        maxAgeHours?.let { hours ->
            val cutoff = System.currentTimeMillis() - (hours * 3600 * 1000)
            if (tower.timestamp < cutoff) return false
        }
        
        // Network type
        val type = tower.networkType.uppercase()
        when {
            type.contains("5G") || type.contains("NR") -> if (!show5G) return false
            type.contains("LTE") -> if (!showLTE) return false
            type.contains("CDMA") -> if (!showCDMA) return false
            type.contains("GSM") -> if (!showGSM) return false
        }
        
        return true
    }
}
