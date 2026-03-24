package com.shadowcheck.mobile.core.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurveillanceDetector @Inject constructor() {

    fun detectThreats(
        wifiNetworks: List<WifiNetwork>,
        btDevices: List<BluetoothDevice>
    ): List<ThreatDetection> {
        return buildList {
            addAll(detectRogueAccessPoints(wifiNetworks))
            addAll(detectSuspiciousBluetoothDevices(btDevices))
        }
    }

    fun detectThreats(
        wifiNetworks: List<WifiNetwork>,
        btDevices: List<BluetoothDevice>,
        towers: List<CellularTower>,
        location: Location? = null
    ): List<ThreatDetection> {
        return buildList {
            addAll(detectThreats(wifiNetworks, btDevices))
            addAll(detectCellularAnomalies(towers))
            if (location == null && wifiNetworks.isNotEmpty()) {
                add(
                    ThreatDetection(
                        type = ThreatType.GPS_JAMMING,
                        severity = ThreatSeverity.MEDIUM,
                        description = "GPS unavailable while nearby radios are present",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    private fun detectRogueAccessPoints(networks: List<WifiNetwork>): List<ThreatDetection> {
        val suspiciousSsids = listOf("pineapple", "free wifi", "xfinitywifi", "attwifi")

        return buildList {
            networks
                .filter { network ->
                    network.ssid.isBlank() || suspiciousSsids.any { token ->
                        network.ssid.contains(token, ignoreCase = true)
                    }
                }
                .forEach { network ->
                    add(
                        ThreatDetection(
                            type = ThreatType.ROGUE_AP,
                            severity = ThreatSeverity.HIGH,
                            description = "Suspicious network detected: ${network.ssid.ifBlank { "<hidden>" }}",
                            timestamp = System.currentTimeMillis(),
                            location = network.toLocation()
                        )
                    )
                }

            networks
                .groupBy { it.ssid }
                .filterKeys { it.isNotBlank() }
                .filterValues { it.size > 1 }
                .forEach { (ssid, duplicates) ->
                    add(
                        ThreatDetection(
                            type = ThreatType.EVIL_TWIN,
                            severity = ThreatSeverity.HIGH,
                            description = "Multiple access points broadcasting '$ssid' (${duplicates.size} seen)",
                            timestamp = System.currentTimeMillis(),
                            location = duplicates.first().toLocation()
                        )
                    )
                }
        }
    }

    private fun detectSuspiciousBluetoothDevices(devices: List<BluetoothDevice>): List<ThreatDetection> {
        return devices
            .filter { it.rssi > -50 }
            .map { device ->
                ThreatDetection(
                    type = ThreatType.TRACKING_BEACON,
                    severity = ThreatSeverity.MEDIUM,
                    description = "Strong Bluetooth signal from ${device.name.ifBlank { device.macAddress }}",
                    timestamp = System.currentTimeMillis(),
                    location = device.toLocation()
                )
            }
    }

    private fun detectCellularAnomalies(towers: List<CellularTower>): List<ThreatDetection> {
        return towers
            .filter { it.signalStrength > -50 }
            .map { tower ->
                ThreatDetection(
                    type = ThreatType.STINGRAY,
                    severity = ThreatSeverity.HIGH,
                    description = "Abnormally strong cellular signal from tower ${tower.cellId}",
                    timestamp = System.currentTimeMillis(),
                    location = tower.toLocation()
                )
            }
    }

    private fun WifiNetwork.toLocation(): Location? =
        if (latitude == 0.0 && longitude == 0.0) null else Location(latitude, longitude)

    private fun BluetoothDevice.toLocation(): Location? =
        if (latitude == 0.0 && longitude == 0.0) null else Location(latitude, longitude)

    private fun CellularTower.toLocation(): Location? =
        if (latitude == 0.0 && longitude == 0.0) null else Location(latitude, longitude)
}
