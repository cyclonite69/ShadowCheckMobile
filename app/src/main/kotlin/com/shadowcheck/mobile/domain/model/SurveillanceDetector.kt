package com.shadowcheck.mobile.domain.model

import javax.inject.Inject
import javax.inject.Singleton
import com.shadowcheck.mobile.core.model.WifiNetwork

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
        towers: List<CellularTower>
    ): List<ThreatDetection> {
        return buildList {
            addAll(detectThreats(wifiNetworks, btDevices))
            addAll(detectCellularAnomalies(towers))
            if (wifiNetworks.isNotEmpty() && towers.isEmpty()) {
                add(
                    ThreatDetection(
                        type = ThreatType.GPS_JAMMER,
                        severity = ThreatSeverity.LOW,
                        title = "Location Signal Incomplete",
                        description = "WiFi networks are present without corroborating cellular data",
                        confidence = 0.2f,
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
                            title = "Suspicious Network",
                            description = "Potentially unsafe WiFi network: ${network.ssid.ifBlank { "<hidden>" }}",
                            confidence = 0.6f,
                            timestamp = System.currentTimeMillis(),
                            affectedDevices = listOf(network.bssid)
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
                            title = "Duplicate SSID",
                            description = "Multiple access points broadcasting '$ssid'",
                            confidence = 0.75f,
                            timestamp = System.currentTimeMillis(),
                            affectedDevices = duplicates.map { it.bssid }
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
                    type = ThreatType.SURVEILLANCE_DEVICE,
                    severity = ThreatSeverity.MEDIUM,
                    title = "Strong Bluetooth Signal",
                    description = "Nearby Bluetooth device: ${device.name ?: device.macAddress}",
                    confidence = 0.4f,
                    timestamp = System.currentTimeMillis(),
                    affectedDevices = listOf(device.macAddress)
                )
            }
    }

    private fun detectCellularAnomalies(towers: List<CellularTower>): List<ThreatDetection> {
        return towers
            .filter { it.signalStrength > -50 }
            .map { tower ->
                ThreatDetection(
                    type = ThreatType.IMSI_CATCHER,
                    severity = ThreatSeverity.HIGH,
                    title = "Abnormal Cell Signal",
                    description = "Unusually strong cellular signal from tower ${tower.cellId}",
                    confidence = 0.6f,
                    timestamp = System.currentTimeMillis(),
                    affectedDevices = listOf(tower.cellId.toString())
                )
            }
    }
}
