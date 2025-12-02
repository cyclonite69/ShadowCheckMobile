package com.shadowcheck.mobile.rebuilt.ui.screens

import android.Manifest
import kotlinx.coroutines.delay
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.shadowcheck.mobile.ui.components.rainbowShimmer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import com.shadowcheck.mobile.rebuilt.service.ScannerService

@Composable
fun HomeScreen(onNavigate: (String) -> Unit = {}) {
    val context = LocalContext.current
    var hasPermissions by remember { mutableStateOf(false) }
    var wifiUnique by remember { mutableStateOf(0) }
    var wifiTotal by remember { mutableStateOf(0) }
    var btUnique by remember { mutableStateOf(0) }
    var btTotal by remember { mutableStateOf(0) }
    var cellUnique by remember { mutableStateOf(0) }
    var cellTotal by remember { mutableStateOf(0) }
    var isActuallyScanning by remember { mutableStateOf(false) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions.values.all { it }
        if (hasPermissions) startScanner(context)
    }
    
    LaunchedEffect(Unit) {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
        hasPermissions = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (hasPermissions) {
            startScanner(context)
            delay(1000)
            while (true) {
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
                isActuallyScanning = wifiManager.isWifiEnabled
                
                // Get counts from database
                val db = androidx.room.Room.databaseBuilder(context, com.shadowcheck.mobile.data.ShadowCheckDatabase::class.java, "shadowcheck.db").build()
                wifiUnique = db.wifiNetworkDao().getUniqueCount()
                wifiTotal = db.wifiNetworkDao().getTotalCount()
                btUnique = db.bleDeviceDao().getUniqueCount()
                btTotal = db.bleDeviceDao().getTotalCount()
                cellUnique = db.cellularTowerDao().getUniqueCount()
                cellTotal = db.cellularTowerDao().getTotalCount()
                
                delay(3000)
            }
        } else {
            permissionLauncher.launch(permissions)
        }
    }
    
    Box(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("GPS: 3m", color = ShadowCheckColors.Accent, fontSize = 14.sp)
            Text("Sats: 39", color = ShadowCheckColors.Accent, fontSize = 14.sp)
        }
        
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Scanning Card
            Card(
                modifier = Modifier.fillMaxWidth(0.9f).rainbowShimmer(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A3A).copy(alpha = 0.6f)),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isActuallyScanning) "SCANNING ACTIVE" else "SCANNING STOPPED",
                        color = if (isActuallyScanning) ShadowCheckColors.Accent else ShadowCheckColors.Error,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isActuallyScanning) "Collecting network data..." else "Scanner not active",
                        color = ShadowCheckColors.TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(Icons.Default.Wifi, wifiUnique, wifiTotal, "WiFi") { onNavigate("wifi_list") }
                StatCard(Icons.Default.Bluetooth, btUnique, btTotal, "Bluetooth") { onNavigate("bluetooth_list") }
                StatCard(Icons.Default.CellTower, cellUnique, cellTotal, "Cellular") { onNavigate("cellular_list") }
            }
        }
        
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.GpsFixed, "GPS", tint = ShadowCheckColors.Accent, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("GPS: 3m", color = ShadowCheckColors.Accent, fontSize = 14.sp)
        }
    }
}

@Composable
fun StatCard(icon: androidx.compose.ui.graphics.vector.ImageVector, unique: Int, total: Int, label: String, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.width(100.dp).height(140.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface.copy(alpha = 0.7f)),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, label, tint = ShadowCheckColors.Primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(unique.toString(), color = ShadowCheckColors.Primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("$total total", color = ShadowCheckColors.TextSecondary, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, color = ShadowCheckColors.TextPrimary, fontSize = 11.sp)
        }
    }
}

private fun startScanner(context: Context) {
    try {
        context.startForegroundService(Intent(context, com.shadowcheck.mobile.rebuilt.service.CompleteScannerService::class.java).apply {
            action = "START"
        })
    } catch (e: Exception) {}
}
