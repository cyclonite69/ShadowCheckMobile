package com.shadowcheck.mobile.models

import com.shadowcheck.mobile.data.BluetoothDevice

data class BluetoothFilters(
    val searchQuery: String = "",
    val minRssi: Int = -100,
    val maxRssi: Int = 0,
    val onlyWithLocation: Boolean = false,
    val maxAgeHours: Int? = null,
    val showClassic: Boolean = true,
    val showBLE: Boolean = true,
    val showPaired: Boolean = true,
    val showUnpaired: Boolean = true
) {
    fun matches(device: BluetoothDevice): Boolean {
        // Search query
        if (searchQuery.isNotEmpty()) {
            val query = searchQuery.lowercase()
            val name = device.name?.lowercase() ?: ""
            if (!name.contains(query) && !device.address.lowercase().contains(query)) {
                return false
            }
        }
        
        // Signal strength
        if (device.rssi < minRssi || device.rssi > maxRssi) {
            return false
        }
        
        // Location filter
        if (onlyWithLocation && (device.latitude == 0.0 && device.longitude == 0.0)) {
            return false
        }
        
        // Age filter
        maxAgeHours?.let { hours ->
            val cutoff = System.currentTimeMillis() - (hours * 3600 * 1000)
            if (device.timestamp < cutoff) return false
        }
        
        // Device type (Classic vs BLE)
        when (device.deviceType) {
            1 -> if (!showClassic) return false  // DEVICE_TYPE_CLASSIC
            2 -> if (!showBLE) return false      // DEVICE_TYPE_LE
        }
        
        // Pairing status
        when (device.bondState) {
            12 -> if (!showPaired) return false    // BOND_BONDED
            else -> if (!showUnpaired) return false
        }
        
        return true
    }
}
