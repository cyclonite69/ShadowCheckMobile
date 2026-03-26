package com.shadowcheck.mobile.rebuilt.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowcheck.mobile.core.model.BluetoothDevice
import com.shadowcheck.mobile.presentation.viewmodel.BluetoothDetailViewModel
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BluetoothDetailScreen(
    macAddress: String,
    viewModel: BluetoothDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val device = uiState.device
    val sightings = uiState.sightings

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShadowCheckColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        device?.name?.ifBlank { "Bluetooth Device" } ?: "Bluetooth Device",
                        color = ShadowCheckColors.Primary,
                        fontSize = 18.sp
                    )
                    Text(macAddress, color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.TextPrimary)
                }
            },
            actions = {
                IconButton(onClick = {}) { Icon(Icons.Default.PlayArrow, "Playback", tint = ShadowCheckColors.Error) }
                IconButton(onClick = {}) { Icon(Icons.Default.Map, "Map", tint = ShadowCheckColors.TextPrimary) }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadowCheckColors.Surface)
        )

        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = ShadowCheckColors.Primary)
                }
            }

            device == null -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Device not found", color = ShadowCheckColors.TextPrimary, fontWeight = FontWeight.Bold)
                        Text(macAddress, color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                    }
                }
            }

            else -> {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Bluetooth Summary", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ShadowCheckColors.Primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Sightings", color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                                Text(sightings.size.toString(), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Avg RSSI", color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                                Text("${uiState.avgSignal} dBm", color = ShadowCheckColors.Primary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Signal Strength Over Time", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ShadowCheckColors.Primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        BluetoothSignalGraph(sightings)
                        BluetoothInfoRow("Min", "${uiState.minSignal} dBm")
                        BluetoothInfoRow("Max", "${uiState.maxSignal} dBm")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Device Information", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ShadowCheckColors.Primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        BluetoothInfoRow("MAC Address", device.macAddress)
                        BluetoothInfoRow("Device Name", device.name.ifBlank { "Unknown Device" })
                        BluetoothInfoRow("RSSI", "${device.rssi} dBm")
                        BluetoothInfoRow("Device Type", deviceTypeLabel(device.deviceType))
                        BluetoothInfoRow("Device Class", "0x${device.deviceClass.toString(16).uppercase()}")
                        BluetoothInfoRow("Bond State", bondStateLabel(device.bondState))
                        BluetoothInfoRow("First Seen", formatBluetoothTimestamp(device.firstSeen))
                        BluetoothInfoRow("Last Seen", formatBluetoothTimestamp(device.lastSeen))
                        BluetoothInfoRow("Latitude", formatCoordinate(device.latitude))
                        BluetoothInfoRow("Longitude", formatCoordinate(device.longitude))
                        BluetoothInfoRow("Altitude", formatMeters(device.altitude))
                        BluetoothInfoRow("Accuracy", formatMeters(device.accuracy.toDouble()))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun BluetoothSignalGraph(sightings: List<BluetoothDevice>) {
    if (sightings.isEmpty()) return

    Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        val maxSignal = sightings.maxOf { it.rssi }.toFloat()
        val minSignal = sightings.minOf { it.rssi }.toFloat()
        val range = maxSignal - minSignal

        val points = sightings.mapIndexed { index, sighting ->
            val x = if (sightings.size == 1) 0f else (index.toFloat() / (sightings.size - 1)) * size.width
            val normalizedSignal = if (range > 0) (sighting.rssi - minSignal) / range else 0.5f
            val y = size.height - (normalizedSignal * size.height)
            Offset(x, y)
        }

        for (i in 0 until points.size - 1) {
            drawLine(
                color = ShadowCheckColors.Primary,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 3f
            )
        }
    }
}

private fun deviceTypeLabel(type: Int): String {
    return when (type) {
        1 -> "Classic Bluetooth"
        2 -> "Bluetooth Low Energy (BLE)"
        3 -> "Dual Mode"
        else -> "Unknown"
    }
}

private fun bondStateLabel(state: Int): String {
    return when (state) {
        10 -> "Not Bonded"
        11 -> "Bonding"
        12 -> "Bonded"
        else -> "Unknown"
    }
}

private fun formatCoordinate(value: Double): String {
    return if (value == 0.0) "Unavailable" else String.format("%.6f", value)
}

private fun formatMeters(value: Double): String {
    return if (value <= 0.0) "Unavailable" else String.format("%.1f m", value)
}

private fun formatBluetoothTimestamp(timestamp: Long): String {
    return if (timestamp <= 0L) "Unknown" else SimpleDateFormat("MMM d, yyyy HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
}

@Composable
private fun BluetoothInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = ShadowCheckColors.TextSecondary, fontSize = 14.sp, modifier = Modifier.weight(0.4f))
        Text(value, color = ShadowCheckColors.TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(0.6f))
    }
}
