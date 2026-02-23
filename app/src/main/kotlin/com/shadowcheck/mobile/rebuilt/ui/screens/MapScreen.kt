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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowcheck.mobile.presentation.viewmodel.MapViewModel
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("Network Map", color = ShadowCheckColors.Primary, fontSize = 18.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.TextPrimary)
                }
            },
            actions = {
                IconButton(onClick = { viewModel.toggleWifi() }) {
                    Icon(Icons.Default.Wifi, "WiFi", tint = if (uiState.showWifi) ShadowCheckColors.Accent else Color.Gray)
                }
                IconButton(onClick = { viewModel.toggleBluetooth() }) {
                    Icon(Icons.Default.Bluetooth, "BT", tint = if (uiState.showBluetooth) ShadowCheckColors.Accent else Color.Gray)
                }
                IconButton(onClick = { viewModel.toggleCellular() }) {
                    Icon(Icons.Default.CellTower, "Cell", tint = if (uiState.showCellular) ShadowCheckColors.Accent else Color.Gray)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadowCheckColors.Surface)
        )
        
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Map View", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Text("WiFi: ${uiState.wifiNetworks.size}", color = ShadowCheckColors.TextSecondary)
                Text("Bluetooth: ${uiState.bluetoothDevices.size}", color = ShadowCheckColors.TextSecondary)
                Text("Cellular: ${uiState.cellularTowers.size}", color = ShadowCheckColors.TextSecondary)
            }
        }
    }
}
