package com.shadowcheck.mobile.rebuilt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.shadowcheck.mobile.presentation.viewmodel.MapViewModel
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val formatter = remember { SimpleDateFormat("HH:mm:ss", Locale.US) }
    val playbackStart = uiState.playbackStart
    val playbackEnd = uiState.playbackEnd
    val pathPoints = uiState.locationSamples.map { LatLng(it.latitude, it.longitude) }
    val latestPoint = pathPoints.lastOrNull()
    val wifiMarkers = if (uiState.showWifi) {
        uiState.wifiNetworks.filter { it.latitude != 0.0 && it.longitude != 0.0 }
    } else {
        emptyList()
    }
    val bluetoothMarkers = if (uiState.showBluetooth) {
        uiState.bluetoothDevices.filter { it.latitude != 0.0 && it.longitude != 0.0 }
    } else {
        emptyList()
    }
    val cellularMarkers = if (uiState.showCellular) {
        uiState.cellularTowers.filter { it.latitude != 0.0 && it.longitude != 0.0 }
    } else {
        emptyList()
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            latestPoint ?: LatLng(0.0, 0.0),
            if (latestPoint != null) 16f else 2f
        )
    }

    LaunchedEffect(latestPoint) {
        latestPoint?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 16f))
        }
    }
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("Network Map", color = ShadowCheckColors.Primary, fontSize = 18.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.TextPrimary)
                }
            },
            actions = {
                IconButton(onClick = { viewModel.toggleWifi() }) {
                    Icon(Icons.Default.Wifi, "WiFi", tint = if (uiState.showWifi) ShadowCheckColors.Accent else Color.Gray)
                }
                IconButton(onClick = { viewModel.toggleBluetooth() }) {
                    Icon(Icons.Default.Bluetooth, "BT", tint = if (uiState.showBluetooth) ShadowCheckColors.Accent else Color.Gray)
                }
                IconButton(onClick = { viewModel.toggleCellular() }) {
                    Icon(Icons.Default.CellTower, "Cell", tint = if (uiState.showCellular) ShadowCheckColors.Accent else Color.Gray)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadowCheckColors.Surface)
        )

        if (uiState.sessions.isNotEmpty()) {
            SessionSelectorCard(
                sessions = uiState.sessions,
                selectedSessionId = uiState.selectedSessionId,
                onSelectSession = { viewModel.selectSession(it) }
            )
        }

        if (playbackStart != null && playbackEnd != null && playbackStart != playbackEnd) {
            PlaybackControlCard(
                playbackEnabled = uiState.playbackEnabled,
                playbackStart = playbackStart,
                playbackEnd = playbackEnd,
                playbackTimestamp = uiState.playbackTimestamp ?: playbackEnd,
                formatter = formatter,
                onPlaybackToggle = { viewModel.setPlaybackEnabled(it) },
                onPlaybackChange = { viewModel.setPlaybackTimestamp(it) },
                onJumpToLatest = { viewModel.jumpToLatest() }
            )
        }
        
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = true
                )
            ) {
                if (pathPoints.size >= 2) {
                    Polyline(
                        points = pathPoints,
                        color = ShadowCheckColors.Accent,
                        width = 8f
                    )
                }

                latestPoint?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Latest Position",
                        snippet = "Trajectory head"
                    )
                }

                wifiMarkers.forEach { network ->
                    Marker(
                        state = MarkerState(position = LatLng(network.latitude, network.longitude)),
                        title = network.ssid.ifEmpty { "Hidden Network" },
                        snippet = "${network.signalLevel} dBm"
                    )
                }

                bluetoothMarkers.forEach { device ->
                    Marker(
                        state = MarkerState(position = LatLng(device.latitude, device.longitude)),
                        title = device.name.ifBlank { device.macAddress },
                        snippet = "${device.rssi} dBm"
                    )
                }

                cellularMarkers.forEach { tower ->
                    Marker(
                        state = MarkerState(position = LatLng(tower.latitude, tower.longitude)),
                        title = tower.operatorName.ifBlank { "Cell ${tower.cellId}" },
                        snippet = "${tower.networkType} ${tower.signalStrength}"
                    )
                }
            }

            TrajectoryStatusCard(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                sampleCount = uiState.locationSamples.size,
                firstTimestamp = uiState.locationSamples.lastOrNull()?.timestamp,
                lastTimestamp = uiState.locationSamples.firstOrNull()?.timestamp,
                latestLatitude = uiState.locationSamples.firstOrNull()?.latitude,
                latestLongitude = uiState.locationSamples.firstOrNull()?.longitude,
                playbackLabel = if (uiState.playbackEnabled) {
                    "Playback @ ${formatter.format(Date(uiState.playbackTimestamp ?: 0L))}"
                } else {
                    "Live"
                }
            )
        }
    }
}

@Composable
private fun SessionSelectorCard(
    sessions: List<com.shadowcheck.mobile.core.model.ScanSession>,
    selectedSessionId: String?,
    onSelectSession: (String?) -> Unit
) {
    val formatter = remember { SimpleDateFormat("HH:mm:ss", Locale.US) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface.copy(alpha = 0.88f)),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Sessions",
                color = ShadowCheckColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sessions.take(8).forEach { session ->
                    val selected = session.sessionId == selectedSessionId
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectSession(session.sessionId) },
                        label = {
                            Text(
                                text = buildString {
                                    append(if (session.status == "active") "Active" else "Done")
                                    append(" • ")
                                    append(formatter.format(Date(session.startedAt)))
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaybackControlCard(
    playbackEnabled: Boolean,
    playbackStart: Long,
    playbackEnd: Long,
    playbackTimestamp: Long,
    formatter: SimpleDateFormat,
    onPlaybackToggle: (Boolean) -> Unit,
    onPlaybackChange: (Long) -> Unit,
    onJumpToLatest: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface.copy(alpha = 0.88f)),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Playback Window",
                    color = ShadowCheckColors.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                FilterChip(
                    selected = playbackEnabled,
                    onClick = { onPlaybackToggle(!playbackEnabled) },
                    label = { Text(if (playbackEnabled) "Playback" else "Live") }
                )
            }

            Text(
                text = "Current: ${formatter.format(Date(playbackTimestamp))}",
                color = ShadowCheckColors.Accent,
                fontSize = 12.sp
            )

            Slider(
                value = playbackTimestamp.toFloat(),
                onValueChange = { onPlaybackChange(it.toLong()) },
                valueRange = playbackStart.toFloat()..playbackEnd.toFloat()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(formatter.format(Date(playbackStart)), color = ShadowCheckColors.TextSecondary, fontSize = 11.sp)
                TextButton(onClick = onJumpToLatest) {
                    Text("Jump To Latest")
                }
                Text(formatter.format(Date(playbackEnd)), color = ShadowCheckColors.TextSecondary, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun TrajectoryStatusCard(
    modifier: Modifier = Modifier,
    sampleCount: Int,
    firstTimestamp: Long?,
    lastTimestamp: Long?,
    latestLatitude: Double?,
    latestLongitude: Double?,
    playbackLabel: String
) {
    val formatter = remember { SimpleDateFormat("HH:mm:ss", Locale.US) }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface.copy(alpha = 0.78f)),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Trajectory Buffer",
                color = ShadowCheckColors.Primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(playbackLabel, color = ShadowCheckColors.Accent, fontSize = 12.sp)
            Text("Samples: $sampleCount", color = ShadowCheckColors.TextPrimary)
            Text(
                text = if (firstTimestamp != null && lastTimestamp != null) {
                    "Span: ${formatter.format(Date(firstTimestamp))} -> ${formatter.format(Date(lastTimestamp))}"
                } else {
                    "Span: no samples yet"
                },
                color = ShadowCheckColors.TextSecondary,
                textAlign = TextAlign.Center
            )
            Text(
                text = if (latestLatitude != null && latestLongitude != null) {
                    "Latest: ${"%.6f".format(latestLatitude)}, ${"%.6f".format(latestLongitude)}"
                } else {
                    "Latest: no fix yet"
                },
                color = ShadowCheckColors.TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
