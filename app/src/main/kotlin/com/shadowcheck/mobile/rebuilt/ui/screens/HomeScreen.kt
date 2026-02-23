package com.shadowcheck.mobile.rebuilt.ui.screens

import android.Manifest
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowcheck.mobile.presentation.viewmodel.HomeViewModel
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import com.shadowcheck.mobile.ui.components.rainbowShimmer

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var hasPermissions by remember { mutableStateOf(false) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions.values.all { it }
        if (hasPermissions) startScanner(context, viewModel)
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
            startScanner(context, viewModel)
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
                        text = if (uiState.isScanning) "SCANNING ACTIVE" else "SCANNING STOPPED",
                        color = if (uiState.isScanning) ShadowCheckColors.Accent else ShadowCheckColors.Error,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (uiState.isScanning) "Collecting network data..." else "Scanner not active",
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
                StatCard(Icons.Default.Wifi, uiState.wifiUnique, uiState.wifiTotal, "WiFi") { onNavigate("wifi_list") }
                StatCard(Icons.Default.Bluetooth, uiState.btUnique, uiState.btTotal, "Bluetooth") { onNavigate("bluetooth_list") }
                StatCard(Icons.Default.CellTower, uiState.cellUnique, uiState.cellTotal, "Cellular") { onNavigate("cellular_list") }
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

private fun startScanner(context: Context, viewModel: HomeViewModel) {
    try {
        context.startForegroundService(Intent(context, com.shadowcheck.mobile.rebuilt.service.CompleteScannerService::class.java).apply {
            action = "START"
        })
        viewModel.setScanning(true)
    } catch (e: Exception) {}
}
