package com.shadowcheck.mobile.rebuilt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.shadowcheck.mobile.data.ShadowCheckDatabase
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import kotlinx.coroutines.delay

data class NetworkMarker(
    val lat: Double,
    val lon: Double,
    val type: String, // "WiFi", "Bluetooth", "Cellular"
    val name: String,
    val signalStrength: Int,
    val id: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    var markers by remember { mutableStateOf<List<NetworkMarker>>(emptyList()) }
    var selectedType by remember { mutableStateOf("All") }
    var centerLat by remember { mutableStateOf(0.0) }
    var centerLon by remember { mutableStateOf(0.0) }
    
    LaunchedEffect(selectedType) {
        val db = Room.databaseBuilder(context, ShadowCheckDatabase::class.java, "shadowcheck.db").build()
        while (true) {
            val allMarkers = mutableListOf<NetworkMarker>()
            
            if (selectedType == "All" || selectedType == "WiFi") {
                db.wifiNetworkDao().getDistinctFlow().collect { networks ->
                    networks.filter { it.latitude != 0.0 && it.longitude != 0.0 }.forEach { net ->
                        allMarkers.add(NetworkMarker(
                            lat = net.latitude,
                            lon = net.longitude,
                            type = "WiFi",
                            name = net.ssid.ifBlank { "Hidden" },
                            signalStrength = net.signalLevel,
                            id = net.bssid
                        ))
                    }
                }
            }
            
            if (selectedType == "All" || selectedType == "Bluetooth") {
                db.bleDeviceDao().getDistinctFlow().collect { devices ->
                    devices.filter { it.latitude != 0.0 && it.longitude != 0.0 }.forEach { dev ->
                        allMarkers.add(NetworkMarker(
                            lat = dev.latitude,
                            lon = dev.longitude,
                            type = "Bluetooth",
                            name = dev.name ?: "Unknown",
                            signalStrength = dev.rssi,
                            id = dev.address
                        ))
                    }
                }
            }
            
            if (selectedType == "All" || selectedType == "Cellular") {
                db.cellularTowerDao().getDistinctFlow().collect { towers ->
                    towers.filter { it.latitude != 0.0 && it.longitude != 0.0 }.forEach { tower ->
                        allMarkers.add(NetworkMarker(
                            lat = tower.latitude,
                            lon = tower.longitude,
                            type = "Cellular",
                            name = "Cell ${tower.cellId}",
                            signalStrength = tower.signalStrength * -20,
                            id = tower.cellId.toString()
                        ))
                    }
                }
            }
            
            markers = allMarkers
            if (markers.isNotEmpty() && centerLat == 0.0) {
                centerLat = markers.map { it.lat }.average()
                centerLon = markers.map { it.lon }.average()
            }
            
            delay(5000)
        }
    }
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("Network Map", color = ShadowCheckColors.Primary, fontSize = 18.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.TextPrimary)
                }
            },
            actions = {
                IconButton(onClick = { /* Center on current location */ }) {
                    Icon(Icons.Default.MyLocation, "Center", tint = ShadowCheckColors.Primary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadowCheckColors.Surface)
        )
        
        // Filter chips
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(selected = selectedType == "All", onClick = { selectedType = "All" }, label = { Text("All (${markers.size})") })
            FilterChip(selected = selectedType == "WiFi", onClick = { selectedType = "WiFi" }, label = { Text("WiFi") })
            FilterChip(selected = selectedType == "Bluetooth", onClick = { selectedType = "Bluetooth" }, label = { Text("BT") })
            FilterChip(selected = selectedType == "Cellular", onClick = { selectedType = "Cellular" }, label = { Text("Cell") })
        }
        
        // Map placeholder (will integrate actual map library)
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF1A2332)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Map, "Map", tint = ShadowCheckColors.Primary, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Map View", color = ShadowCheckColors.Primary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("${markers.size} networks", color = ShadowCheckColors.TextSecondary, fontSize = 14.sp)
                Text("Center: ${String.format("%.6f", centerLat)}, ${String.format("%.6f", centerLon)}", 
                    color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Legend
                Card(colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Legend", fontWeight = FontWeight.Bold, color = ShadowCheckColors.Primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        LegendItem(Icons.Default.Wifi, "WiFi Network", Color(0xFF00BCD4))
                        LegendItem(Icons.Default.Bluetooth, "Bluetooth Device", Color(0xFF2196F3))
                        LegendItem(Icons.Default.CellTower, "Cell Tower", Color(0xFF9C27B0))
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Map integration: Mapbox/Google Maps", color = ShadowCheckColors.TextSecondary, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun LegendItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, label, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = ShadowCheckColors.TextPrimary, fontSize = 12.sp)
    }
}
