package com.shadowcheck.mobile.ui.screens.finder

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shadowcheck.mobile.data.WifiNetwork
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun NetworkFinderScreen(
    targetNetwork: WifiNetwork?,
    currentLat: Double,
    currentLon: Double,
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
                        text = "Network Finder",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    targetNetwork?.let {
                        Text(
                            text = it.ssid.ifEmpty { "Hidden Network" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = ShadowCheckColors.TextSecondary
                        )
                    }
                }
            }
        }
        
        if (targetNetwork == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.SearchOff,
                        null,
                        modifier = Modifier.size(64.dp),
                        tint = ShadowCheckColors.TextSecondary
                    )
                    Text("No target network selected")
                }
            }
        } else {
            val distance = calculateDistance(
                currentLat, currentLon,
                targetNetwork.latitude, targetNetwork.longitude
            )
            val bearing = calculateBearing(
                currentLat, currentLon,
                targetNetwork.latitude, targetNetwork.longitude
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Distance card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Distance",
                            style = MaterialTheme.typography.titleMedium,
                            color = ShadowCheckColors.TextSecondary
                        )
                        Text(
                            text = String.format("%.1f m", distance),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                distance < 10 -> ShadowCheckColors.SignalGood
                                distance < 50 -> ShadowCheckColors.SignalMedium
                                else -> ShadowCheckColors.Error
                            }
                        )
                    }
                }
                
                // Compass
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Navigation,
                                null,
                                modifier = Modifier.size(120.dp),
                                tint = ShadowCheckColors.Primary
                            )
                            Text(
                                text = "${bearing.toInt()}°",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = getDirection(bearing),
                                color = ShadowCheckColors.TextSecondary
                            )
                        }
                    }
                }
                
                // Signal strength
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Signal Strength",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${targetNetwork.signalLevel} dBm")
                            Text(
                                text = when {
                                    targetNetwork.signalLevel >= -50 -> "Excellent"
                                    targetNetwork.signalLevel >= -60 -> "Good"
                                    targetNetwork.signalLevel >= -70 -> "Fair"
                                    else -> "Weak"
                                },
                                color = when {
                                    targetNetwork.signalLevel >= -60 -> ShadowCheckColors.SignalGood
                                    targetNetwork.signalLevel >= -75 -> ShadowCheckColors.SignalMedium
                                    else -> ShadowCheckColors.Error
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371000.0 // Earth radius in meters
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}

private fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLon = Math.toRadians(lon2 - lon1)
    val y = sin(dLon) * cos(Math.toRadians(lat2))
    val x = cos(Math.toRadians(lat1)) * sin(Math.toRadians(lat2)) -
            sin(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * cos(dLon)
    var bearing = Math.toDegrees(atan2(y, x))
    bearing = (bearing + 360) % 360
    return bearing
}

private fun getDirection(bearing: Double): String {
    return when {
        bearing < 22.5 || bearing >= 337.5 -> "North"
        bearing < 67.5 -> "Northeast"
        bearing < 112.5 -> "East"
        bearing < 157.5 -> "Southeast"
        bearing < 202.5 -> "South"
        bearing < 247.5 -> "Southwest"
        bearing < 292.5 -> "West"
        else -> "Northwest"
    }
}
