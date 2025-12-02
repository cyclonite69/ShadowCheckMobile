package com.shadowcheck.mobile.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.NetworkCell
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shadowcheck.mobile.ui.screens.bluetooth.BluetoothScreen
import com.shadowcheck.mobile.ui.screens.cellular.CellularScreen
import com.shadowcheck.mobile.ui.screens.wifi.WifiScreen

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Wifi : BottomNavItem("wifi", Icons.Default.Wifi, "Wi-Fi")
    object Bluetooth : BottomNavItem("bluetooth", Icons.Default.Bluetooth, "Bluetooth")
    object Cellular : BottomNavItem("cellular", Icons.Default.NetworkCell, "Cellular")
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Wifi,
        BottomNavItem.Bluetooth,
        BottomNavItem.Cellular,
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = BottomNavItem.Wifi.route, Modifier.padding(innerPadding)) {
            composable(BottomNavItem.Wifi.route) { WifiScreen() }
            composable(BottomNavItem.Bluetooth.route) { BluetoothScreen() }
            composable(BottomNavItem.Cellular.route) { CellularScreen() }
        }
    }
}
