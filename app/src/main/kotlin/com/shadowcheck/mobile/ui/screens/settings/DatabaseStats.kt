package com.shadowcheck.mobile.ui.screens.settings

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
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@Composable
fun DatabaseStatsScreen(
    wifiCount: Int,
    btCount: Int,
    cellCount: Int,
    totalSize: Long,
    onBack: () -> Unit,
    onOptimize: () -> Unit,
    onVacuum: () -> Unit
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
                Text(
                    text = "Database Statistics",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Record counts
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = ShadowCheckColors.Surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Record Counts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    StatRow(
                        icon = Icons.Default.Wifi,
                        label = "WiFi Networks",
                        value = wifiCount.toString()
                    )
                    
                    StatRow(
                        icon = Icons.Default.Bluetooth,
                        label = "Bluetooth Devices",
                        value = btCount.toString()
                    )
                    
                    StatRow(
                        icon = Icons.Default.CellTower,
                        label = "Cell Towers",
                        value = cellCount.toString()
                    )
                    
                    Divider()
                    
                    StatRow(
                        icon = Icons.Default.Storage,
                        label = "Total Records",
                        value = (wifiCount + btCount + cellCount).toString(),
                        highlighted = true
                    )
                }
            }
            
            // Storage
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = ShadowCheckColors.Surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Storage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    StatRow(
                        icon = Icons.Default.Folder,
                        label = "Database Size",
                        value = formatBytes(totalSize)
                    )
                }
            }
            
            // Maintenance
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = ShadowCheckColors.Surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Maintenance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Button(
                        onClick = onOptimize,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CleaningServices, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Remove Duplicates")
                    }
                    
                    OutlinedButton(
                        onClick = onVacuum,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Compress, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Optimize Database")
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    highlighted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (highlighted) ShadowCheckColors.Primary else ShadowCheckColors.TextSecondary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal
            )
        }
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (highlighted) ShadowCheckColors.Primary else ShadowCheckColors.TextPrimary
        )
    }
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}
