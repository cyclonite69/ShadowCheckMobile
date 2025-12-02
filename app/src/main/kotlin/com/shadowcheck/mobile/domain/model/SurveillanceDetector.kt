package com.shadowcheck.mobile.domain.model

import android.location.Location
import com.shadowcheck.mobile.data.BluetoothDevice
import com.shadowcheck.mobile.data.CellularTower
import com.shadowcheck.mobile.data.WifiNetwork

class SurveillanceDetector {
    
    fun detectThreats(
        wifiNetworks: List<WifiNetwork>,
        btDevices: List<BluetoothDevice>,
        cellTowers: List<CellularTower>,
        userLocation: Location?
    ): List<ThreatDetection> {
        val threats = mutableListOf<ThreatDetection>()
        
        threats.addAll(detectStingray(cellTowers))
        threats.addAll(detectRogueAP(wifiNetworks))
        threats.addAll(detectTrackingBeacons(btDevices, userLocation))
        threats.addAll(detectWiFiPineapple(wifiNetworks))
        threats.addAll(detectHiddenCameras(wifiNetworks))
        threats.addAll(detectGPSJamming(userLocation, wifiNetworks))
        threats.addAll(detectAnomalousPatterns(wifiNetworks, btDevices, cellTowers))
        
        return threats.sortedByDescending { it.severity.ordinal }
    }
    
    private fun detectStingray(towers: List<CellularTower>): List<ThreatDetection> {
        val threats = mutableListOf<ThreatDetection>()
        
        // Group towers by location
        val towersByLocation = towers.groupBy { "${it.latitude},${it.longitude}" }
        
        // Detect multiple towers at same location (IMSI catcher indicator)
        towersByLocation.forEach { (_, towersAtLocation) ->
            if (towersAtLocation.size > 3) {
                threats.add(
                    ThreatDetection(
                        type = ThreatType.IMSI_CATCHER,
                        severity = ThreatSeverity.CRITICAL,
                        title = "Possible IMSI Catcher Detected",
                        description = "Multiple cell towers detected at same location",
                        confidence = 0.7f,
                        timestamp = System.currentTimeMillis(),
                        affectedDevices = towersAtLocation.map { "${it.cellId}" }
                    )
                )
            }
        }
        
        // Detect abnormal signal strength
        towers.forEach { tower ->
            if (tower.signalStrength > -50) {
                threats.add(
                    ThreatDetection(
                        type = ThreatType.IMSI_CATCHER,
                        severity = ThreatSeverity.HIGH,
                        title = "Abnormally Strong Cell Signal",
                        description = "Cell tower signal unusually strong (${tower.signalStrength} dBm)",
                        confidence = 0.6f,
                        timestamp = System.currentTimeMillis(),
                        affectedDevices = listOf("${tower.cellId}")
                    )
                )
            }
        }
        
        return threats
    }
    
    private fun detectRogueAP(networks: List<WifiNetwork>): List<ThreatDetection> {
        val threats = mutableListOf<ThreatDetection>()
        
        // Group by SSID to find duplicates
        val networksBySSID = networks.groupBy { it.ssid }
        
        networksBySSID.forEach { (ssid, nets) ->
            if (nets.size > 1 && ssid.isNotEmpty()) {
                // Multiple APs with same SSID (possible evil twin)
                threats.add(
                    ThreatDetection(
                        type = ThreatType.EVIL_TWIN,
                        severity = ThreatSeverity.HIGH,
                        title = "Possible Evil Twin AP",
                        description = "Multiple access points broadcasting '$ssid'",
                        confidence = 0.75f,
                        timestamp = System.currentTimeMillis(),
                        affectedDevices = nets.map { it.bssid }
                    )
                )
            }
        }
        
        // Detect hidden networks with strong signal
        networks.filter { it.ssid.isEmpty() && it.signalLevel > -50 }.forEach { network ->
            threats.add(
                ThreatDetection(
                    type = ThreatType.ROGUE_AP,
                    severity = ThreatSeverity.MEDIUM,
                    title = "Hidden Network Nearby",
                    description = "Strong hidden WiFi network detected",
                    confidence = 0.5f,
                    timestamp = System.currentTimeMillis(),
                    affectedDevices = listOf(network.bssid)
                )
            )
        }
        
        return threats
    }
    
    private fun detectTrackingBeacons(devices: List<BluetoothDevice>, location: Location?): List<ThreatDetection> {
        val threats = mutableListOf<ThreatDetection>()
        
        // Detect BLE beacons with strong signal
        devices.filter { it.rssi > -50 }.forEach { device ->
            threats.add(
                ThreatDetection(
                    type = ThreatType.SURVEILLANCE_DEVICE,
                    severity = ThreatSeverity.MEDIUM,
                    title = "Nearby Bluetooth Device",
                    description = "Strong Bluetooth signal from ${device.name ?: "Unknown"}",
                    confidence = 0.4f,
                    timestamp = System.currentTimeMillis(),
                    affectedDevices = listOf(device.address)
                )
            )
        }
        
        return threats
    }
    
    private fun detectWiFiPineapple(networks: List<WifiNetwork>): List<ThreatDetection> {
        val threats = mutableListOf<ThreatDetection>()
        
        // Detect common pineapple SSIDs
        val suspiciousSSIDs = listOf("Pineapple", "Free WiFi", "attwifi", "xfinitywifi")
        
        networks.filter { network ->
            suspiciousSSIDs.any { network.ssid.contains(it, ignoreCase = true) }
        }.forEach { network ->
            threats.add(
                ThreatDetection(
                    type = ThreatType.ROGUE_AP,
                    severity = ThreatSeverity.HIGH,
                    title = "Suspicious WiFi Network",
                    description = "Network '${network.ssid}' may be malicious",
                    confidence = 0.65f,
                    timestamp = System.currentTimeMillis(),
                    affectedDevices = listOf(network.bssid)
                )
            )
        }
        
        return threats
    }
    
    private fun detectHiddenCameras(networks: List<WifiNetwork>): List<ThreatDetection> {
        val threats = mutableListOf<ThreatDetection>()
        
        // Detect camera-related SSIDs
        val cameraKeywords = listOf("cam", "camera", "ipcam", "dvr", "nvr")
        
        networks.filter { network ->
            cameraKeywords.any { network.ssid.contains(it, ignoreCase = true) }
        }.forEach { network ->
            threats.add(
                ThreatDetection(
                    type = ThreatType.HIDDEN_CAMERA,
                    severity = ThreatSeverity.MEDIUM,
                    title = "Possible Hidden Camera",
                    description = "WiFi camera detected: ${network.ssid}",
                    confidence = 0.55f,
                    timestamp = System.currentTimeMillis(),
                    affectedDevices = listOf(network.bssid)
                )
            )
        }
        
        return threats
    }
    
    private fun detectGPSJamming(location: Location?, networks: List<WifiNetwork>): List<ThreatDetection> {
        val threats = mutableListOf<ThreatDetection>()
        
        // If we have networks but no GPS, possible jamming
        if (location == null && networks.isNotEmpty()) {
            threats.add(
                ThreatDetection(
                    type = ThreatType.GPS_JAMMER,
                    severity = ThreatSeverity.HIGH,
                    title = "GPS Signal Lost",
                    description = "GPS unavailable despite WiFi networks present",
                    confidence = 0.4f,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
        
        return threats
    }
    
    private fun detectAnomalousPatterns(
        networks: List<WifiNetwork>,
        devices: List<BluetoothDevice>,
        towers: List<CellularTower>
    ): List<ThreatDetection> {
        val threats = mutableListOf<ThreatDetection>()
        
        // Detect unusually high device density
        val totalDevices = networks.size + devices.size + towers.size
        if (totalDevices > 100) {
            threats.add(
                ThreatDetection(
                    type = ThreatType.UNKNOWN_THREAT,
                    severity = ThreatSeverity.LOW,
                    title = "High Device Density",
                    description = "$totalDevices devices detected in area",
                    confidence = 0.3f,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
        
        return threats
    }
}
