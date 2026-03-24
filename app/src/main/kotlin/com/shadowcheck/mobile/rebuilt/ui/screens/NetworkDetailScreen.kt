package com.shadowcheck.mobile.rebuilt.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowcheck.mobile.core.model.WifiNetwork
import com.shadowcheck.mobile.presentation.viewmodel.NetworkDetailViewModel
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkDetailScreen(
    bssid: String,
    viewModel: NetworkDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val network = uiState.network
    val sightings = uiState.sightings
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background).verticalScroll(rememberScrollState())) {
        TopAppBar(
            title = { 
                Column {
                    Text(network?.ssid?.ifBlank { "Hidden Network" } ?: "Network Details", color = ShadowCheckColors.Primary, fontSize = 18.sp)
                    Text(bssid, color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.TextPrimary)
                }
            },
            actions = {
                IconButton(onClick = {}) { Icon(Icons.Default.PlayArrow, "Playback", tint = ShadowCheckColors.Error) }
                IconButton(onClick = {}) { Icon(Icons.Default.Star, "Favorite", tint = ShadowCheckColors.Accent) }
                IconButton(onClick = {}) { Icon(Icons.Default.Map, "Map", tint = ShadowCheckColors.TextPrimary) }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadowCheckColors.Surface)
        )
        
        // Network Summary
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Network Summary", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ShadowCheckColors.Primary)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Sightings", color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                        Text(sightings.size.toString(), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Avg Signal", color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                        Text("${uiState.avgSignal} dBm", color = ShadowCheckColors.Primary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        // Notes & Tags
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Notes & Tags", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ShadowCheckColors.Primary)
                    Text("Tap to add notes or tags", color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                }
                Icon(Icons.Default.Edit, "Edit", tint = ShadowCheckColors.Primary)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Signal Strength Over Time
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Signal Strength Over Time", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ShadowCheckColors.Primary)
                Spacer(modifier = Modifier.height(8.dp))
                SignalGraph(sightings)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Min: ${uiState.minSignal} dBm", color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                        if (sightings.isNotEmpty()) {
                            Text(formatTimestamp(sightings.first().timestamp), color = ShadowCheckColors.TextSecondary, fontSize = 10.sp)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Max: ${uiState.maxSignal} dBm", color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                        if (sightings.isNotEmpty()) {
                            Text(formatTimestamp(sightings.last().timestamp), color = ShadowCheckColors.TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Network Information
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Network Information", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ShadowCheckColors.Primary)
                Spacer(modifier = Modifier.height(8.dp))
                network?.let { net ->
                    InfoRow("Frequency", "${net.frequency} MHz")
                    if (net.channel > 0) {
                        InfoRow("Channel", "${net.channel} (Width: ${net.channelWidth} MHz)")
                    }
                    InfoRow("Security", net.capabilities)
                    InfoRow("First Seen", formatTimestamp(net.firstSeen))
                    InfoRow("Last Seen", formatTimestamp(net.lastSeen))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SignalGraph(sightings: List<WifiNetwork>) {
    if (sightings.isEmpty()) return
    
    Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        val maxSignal = sightings.maxOf { it.signalLevel }.toFloat()
        val minSignal = sightings.minOf { it.signalLevel }.toFloat()
        val range = maxSignal - minSignal
        
        val points = sightings.mapIndexed { index, sighting ->
            val x = (index.toFloat() / (sightings.size - 1)) * size.width
            val normalizedSignal = if (range > 0) (sighting.signalLevel - minSignal) / range else 0.5f
            val y = size.height - (normalizedSignal * size.height)
            Offset(x, y)
        }
        
        for (i in 0 until points.size - 1) {
            drawLine(
                color = Color(0xFF00BCD4),
                start = points[i],
                end = points[i + 1],
                strokeWidth = 3f
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = ShadowCheckColors.TextSecondary, fontSize = 14.sp, modifier = Modifier.weight(0.4f))
        Text(value, color = ShadowCheckColors.TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(0.6f))
    }
}

fun formatTimestamp(timestamp: Long): String {
    return SimpleDateFormat("MMM d, yyyy HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
}
