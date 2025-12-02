package com.shadowcheck.mobile.ui.screens.maps

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import com.shadowcheck.mobile.domain.model.HeatmapLayer
import com.shadowcheck.mobile.domain.model.RadioType
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@Composable
fun HeatmapScreen(
    wifiHeatmap: HeatmapLayer?,
    bluetoothHeatmap: HeatmapLayer?,
    cellularHeatmap: HeatmapLayer?,
    currentLat: Double,
    currentLon: Double,
    onBack: () -> Unit
) {
    var selectedLayer by remember { mutableStateOf(RadioType.WIFI) }
    var showLegend by remember { mutableStateOf(true) }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(currentLat, currentLon), 14f)
    }
    
    val currentHeatmap = when (selectedLayer) {
        RadioType.WIFI -> wifiHeatmap
        RadioType.BLUETOOTH -> bluetoothHeatmap
        RadioType.CELLULAR -> cellularHeatmap
        else -> null
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = true
            )
        ) {
            // Render heatmap overlay
            currentHeatmap?.let { heatmap ->
                val weightedPoints = heatmap.points.map { point ->
                    WeightedLatLng(
                        LatLng(point.latitude, point.longitude),
                        point.intensity.toDouble()
                    )
                }
                
                if (weightedPoints.isNotEmpty()) {
                    val gradient = when (selectedLayer) {
                        RadioType.WIFI -> createWiFiGradient()
                        RadioType.BLUETOOTH -> createBluetoothGradient()
                        RadioType.CELLULAR -> createCellularGradient()
                        else -> createWiFiGradient()
                    }
                    
                    // Note: Actual heatmap rendering requires TileOverlay
                    // This is a simplified version showing markers
                    heatmap.points.forEach { point ->
                        Circle(
                            center = LatLng(point.latitude, point.longitude),
                            radius = 50.0,
                            fillColor = getHeatmapColor(point.intensity, selectedLayer),
                            strokeColor = Color.Transparent
                        )
                    }
                }
            }
        }
        
        // Header
        Surface(
            modifier = Modifier.align(Alignment.TopCenter),
            color = ShadowCheckColors.Surface.copy(alpha = 0.9f),
            tonalElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                        Column {
                            Text(
                                text = "Network Heatmap",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            currentHeatmap?.let {
                                Text(
                                    text = "${it.points.size} data points",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ShadowCheckColors.TextSecondary
                                )
                            }
                        }
                    }
                    
                    IconButton(onClick = { showLegend = !showLegend }) {
                        Icon(Icons.Default.Info, "Legend")
                    }
                }
                
                // Layer selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedLayer == RadioType.WIFI,
                        onClick = { selectedLayer = RadioType.WIFI },
                        label = { Text("WiFi") },
                        leadingIcon = { Icon(Icons.Default.Wifi, null) }
                    )
                    FilterChip(
                        selected = selectedLayer == RadioType.BLUETOOTH,
                        onClick = { selectedLayer = RadioType.BLUETOOTH },
                        label = { Text("Bluetooth") },
                        leadingIcon = { Icon(Icons.Default.Bluetooth, null) }
                    )
                    FilterChip(
                        selected = selectedLayer == RadioType.CELLULAR,
                        onClick = { selectedLayer = RadioType.CELLULAR },
                        label = { Text("Cellular") },
                        leadingIcon = { Icon(Icons.Default.CellTower, null) }
                    )
                }
            }
        }
        
        // Legend
        if (showLegend) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ShadowCheckColors.Surface.copy(alpha = 0.9f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Intensity",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    HeatmapLegendItem("High", getHeatmapColor(1f, selectedLayer))
                    HeatmapLegendItem("Medium", getHeatmapColor(0.5f, selectedLayer))
                    HeatmapLegendItem("Low", getHeatmapColor(0.2f, selectedLayer))
                }
            }
        }
    }
}

@Composable
fun HeatmapLegendItem(label: String, color: Color) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(16.dp),
            color = color,
            shape = MaterialTheme.shapes.small
        ) {}
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun getHeatmapColor(intensity: Float, type: RadioType): Color {
    return when (type) {
        RadioType.WIFI -> {
            when {
                intensity > 0.7f -> Color(0xFFFF0000).copy(alpha = 0.6f)
                intensity > 0.4f -> Color(0xFFFFFF00).copy(alpha = 0.6f)
                else -> Color(0xFF00FF00).copy(alpha = 0.6f)
            }
        }
        RadioType.BLUETOOTH -> {
            when {
                intensity > 0.7f -> Color(0xFF0000FF).copy(alpha = 0.6f)
                intensity > 0.4f -> Color(0xFF00FFFF).copy(alpha = 0.6f)
                else -> Color(0xFF00FF00).copy(alpha = 0.6f)
            }
        }
        RadioType.CELLULAR -> {
            when {
                intensity > 0.7f -> Color(0xFFFF00FF).copy(alpha = 0.6f)
                intensity > 0.4f -> Color(0xFFFF8800).copy(alpha = 0.6f)
                else -> Color(0xFF00FF00).copy(alpha = 0.6f)
            }
        }
        else -> Color.Gray.copy(alpha = 0.6f)
    }
}

private fun createWiFiGradient(): Gradient {
    return Gradient(
        intArrayOf(
            android.graphics.Color.rgb(0, 255, 0),
            android.graphics.Color.rgb(255, 255, 0),
            android.graphics.Color.rgb(255, 0, 0)
        ),
        floatArrayOf(0.2f, 0.5f, 1.0f)
    )
}

private fun createBluetoothGradient(): Gradient {
    return Gradient(
        intArrayOf(
            android.graphics.Color.rgb(0, 255, 0),
            android.graphics.Color.rgb(0, 255, 255),
            android.graphics.Color.rgb(0, 0, 255)
        ),
        floatArrayOf(0.2f, 0.5f, 1.0f)
    )
}

private fun createCellularGradient(): Gradient {
    return Gradient(
        intArrayOf(
            android.graphics.Color.rgb(0, 255, 0),
            android.graphics.Color.rgb(255, 136, 0),
            android.graphics.Color.rgb(255, 0, 255)
        ),
        floatArrayOf(0.2f, 0.5f, 1.0f)
    )
}
