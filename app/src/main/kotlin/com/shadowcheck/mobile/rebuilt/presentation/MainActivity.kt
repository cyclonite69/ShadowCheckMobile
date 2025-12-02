package com.shadowcheck.mobile.rebuilt.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.ui.zIndex
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckTheme
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import com.shadowcheck.mobile.rebuilt.ui.screens.HomeScreen
import com.shadowcheck.mobile.ui.Sidebar
import com.shadowcheck.mobile.ui.screens.lists.NetworkListScreen
import com.shadowcheck.mobile.ui.screens.lists.BluetoothListScreen
import com.shadowcheck.mobile.ui.screens.lists.CellularListScreen
import com.shadowcheck.mobile.ui.screens.maps.MapScreen
import com.shadowcheck.mobile.rebuilt.ui.screens.PlaybackScreen
import com.shadowcheck.mobile.ui.screens.finder.NetworkFinderScreen
import com.shadowcheck.mobile.ui.screens.security.SurveillanceScreen
import com.shadowcheck.mobile.ui.screens.security.GeofenceScreen
import com.shadowcheck.mobile.ui.screens.security.ChannelAnalysisScreen
import com.shadowcheck.mobile.ui.screens.NetworkStats
import com.shadowcheck.mobile.ui.screens.StatsScreen
import com.shadowcheck.mobile.rebuilt.ui.screens.WigleScreen
import com.shadowcheck.mobile.ui.screens.settings.SettingsScreen
import com.shadowcheck.mobile.util.EmulatorHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Start mock scanner if running on emulator
        EmulatorHelper.startMockScannerIfEmulator(this)
        
        setContent {
            ShadowCheckTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var sidebarOpen by remember { mutableStateOf(false) }
    var currentRoute by remember { mutableStateOf("home") }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("home") {
                currentRoute = "home"
                HomeScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable("wifi") {
                currentRoute = "wifi"
                NetworkListScreen(
                    networks = emptyList(),
                    filters = com.shadowcheck.mobile.models.WiFiFilters(),
                    onFiltersChange = {},
                    onNetworkClick = {},
                    onExport = {}
                )
            }
            composable("bluetooth") {
                currentRoute = "bluetooth"
                BluetoothListScreen(
                    devices = emptyList(),
                    filters = com.shadowcheck.mobile.models.BluetoothFilters(),
                    onFiltersChange = {},
                    onDeviceClick = {},
                    onExport = {}
                )
            }
            composable("cellular") {
                currentRoute = "cellular"
                CellularListScreen(
                    towers = emptyList(),
                    filters = com.shadowcheck.mobile.models.CellularFilters(),
                    onFiltersChange = {},
                    onTowerClick = {},
                    onExport = {}
                )
            }
            composable("map") {
                currentRoute = "map"
                com.shadowcheck.mobile.rebuilt.ui.screens.MapScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable("heatmap") {
                currentRoute = "heatmap"
                com.shadowcheck.mobile.rebuilt.ui.screens.HeatmapScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable("playback") {
                currentRoute = "playback"
                PlaybackScreen()
            }
            composable("finder") {
                currentRoute = "finder"
                NetworkFinderScreen(
                    targetNetwork = null,
                    currentLat = 0.0,
                    currentLon = 0.0,
                    onBack = {}
                )
            }
            composable("threats") {
                currentRoute = "threats"
                com.shadowcheck.mobile.rebuilt.ui.screens.ThreatDetectionScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable("geofence") {
                currentRoute = "geofence"
                GeofenceScreen(
                    geofences = emptyList(),
                    onAddGeofence = {},
                    onEditGeofence = {},
                    onDeleteGeofence = {},
                    onToggleGeofence = { _ -> }
                )
            }
            composable("channels") {
                currentRoute = "channels"
                com.shadowcheck.mobile.rebuilt.ui.screens.ChannelDistributionScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable("stats") {
                currentRoute = "stats"
                StatsScreen(
                    stats = NetworkStats(0, 0, 0, 0, 0, 0, 0, 0, 0, "N/A", "N/A", 0, 0, 0)
                )
            }
            composable("wigle") {
                currentRoute = "wigle"
                WigleScreen()
            }
            composable("theme") {
                currentRoute = "theme"
                com.shadowcheck.mobile.rebuilt.ui.screens.ComprehensiveThemeEditor(
                    onBack = { navController.popBackStack() }
                )
            }
            composable("settings") {
                currentRoute = "settings"
                com.shadowcheck.mobile.rebuilt.ui.screens.SettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable("wifi_list") {
                currentRoute = "wifi_list"
                com.shadowcheck.mobile.rebuilt.ui.screens.WiFiListScreen(
                    onBack = { navController.popBackStack() },
                    onNetworkClick = { bssid -> navController.navigate("network_detail/$bssid") }
                )
            }
            composable("network_detail/{bssid}") { backStackEntry ->
                currentRoute = "network_detail"
                val bssid = backStackEntry.arguments?.getString("bssid") ?: ""
                com.shadowcheck.mobile.rebuilt.ui.screens.NetworkDetailScreen(
                    bssid = bssid,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("bluetooth_list") {
                currentRoute = "bluetooth_list"
                com.shadowcheck.mobile.rebuilt.ui.screens.BluetoothListScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable("cellular_list") {
                currentRoute = "cellular_list"
                com.shadowcheck.mobile.rebuilt.ui.screens.CellularListScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
        
        // Sidebar with spring animation
        val sidebarOffset by animateDpAsState(
            targetValue = if (sidebarOpen) 0.dp else (-280).dp,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .offset(x = sidebarOffset)
                .zIndex(2f)
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight()
                        .background(ShadowCheckColors.Surface)
                ) {
                    Sidebar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            currentRoute = route
                            navController.navigate(route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                            sidebarOpen = false
                        }
                    )
                }
                
                // Tab - smaller and more transparent
                Box(
                    modifier = Modifier
                        .width(30.dp)
                        .height(80.dp)
                        .align(Alignment.CenterVertically)
                        .background(ShadowCheckColors.Primary.copy(alpha = 0.7f), RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                        .clickable { sidebarOpen = !sidebarOpen },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (sidebarOpen) Icons.Default.ChevronLeft else Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        
        if (sidebarOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { sidebarOpen = false }
            )
        }
    }
}
