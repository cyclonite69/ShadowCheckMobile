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
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import com.shadowcheck.mobile.service.WiFiConnectionEvent
import com.shadowcheck.mobile.service.WiFiDisconnectionEvent
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WiFiEventsScreen(
    connectionEvents: List<WiFiConnectionEvent>,
    disconnectionEvents: List<WiFiDisconnectionEvent>,
    onBack: () -> Unit
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
                Column {
                    Text(
                        text = "WiFi Events Monitor",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time connection tracking",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ShadowCheckColors.TextSecondary
                    )
                }
            }
        }
        
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Disconnection events (show first - more important)
            items(disconnectionEvents.sortedByDescending { it.timestamp }) { event ->
                DisconnectionEventCard(event)
            }
            
            // Connection events
            items(connectionEvents.sortedByDescending { it.timestamp }) { event ->
                ConnectionEventCard(event)
            }
        }
    }
}

@Composable
fun DisconnectionEventCard(event: WiFiDisconnectionEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (event.reason) {
                6, 7 -> ShadowCheckColors.Error.copy(alpha = 0.1f) // Deauth
                else -> ShadowCheckColors.SignalMedium.copy(alpha = 0.1f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.WifiOff,
                null,
                tint = when (event.reason) {
                    6, 7 -> ShadowCheckColors.Error
                    else -> ShadowCheckColors.SignalMedium
                },
                modifier = Modifier.size(32.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "WiFi Disconnected",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = event.reasonText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (event.reason) {
                        6, 7 -> ShadowCheckColors.Error
                        else -> ShadowCheckColors.SignalMedium
                    }
                )
                
                if (event.reason == 6 || event.reason == 7) {
                    Text(
                        text = "⚠️ Possible deauth attack detected!",
                        style = MaterialTheme.typography.bodySmall,
                        color = ShadowCheckColors.Error,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = "Reason code: ${event.reason}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ShadowCheckColors.TextSecondary
                )
                
                Text(
                    text = formatTimestamp(event.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = ShadowCheckColors.TextSecondary
                )
            }
        }
    }
}

@Composable
fun ConnectionEventCard(event: WiFiConnectionEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ShadowCheckColors.Surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Wifi,
                null,
                tint = ShadowCheckColors.SignalGood,
                modifier = Modifier.size(32.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.ssid.ifEmpty { "Hidden Network" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = event.bssid,
                    style = MaterialTheme.typography.bodySmall,
                    color = ShadowCheckColors.TextSecondary
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Column {
                        Text(
                            text = "RSSI",
                            style = MaterialTheme.typography.labelSmall,
                            color = ShadowCheckColors.TextSecondary
                        )
                        Text(
                            text = "${event.rssi} dBm",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Frequency",
                            style = MaterialTheme.typography.labelSmall,
                            color = ShadowCheckColors.TextSecondary
                        )
                        Text(
                            text = "${event.frequency} MHz",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Speed",
                            style = MaterialTheme.typography.labelSmall,
                            color = ShadowCheckColors.TextSecondary
                        )
                        Text(
                            text = "${event.linkSpeed} Mbps",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Text(
                    text = formatTimestamp(event.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = ShadowCheckColors.TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
