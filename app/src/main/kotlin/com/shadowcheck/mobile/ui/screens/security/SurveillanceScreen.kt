package com.shadowcheck.mobile.ui.screens.security

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shadowcheck.mobile.domain.model.ThreatDetection
import com.shadowcheck.mobile.domain.model.ThreatSeverity
import com.shadowcheck.mobile.domain.model.ThreatType
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@Composable
fun SurveillanceScreen(
    threats: List<ThreatDetection>,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = ShadowCheckColors.Surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Threat Detection",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${threats.size} threats detected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (threats.isEmpty()) ShadowCheckColors.SignalGood 
                               else ShadowCheckColors.Error
                    )
                }
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, "Refresh")
                }
            }
        }
        
        if (threats.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Security,
                        null,
                        modifier = Modifier.size(64.dp),
                        tint = ShadowCheckColors.SignalGood
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "No Threats Detected",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ShadowCheckColors.SignalGood
                    )
                    Text(
                        text = "Your environment appears safe",
                        color = ShadowCheckColors.TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(threats) { threat ->
                    ThreatCard(threat)
                }
            }
        }
    }
}

@Composable
fun ThreatCard(threat: ThreatDetection) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (threat.severity) {
                ThreatSeverity.CRITICAL -> ShadowCheckColors.Error.copy(alpha = 0.1f)
                ThreatSeverity.HIGH -> ShadowCheckColors.SignalMedium.copy(alpha = 0.1f)
                ThreatSeverity.MEDIUM -> ShadowCheckColors.Primary.copy(alpha = 0.1f)
                ThreatSeverity.LOW -> ShadowCheckColors.Surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        getThreatIcon(threat.type),
                        null,
                        tint = getSeverityColor(threat.severity)
                    )
                    Column {
                        Text(
                            text = threat.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = threat.type.name.replace("_", " "),
                            style = MaterialTheme.typography.bodySmall,
                            color = ShadowCheckColors.TextSecondary
                        )
                    }
                }
                
                Surface(
                    color = getSeverityColor(threat.severity),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = threat.severity.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = threat.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (threat.affectedDevices.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Affected: ${threat.affectedDevices.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ShadowCheckColors.TextSecondary
                )
            }
            
            Spacer(Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Confidence: ${(threat.confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = ShadowCheckColors.TextSecondary
                )
                Text(
                    text = formatTime(threat.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = ShadowCheckColors.TextSecondary
                )
            }
        }
    }
}

private fun getThreatIcon(type: ThreatType) = when (type) {
    ThreatType.IMSI_CATCHER -> Icons.Default.PhoneAndroid
    ThreatType.ROGUE_AP, ThreatType.EVIL_TWIN -> Icons.Default.Wifi
    ThreatType.SURVEILLANCE_DEVICE -> Icons.Default.Videocam
    ThreatType.BLUETOOTH_SNIFFER -> Icons.Default.Bluetooth
    ThreatType.GPS_JAMMER -> Icons.Default.GpsOff
    ThreatType.HIDDEN_CAMERA -> Icons.Default.Camera
    ThreatType.DEAUTH_ATTACK -> Icons.Default.WifiOff
    else -> Icons.Default.Warning
}

private fun getSeverityColor(severity: ThreatSeverity) = when (severity) {
    ThreatSeverity.CRITICAL -> ShadowCheckColors.Error
    ThreatSeverity.HIGH -> ShadowCheckColors.SignalMedium
    ThreatSeverity.MEDIUM -> ShadowCheckColors.Primary
    ThreatSeverity.LOW -> ShadowCheckColors.TextSecondary
}

private fun formatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        else -> "${diff / 86400000}d ago"
    }
}
