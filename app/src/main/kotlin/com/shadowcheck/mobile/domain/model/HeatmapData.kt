package com.shadowcheck.mobile.domain.model

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
        networks: List<com.shadowcheck.mobile.data.WifiNetwork>,
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
        devices: List<com.shadowcheck.mobile.data.BluetoothDevice>,
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
        towers: List<com.shadowcheck.mobile.data.CellularTower>,
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
