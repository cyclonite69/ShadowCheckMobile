package com.shadowcheck.mobile.ui.screens.maps

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.shadowcheck.mobile.data.BluetoothDevice
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@Composable
fun BluetoothMapScreen(
    devices: List<BluetoothDevice>,
    currentLat: Double,
    currentLon: Double,
    onDeviceClick: (BluetoothDevice) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(currentLat, currentLon), 15f)
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = true
            )
        ) {
            // Add markers for devices
            devices.filter { 
                it.latitude != 0.0 && it.longitude != 0.0 
            }.forEach { device ->
                Marker(
                    state = MarkerState(position = LatLng(device.latitude, device.longitude)),
                    title = device.name ?: "Unknown Device",
                    snippet = "${device.rssi} dBm",
                    onClick = {
                        onDeviceClick(device)
                        true
                    }
                )
            }
        }
        
        // Header
        Surface(
            modifier = Modifier.align(Alignment.TopCenter),
            color = ShadowCheckColors.Surface.copy(alpha = 0.9f),
            tonalElevation = 4.dp
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
                        text = "Bluetooth Map",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${devices.count { it.latitude != 0.0 }} devices",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ShadowCheckColors.TextSecondary
                    )
                }
            }
        }
    }
}
