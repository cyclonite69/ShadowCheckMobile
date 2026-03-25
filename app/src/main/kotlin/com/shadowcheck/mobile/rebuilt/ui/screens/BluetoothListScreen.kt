package com.shadowcheck.mobile.rebuilt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowcheck.mobile.presentation.viewmodel.BluetoothListViewModel
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import com.shadowcheck.mobile.core.model.BluetoothDevice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothListScreen(
    viewModel: BluetoothListViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("Bluetooth Devices (${uiState.distinctCount} distinct)", color = ShadowCheckColors.Primary, fontSize = 18.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.TextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadowCheckColors.Surface)
        )
        
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(uiState.devices) { device ->
                BluetoothCard(device, uiState.sightingsCounts[device.macAddress] ?: 0)
            }
        }
    }
}

@Composable
fun BluetoothCard(device: BluetoothDevice, sightings: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(device.name.ifBlank { "Unknown Device" }, fontWeight = FontWeight.Bold, color = Color.White)
            Text(device.macAddress, fontSize = 12.sp, color = ShadowCheckColors.TextSecondary)
            Text("$sightings sightings", fontSize = 10.sp, color = ShadowCheckColors.Accent)
        }
    }
}
