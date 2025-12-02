package com.shadowcheck.mobile.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@Composable
fun SettingsScreen(
    onNavigateToApiKeys: () -> Unit,
    onNavigateToDatabase: () -> Unit,
    onExportData: () -> Unit,
    onClearData: () -> Unit
) {
    var scanInterval by remember { mutableStateOf(3f) }
    var autoScan by remember { mutableStateOf(true) }
    var showNotifications by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Surface(
            color = ShadowCheckColors.Surface,
            tonalElevation = 2.dp
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Scanning settings
            SettingsSection(title = "Scanning") {
                SettingsRow(
                    title = "Auto Scan",
                    subtitle = "Automatically scan for networks",
                    trailing = {
                        Switch(
                            checked = autoScan,
                            onCheckedChange = { autoScan = it }
                        )
                    }
                )
                
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Scan Interval: ${scanInterval.toInt()} seconds")
                    Slider(
                        value = scanInterval,
                        onValueChange = { scanInterval = it },
                        valueRange = 1f..10f,
                        steps = 8
                    )
                }
            }
            
            // Notifications
            SettingsSection(title = "Notifications") {
                SettingsRow(
                    title = "Show Notifications",
                    subtitle = "Alert on threat detection",
                    trailing = {
                        Switch(
                            checked = showNotifications,
                            onCheckedChange = { showNotifications = it }
                        )
                    }
                )
            }
            
            // Appearance
            SettingsSection(title = "Appearance") {
                SettingsRow(
                    title = "Dark Mode",
                    subtitle = "Use dark theme",
                    trailing = {
                        Switch(
                            checked = darkMode,
                            onCheckedChange = { darkMode = it }
                        )
                    }
                )
            }
            
            // Data management
            SettingsSection(title = "Data Management") {
                SettingsRow(
                    title = "API Keys",
                    subtitle = "Manage WiGLE and other API keys",
                    icon = Icons.Default.Key,
                    onClick = onNavigateToApiKeys
                )
                
                SettingsRow(
                    title = "Database",
                    subtitle = "View database statistics",
                    icon = Icons.Default.Storage,
                    onClick = onNavigateToDatabase
                )
                
                SettingsRow(
                    title = "Export Data",
                    subtitle = "Export to CSV, KML, or GeoJSON",
                    icon = Icons.Default.Download,
                    onClick = onExportData
                )
                
                SettingsRow(
                    title = "Clear All Data",
                    subtitle = "Delete all scanned networks",
                    icon = Icons.Default.Delete,
                    iconTint = ShadowCheckColors.Error,
                    onClick = onClearData
                )
            }
            
            // About
            SettingsSection(title = "About") {
                SettingsRow(
                    title = "Version",
                    subtitle = "1.0.0"
                )
                
                SettingsRow(
                    title = "Open Source Licenses",
                    icon = Icons.Default.Info,
                    onClick = { /* Show licenses */ }
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ShadowCheckColors.Primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = ShadowCheckColors.Surface
            )
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsRow(
    title: String,
    subtitle: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconTint: androidx.compose.ui.graphics.Color = ShadowCheckColors.TextPrimary,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = iconTint
                )
            }
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = ShadowCheckColors.TextSecondary
                    )
                }
            }
        }
        
        trailing?.invoke()
    }
}
