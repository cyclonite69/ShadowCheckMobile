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
import com.shadowcheck.mobile.data.CellularTower
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CellularDetailsScreen(
    tower: CellularTower,
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
                        text = tower.operatorName.ifEmpty { "Unknown Operator" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Cell ID: ${tower.cellId}",
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
                DetailRow("Signal Strength", "${tower.signalStrength} dBm")
                DetailRow("Signal Quality", tower.signalQuality.toString())
                DetailRow("Quality Rating", getSignalQuality(tower.signalStrength))
            }
            
            // Cell info
            DetailCard(title = "Cell Information") {
                DetailRow("Cell ID", tower.cellId.toString())
                DetailRow("LAC/TAC", tower.lac.toString())
                if (tower.psc > 0) {
                    DetailRow("PSC/PCI", tower.psc.toString())
                }
                DetailRow("Network Type", tower.networkType)
            }
            
            // Network info
            DetailCard(title = "Network Information") {
                DetailRow("MCC", tower.mcc.toString())
                DetailRow("MNC", tower.mnc.toString())
                DetailRow("Operator", tower.operatorName.ifEmpty { "Unknown" })
                DetailRow("Country", getMccCountry(tower.mcc))
            }
            
            // Location
            if (tower.latitude != 0.0 || tower.longitude != 0.0) {
                DetailCard(title = "Location") {
                    DetailRow("Latitude", String.format("%.6f", tower.latitude))
                    DetailRow("Longitude", String.format("%.6f", tower.longitude))
                    if (tower.altitude != 0.0) {
                        DetailRow("Altitude", String.format("%.1f m", tower.altitude))
                    }
                    if (tower.accuracy > 0) {
                        DetailRow("Accuracy", String.format("%.1f m", tower.accuracy))
                    }
                }
            }
            
            // Timestamps
            DetailCard(title = "Timestamps") {
                DetailRow("Last Seen", formatTimestamp(tower.timestamp))
                if (tower.firstSeen > 0) {
                    DetailRow("First Seen", formatTimestamp(tower.firstSeen))
                }
                DetailRow("Source", tower.source)
            }
        }
    }
}

private fun getSignalQuality(signalStrength: Int): String {
    return when {
        signalStrength >= -70 -> "Excellent"
        signalStrength >= -85 -> "Good"
        signalStrength >= -100 -> "Fair"
        signalStrength >= -110 -> "Weak"
        else -> "Very Weak"
    }
}

private fun getMccCountry(mcc: Int): String {
    return when (mcc) {
        310, 311, 312, 313, 316 -> "United States"
        302 -> "Canada"
        234 -> "United Kingdom"
        262 -> "Germany"
        208 -> "France"
        222 -> "Italy"
        214 -> "Spain"
        404, 405 -> "India"
        460 -> "China"
        440 -> "Japan"
        450 -> "South Korea"
        505 -> "Australia"
        else -> "Unknown"
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
