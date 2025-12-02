package com.shadowcheck.mobile

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
import com.shadowcheck.mobile.domain.model.RadioType
import com.shadowcheck.mobile.domain.model.UnifiedSighting
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UnifiedDetailScreen(
    sighting: UnifiedSighting,
    onBack: () -> Unit,
    onShowOnMap: () -> Unit
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sighting.name.ifEmpty { "Unknown" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = sighting.type.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ShadowCheckColors.TextSecondary
                    )
                }
                IconButton(onClick = onShowOnMap) {
                    Icon(Icons.Default.Map, "Show on map")
                }
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Signal info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Signal Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ShadowCheckColors.Primary
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailRow("Signal Level", "${sighting.signalLevel} dBm")
                    sighting.frequency?.let {
                        DetailRow("Frequency", "$it MHz")
                    }
                    sighting.channel?.let {
                        DetailRow("Channel", it.toString())
                    }
                }
            }
            
            // Type-specific info
            when (sighting.type) {
                RadioType.WIFI -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "WiFi Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ShadowCheckColors.Primary
                            )
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            sighting.capabilities?.let {
                                DetailRow("Security", it)
                            }
                            sighting.standard?.let {
                                DetailRow("Standard", it)
                            }
                            sighting.channelWidth?.let {
                                DetailRow("Channel Width", "$it MHz")
                            }
                        }
                    }
                }
                RadioType.CELLULAR -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Cellular Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ShadowCheckColors.Primary
                            )
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            sighting.cellId?.let {
                                DetailRow("Cell ID", it.toString())
                            }
                            sighting.mcc?.let {
                                DetailRow("MCC", it.toString())
                            }
                            sighting.mnc?.let {
                                DetailRow("MNC", it.toString())
                            }
                        }
                    }
                }
                else -> {}
            }
            
            // Location
            if (sighting.latitude != 0.0 || sighting.longitude != 0.0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Location",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ShadowCheckColors.Primary
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        DetailRow("Latitude", String.format("%.6f", sighting.latitude))
                        DetailRow("Longitude", String.format("%.6f", sighting.longitude))
                        if (sighting.altitude != 0.0) {
                            DetailRow("Altitude", String.format("%.1f m", sighting.altitude))
                        }
                    }
                }
            }
            
            // Timestamp
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Timestamp",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ShadowCheckColors.Primary
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailRow("Detected", formatTimestamp(sighting.timestamp))
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
