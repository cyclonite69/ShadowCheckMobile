package com.shadowcheck.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@Composable
fun Sidebar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    var isScanning by remember { mutableStateOf(true) }
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text("ShadowCheck", color = ShadowCheckColors.Primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Network Scanner", color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
        }
        
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
        ) {
            SidebarItem("Home", Icons.Default.Home, "home", currentRoute, onNavigate)
            SidebarItem("WiFi", Icons.Default.Wifi, "wifi_list", currentRoute, onNavigate)
            SidebarItem("Bluetooth", Icons.Default.Bluetooth, "bluetooth_list", currentRoute, onNavigate)
            SidebarItem("Cellular", Icons.Default.CellTower, "cellular_list", currentRoute, onNavigate)
            SidebarItem("Map", Icons.Default.Map, "map", currentRoute, onNavigate)
            SidebarItem("Heatmap", Icons.Default.Thermostat, "heatmap", currentRoute, onNavigate)
            SidebarItem("Playback", Icons.Default.PlayArrow, "playback", currentRoute, onNavigate)
            SidebarItem("Finder", Icons.Default.Search, "finder", currentRoute, onNavigate)
            SidebarItem("Threats", Icons.Default.Shield, "threats", currentRoute, onNavigate)
            SidebarItem("Geofence", Icons.Default.LocationOn, "geofence", currentRoute, onNavigate)
            SidebarItem("Channels", Icons.Default.BarChart, "channels", currentRoute, onNavigate)
            SidebarItem("Statistics", Icons.Default.Analytics, "stats", currentRoute, onNavigate)
            SidebarItem("WiGLE", Icons.Default.Cloud, "wigle", currentRoute, onNavigate)
            SidebarItem("Theme Builder", Icons.Default.Palette, "theme", currentRoute, onNavigate)
            SidebarItem("Settings", Icons.Default.Settings, "settings", currentRoute, onNavigate)
        }
        
        Button(
            onClick = { isScanning = !isScanning },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isScanning) ShadowCheckColors.Error else ShadowCheckColors.Accent
            )
        ) {
            Icon(
                if (isScanning) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(if (isScanning) "Stop Scanning" else "Start Scanning")
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("v1.0", color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
            Text(if (isScanning) "Scanning" else "Stopped", color = if (isScanning) ShadowCheckColors.Accent else ShadowCheckColors.Error, fontSize = 12.sp)
        }
    }
}

@Composable
fun SidebarItem(
    text: String,
    icon: ImageVector,
    route: String,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val isSelected = currentRoute == route
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate(route) }
            .background(if (isSelected) ShadowCheckColors.Primary.copy(alpha = 0.2f) else Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, text, tint = if (isSelected) ShadowCheckColors.Primary else ShadowCheckColors.TextSecondary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = if (isSelected) ShadowCheckColors.Primary else ShadowCheckColors.TextPrimary, fontSize = 16.sp)
    }
}
