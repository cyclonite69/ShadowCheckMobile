package com.shadowcheck.mobile.ui.screens.maps

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.shadowcheck.mobile.data.WifiNetwork
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@Composable
fun GoogleMapScreen(
    networks: List<WifiNetwork>,
    currentLat: Double,
    currentLon: Double,
    onNetworkClick: (WifiNetwork) -> Unit
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
            // Add markers for networks
            networks.filter { 
                it.latitude != 0.0 && it.longitude != 0.0 
            }.forEach { network ->
                Marker(
                    state = MarkerState(position = LatLng(network.latitude, network.longitude)),
                    title = network.ssid.ifEmpty { "Hidden Network" },
                    snippet = "${network.signalLevel} dBm",
                    onClick = {
                        onNetworkClick(network)
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
                        text = "Network Map",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${networks.count { it.latitude != 0.0 }} networks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ShadowCheckColors.TextSecondary
                    )
                }
            }
        }
    }
}
