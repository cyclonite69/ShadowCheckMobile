package com.shadowcheck.mobile.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : NavItem("home", "Home", Icons.Default.Home)
    object WiFi : NavItem("wifi", "WiFi", Icons.Default.Wifi)
    object Bluetooth : NavItem("bluetooth", "Bluetooth", Icons.Default.Bluetooth)
    object Cellular : NavItem("cellular", "Cellular", Icons.Default.CellTower)
    object Map : NavItem("map", "Map", Icons.Default.Map)
    object Security : NavItem("security", "Security", Icons.Default.Security)
    object Stats : NavItem("stats", "Statistics", Icons.Default.BarChart)
    object Settings : NavItem("settings", "Settings", Icons.Default.Settings)
}

val navItems = listOf(
    NavItem.Home,
    NavItem.WiFi,
    NavItem.Bluetooth,
    NavItem.Cellular,
    NavItem.Map,
    NavItem.Security,
    NavItem.Stats,
    NavItem.Settings
)
