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
import com.shadowcheck.mobile.data.BluetoothDevice
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BluetoothDetailsScreen(
    device: BluetoothDevice,
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
                        text = device.name ?: "Unknown Device",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = device.address,
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
                DetailRow("RSSI", "${device.rssi} dBm")
                DetailRow("Signal Quality", getSignalQuality(device.rssi))
            }
            
            // Device info
            DetailCard(title = "Device Information") {
                DetailRow("Device Type", getDeviceType(device.deviceType))
                DetailRow("Device Class", device.deviceClass.toString(16).uppercase())
                DetailRow("Bond State", getBondState(device.bondState))
            }
            
            // Location
            if (device.latitude != 0.0 || device.longitude != 0.0) {
                DetailCard(title = "Location") {
                    DetailRow("Latitude", String.format("%.6f", device.latitude))
                    DetailRow("Longitude", String.format("%.6f", device.longitude))
                    if (device.altitude != 0.0) {
                        DetailRow("Altitude", String.format("%.1f m", device.altitude))
                    }
                    if (device.accuracy > 0) {
                        DetailRow("Accuracy", String.format("%.1f m", device.accuracy))
                    }
                }
            }
            
            // Timestamps
            DetailCard(title = "Timestamps") {
                DetailRow("Last Seen", formatTimestamp(device.timestamp))
                if (device.firstSeen > 0) {
                    DetailRow("First Seen", formatTimestamp(device.firstSeen))
                }
                DetailRow("Source", device.source)
            }
        }
    }
}

private fun getDeviceType(type: Int): String {
    return when (type) {
        1 -> "Classic Bluetooth"
        2 -> "Bluetooth Low Energy (BLE)"
        3 -> "Dual Mode (Classic + BLE)"
        else -> "Unknown"
    }
}

private fun getBondState(state: Int): String {
    return when (state) {
        10 -> "Not Bonded"
        11 -> "Bonding"
        12 -> "Bonded (Paired)"
        else -> "Unknown"
    }
}

private fun getSignalQuality(rssi: Int): String {
    return when {
        rssi >= -50 -> "Excellent"
        rssi >= -60 -> "Good"
        rssi >= -70 -> "Fair"
        rssi >= -80 -> "Weak"
        else -> "Very Weak"
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
