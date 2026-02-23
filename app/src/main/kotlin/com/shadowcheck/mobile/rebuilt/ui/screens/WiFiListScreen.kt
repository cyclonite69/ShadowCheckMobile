package com.shadowcheck.mobile.rebuilt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.shadowcheck.mobile.presentation.viewmodel.WifiListViewModel
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WiFiListScreen(
    viewModel: WifiListViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onNetworkClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val networks = uiState.networks
    val distinctCount = uiState.distinctCount
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("WiFi Networks ($distinctCount distinct)", color = ShadowCheckColors.Primary, fontSize = 18.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.TextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadowCheckColors.Surface)
        )
        
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            items(networks) { network ->
                WiFiCard(network, sightingsCounts[network.bssid] ?: 1) { onNetworkClick(network.bssid) }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun WiFiCard(network: WifiNetwork, sightings: Int, onClick: () -> Unit = {}) {
    val signalColor = when {
        network.signalLevel > -60 -> Color(0xFF4CAF50)
        network.signalLevel > -70 -> Color(0xFFFFC107)
        network.signalLevel > -80 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface.copy(alpha = 0.7f)),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (network.ssid.isBlank()) "Hidden Network" else network.ssid,
                    color = ShadowCheckColors.Primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "BSSID: ${network.bssid}",
                    color = ShadowCheckColors.TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "${network.frequency} MHz • $sightings sightings",
                    color = ShadowCheckColors.TextSecondary,
                    fontSize = 12.sp
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = network.signalLevel.toString(),
                    color = signalColor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "dBm",
                    color = ShadowCheckColors.TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}
