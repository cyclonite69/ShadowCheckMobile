package com.shadowcheck.mobile.ui.screens.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shadowcheck.mobile.data.WifiNetwork
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NetworkDetailsScreen(
    network: WifiNetwork,
    onBack: () -> Unit,
    onShowOnMap: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = network.ssid.ifEmpty { "Hidden Network" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = network.bssid,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ShadowCheckColors.TextSecondary
                    )
                }
                IconButton(onClick = onShowOnMap) {
                    Icon(Icons.Default.Map, "Show on map")
                }
            }
        }
        
        // Details
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Signal info
            DetailCard(title = "Signal Information") {
                DetailRow("Signal Strength", "${network.signalLevel} dBm")
                DetailRow("Frequency", "${network.frequency} MHz")
                DetailRow("Channel", network.channel.toString())
                DetailRow("Channel Width", "${network.channelWidth} MHz")
                if (network.centerFreq0 > 0) {
                    DetailRow("Center Frequency", "${network.centerFreq0} MHz")
                }
            }
            
            // Security
            DetailCard(title = "Security") {
                DetailRow("Capabilities", network.capabilities)
                DetailRow("802.11mc", if (network.is80211mc) "Yes" else "No")
                DetailRow("Passpoint", if (network.isPasspoint) "Yes" else "No")
            }
            
            // Network info
            DetailCard(title = "Network Information") {
                DetailRow("Standard", network.standard.ifEmpty { "Unknown" })
                DetailRow("Max Data Rate", "${network.maxDataRate} Mbps")
                if (network.operatorFriendlyName.isNotEmpty()) {
                    DetailRow("Operator", network.operatorFriendlyName)
                }
                if (network.venueName.isNotEmpty()) {
                    DetailRow("Venue", network.venueName)
                }
            }
            
            // Vendor info
            if (network.vendorName.isNotEmpty() || network.vendorOui.isNotEmpty()) {
                DetailCard(title = "Vendor Information") {
                    if (network.vendorName.isNotEmpty()) {
                        DetailRow("Manufacturer", network.vendorName)
                    }
                    if (network.vendorOui.isNotEmpty()) {
                        DetailRow("OUI", network.vendorOui)
                    }
                }
            }
            
            // Location
            if (network.latitude != 0.0 || network.longitude != 0.0) {
                DetailCard(title = "Location") {
                    DetailRow("Latitude", String.format("%.6f", network.latitude))
                    DetailRow("Longitude", String.format("%.6f", network.longitude))
                    if (network.altitude != 0.0) {
                        DetailRow("Altitude", String.format("%.1f m", network.altitude))
                    }
                    if (network.accuracy > 0) {
                        DetailRow("Accuracy", String.format("%.1f m", network.accuracy))
                    }
                }
            }
            
            // Timestamps
            DetailCard(title = "Timestamps") {
                DetailRow("Last Seen", formatTimestamp(network.timestamp))
                if (network.firstSeen > 0) {
                    DetailRow("First Seen", formatTimestamp(network.firstSeen))
                }
                DetailRow("Source", network.source)
            }
        }
    }
}

@Composable
fun DetailCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ShadowCheckColors.Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ShadowCheckColors.Primary
            )
            Divider()
            content()
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = ShadowCheckColors.TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
