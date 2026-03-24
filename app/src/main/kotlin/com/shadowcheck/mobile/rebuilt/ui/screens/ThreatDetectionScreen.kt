package com.shadowcheck.mobile.rebuilt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.shadowcheck.mobile.presentation.viewmodel.Threat
import com.shadowcheck.mobile.presentation.viewmodel.ThreatDetectionViewModel
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreatDetectionScreen(
    viewModel: ThreatDetectionViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val threats = uiState.threats
    val isScanning = uiState.isScanning
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("Threat Detection", color = ShadowCheckColors.Primary, fontSize = 18.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.TextPrimary)
                }
            },
            actions = {
                IconButton(onClick = { viewModel.toggleScanning() }) {
                    Icon(
                        if (isScanning) Icons.Default.Stop else Icons.Default.PlayArrow,
                        if (isScanning) "Stop" else "Start",
                        tint = if (isScanning) ShadowCheckColors.Error else ShadowCheckColors.Accent
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadowCheckColors.Surface)
        )
        
        // Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ThreatSummaryCard("Critical", threats.count { it.severity == "Critical" }, Color(0xFFF44336))
            ThreatSummaryCard("High", threats.count { it.severity == "High" }, Color(0xFFFF9800))
            ThreatSummaryCard("Medium", threats.count { it.severity == "Medium" }, Color(0xFFFFC107))
            ThreatSummaryCard("Low", threats.count { it.severity == "Low" }, Color(0xFF4CAF50))
        }
        
        if (threats.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Shield, "No Threats", tint = ShadowCheckColors.Accent, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        if (isScanning) "Scanning for threats..." else "No threats detected",
                        color = ShadowCheckColors.TextSecondary,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                items(threats) { threat ->
                    ThreatCard(threat)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ThreatSummaryCard(label: String, count: Int, color: Color) {
    Card(
        modifier = Modifier.width(80.dp),
        colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count.toString(), color = color, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(label, color = ShadowCheckColors.TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun ThreatCard(threat: Threat) {
    val severityColor = when (threat.severity) {
        "Critical" -> Color(0xFFF44336)
        "High" -> Color(0xFFFF9800)
        "Medium" -> Color(0xFFFFC107)
        else -> Color(0xFF4CAF50)
    }
    
    val icon = when (threat.type) {
        "Rogue AP" -> Icons.Default.Warning
        "Evil Twin" -> Icons.Default.ContentCopy
        "Deauth Attack" -> Icons.Default.Block
        "Unusual Signal" -> Icons.Default.SignalWifiStatusbar4Bar
        "Hidden Network" -> Icons.Default.VisibilityOff
        "Suspicious BT" -> Icons.Default.BluetoothSearching
        else -> Icons.Default.Security
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, threat.type, tint = severityColor, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(threat.title, color = ShadowCheckColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = severityColor,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            threat.severity,
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(threat.description, color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                if (threat.bssid.isNotEmpty()) {
                    Text("BSSID: ${threat.bssid}", color = ShadowCheckColors.TextSecondary, fontSize = 10.sp)
                }
            }
            Icon(Icons.Default.ChevronRight, "Details", tint = ShadowCheckColors.TextSecondary)
        }
    }
}
