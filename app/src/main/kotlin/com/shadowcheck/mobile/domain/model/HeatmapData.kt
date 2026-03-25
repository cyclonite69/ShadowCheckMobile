package com.shadowcheck.mobile.domain.model

import com.shadowcheck.mobile.core.model.BluetoothDevice
import com.shadowcheck.mobile.core.model.CellularTower
import com.shadowcheck.mobile.core.model.WifiNetwork

data class HeatmapPoint(
    val latitude: Double,
    val longitude: Double,
    val intensity: Float, // 0.0 to 1.0
    val signalStrength: Int,
    val networkCount: Int
)

data class HeatmapLayer(
    val type: RadioType,
    val points: List<HeatmapPoint>,
    val minIntensity: Float,
    val maxIntensity: Float
)

object HeatmapGenerator {
    
    fun generateWiFiHeatmap(
        networks: List<WifiNetwork>,
        gridSize: Double = 0.0001 // ~11 meters
    ): HeatmapLayer {
        val points = networks
            .filter { it.latitude != 0.0 && it.longitude != 0.0 }
            .groupBy { 
                Pair(
                    (it.latitude / gridSize).toInt(),
                    (it.longitude / gridSize).toInt()
                )
            }
            .map { (grid, nets) ->
                val avgLat = nets.map { it.latitude }.average()
                val avgLon = nets.map { it.longitude }.average()
                val avgSignal = nets.map { it.signalLevel }.average().toInt()
                val count = nets.size
                
                HeatmapPoint(
                    latitude = avgLat,
                    longitude = avgLon,
                    intensity = (count / 10f).coerceIn(0f, 1f),
                    signalStrength = avgSignal,
                    networkCount = count
                )
            }
        
        val maxIntensity = points.maxOfOrNull { it.intensity } ?: 1f
        val minIntensity = points.minOfOrNull { it.intensity } ?: 0f
        
        return HeatmapLayer(
            type = RadioType.WIFI,
            points = points,
            minIntensity = minIntensity,
            maxIntensity = maxIntensity
        )
    }
    
    fun generateBluetoothHeatmap(
        devices: List<BluetoothDevice>,
        gridSize: Double = 0.0001
    ): HeatmapLayer {
        val points = devices
            .filter { it.latitude != 0.0 && it.longitude != 0.0 }
            .groupBy { 
                Pair(
                    (it.latitude / gridSize).toInt(),
                    (it.longitude / gridSize).toInt()
                )
            }
            .map { (grid, devs) ->
                val avgLat = devs.map { it.latitude }.average()
                val avgLon = devs.map { it.longitude }.average()
                val avgSignal = devs.map { it.rssi }.average().toInt()
                val count = devs.size
                
                HeatmapPoint(
                    latitude = avgLat,
                    longitude = avgLon,
                    intensity = (count / 5f).coerceIn(0f, 1f),
                    signalStrength = avgSignal,
                    networkCount = count
                )
            }
        
        val maxIntensity = points.maxOfOrNull { it.intensity } ?: 1f
        val minIntensity = points.minOfOrNull { it.intensity } ?: 0f
        
        return HeatmapLayer(
            type = RadioType.BLUETOOTH,
            points = points,
            minIntensity = minIntensity,
            maxIntensity = maxIntensity
        )
    }
    
    fun generateCellularHeatmap(
        towers: List<CellularTower>,
        gridSize: Double = 0.001 // ~111 meters
    ): HeatmapLayer {
        val points = towers
            .filter { it.latitude != 0.0 && it.longitude != 0.0 }
            .groupBy { 
                Pair(
                    (it.latitude / gridSize).toInt(),
                    (it.longitude / gridSize).toInt()
                )
            }
            .map { (grid, tows) ->
                val avgLat = tows.map { it.latitude }.average()
                val avgLon = tows.map { it.longitude }.average()
                val avgSignal = tows.map { it.signalStrength }.average().toInt()
                val count = tows.size
                
                HeatmapPoint(
                    latitude = avgLat,
                    longitude = avgLon,
                    intensity = (count / 3f).coerceIn(0f, 1f),
                    signalStrength = avgSignal,
                    networkCount = count
                )
            }
        
        val maxIntensity = points.maxOfOrNull { it.intensity } ?: 1f
        val minIntensity = points.minOfOrNull { it.intensity } ?: 0f
        
        return HeatmapLayer(
            type = RadioType.CELLULAR,
            points = points,
            minIntensity = minIntensity,
            maxIntensity = maxIntensity
        )
    }
}

object HeatmapData {
    data class HeatmapPoint(
        val x: Int,
        val y: Int,
        val intensity: Double
    )

    fun generateWiFiHeatmap(networks: List<com.shadowcheck.mobile.wifi.model.WifiNetwork>): List<HeatmapPoint> =
        buildPoints(networks.map { it.signalLevel })

    fun generateBluetoothHeatmap(devices: List<BluetoothDevice>): List<HeatmapPoint> =
        buildPoints(devices.map { it.rssi })

    fun generateCellularHeatmap(towers: List<CellularTower>): List<HeatmapPoint> =
        buildPoints(towers.map { it.signalStrength })

    private fun buildPoints(signals: List<Int>): List<HeatmapPoint> {
        if (signals.isEmpty()) return emptyList()

        return signals.take(64).mapIndexed { index, signal ->
            val column = index % 8
            val row = index / 8
            val intensity = ((signal + 120).coerceIn(0, 120) / 120.0).coerceIn(0.15, 1.0)

            HeatmapPoint(
                x = 80 + (column * 100),
                y = 80 + (row * 100),
                intensity = intensity
            )
        }
    }
}
