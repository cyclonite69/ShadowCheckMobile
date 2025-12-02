package com.shadowcheck.mobile.utils

import com.shadowcheck.mobile.data.BluetoothDevice
import com.shadowcheck.mobile.data.CellularTower
import com.shadowcheck.mobile.data.WifiNetwork
import java.io.File

object ExportUtils {
    
    fun exportCSV(
        wifiNetworks: List<WifiNetwork>,
        btDevices: List<BluetoothDevice>,
        cellTowers: List<CellularTower>,
        outputFile: File
    ) {
        outputFile.bufferedWriter().use { writer ->
            // WiFi header
            writer.write("Type,ID,Name,Signal,Frequency,Latitude,Longitude,Timestamp\n")
            
            // WiFi data
            wifiNetworks.forEach { network ->
                writer.write("WiFi,${network.bssid},${network.ssid},${network.signalLevel},${network.frequency},${network.latitude},${network.longitude},${network.timestamp}\n")
            }
            
            // Bluetooth data
            btDevices.forEach { device ->
                writer.write("Bluetooth,${device.address},${device.name ?: ""},${device.rssi},,${device.latitude},${device.longitude},${device.timestamp}\n")
            }
            
            // Cellular data
            cellTowers.forEach { tower ->
                writer.write("Cellular,${tower.cellId},${tower.operatorName},${tower.signalStrength},,${tower.latitude},${tower.longitude},${tower.timestamp}\n")
            }
        }
    }
    
    fun exportKML(
        wifiNetworks: List<WifiNetwork>,
        btDevices: List<BluetoothDevice>,
        cellTowers: List<CellularTower>,
        outputFile: File
    ) {
        outputFile.bufferedWriter().use { writer ->
            writer.write("""
                <?xml version="1.0" encoding="UTF-8"?>
                <kml xmlns="http://www.opengis.net/kml/2.2">
                <Document>
                    <name>ShadowCheck Export</name>
            """.trimIndent())
            
            // WiFi placemarks
            writer.write("\n<Folder><name>WiFi Networks</name>\n")
            wifiNetworks.filter { it.latitude != 0.0 && it.longitude != 0.0 }.forEach { network ->
                writer.write("""
                    <Placemark>
                        <name>${escapeXml(network.ssid)}</name>
                        <description>BSSID: ${network.bssid}, Signal: ${network.signalLevel} dBm</description>
                        <Point><coordinates>${network.longitude},${network.latitude},0</coordinates></Point>
                    </Placemark>
                """.trimIndent())
            }
            writer.write("</Folder>\n")
            
            // Bluetooth placemarks
            writer.write("<Folder><name>Bluetooth Devices</name>\n")
            btDevices.filter { it.latitude != 0.0 && it.longitude != 0.0 }.forEach { device ->
                writer.write("""
                    <Placemark>
                        <name>${escapeXml(device.name ?: "Unknown")}</name>
                        <description>Address: ${device.address}, RSSI: ${device.rssi} dBm</description>
                        <Point><coordinates>${device.longitude},${device.latitude},0</coordinates></Point>
                    </Placemark>
                """.trimIndent())
            }
            writer.write("</Folder>\n")
            
            // Cellular placemarks
            writer.write("<Folder><name>Cell Towers</name>\n")
            cellTowers.filter { it.latitude != 0.0 && it.longitude != 0.0 }.forEach { tower ->
                writer.write("""
                    <Placemark>
                        <name>${escapeXml(tower.operatorName)}</name>
                        <description>Cell ID: ${tower.cellId}, Signal: ${tower.signalStrength} dBm</description>
                        <Point><coordinates>${tower.longitude},${tower.latitude},0</coordinates></Point>
                    </Placemark>
                """.trimIndent())
            }
            writer.write("</Folder>\n")
            
            writer.write("</Document>\n</kml>")
        }
    }
    
    fun exportGeoJSON(
        wifiNetworks: List<WifiNetwork>,
        btDevices: List<BluetoothDevice>,
        cellTowers: List<CellularTower>,
        outputFile: File
    ) {
        outputFile.bufferedWriter().use { writer ->
            writer.write("""{"type":"FeatureCollection","features":[""")
            
            val features = mutableListOf<String>()
            
            // WiFi features
            wifiNetworks.filter { it.latitude != 0.0 && it.longitude != 0.0 }.forEach { network ->
                features.add("""
                    {"type":"Feature","geometry":{"type":"Point","coordinates":[${network.longitude},${network.latitude}]},
                    "properties":{"type":"WiFi","ssid":"${escapeJson(network.ssid)}","bssid":"${network.bssid}",
                    "signal":${network.signalLevel},"frequency":${network.frequency}}}
                """.trimIndent())
            }
            
            // Bluetooth features
            btDevices.filter { it.latitude != 0.0 && it.longitude != 0.0 }.forEach { device ->
                features.add("""
                    {"type":"Feature","geometry":{"type":"Point","coordinates":[${device.longitude},${device.latitude}]},
                    "properties":{"type":"Bluetooth","name":"${escapeJson(device.name ?: "")}","address":"${device.address}",
                    "rssi":${device.rssi}}}
                """.trimIndent())
            }
            
            // Cellular features
            cellTowers.filter { it.latitude != 0.0 && it.longitude != 0.0 }.forEach { tower ->
                features.add("""
                    {"type":"Feature","geometry":{"type":"Point","coordinates":[${tower.longitude},${tower.latitude}]},
                    "properties":{"type":"Cellular","operator":"${escapeJson(tower.operatorName)}","cellId":${tower.cellId},
                    "signal":${tower.signalStrength}}}
                """.trimIndent())
            }
            
            writer.write(features.joinToString(","))
            writer.write("]}")
        }
    }
    
    private fun escapeXml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
    
    private fun escapeJson(text: String): String {
        return text.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}
