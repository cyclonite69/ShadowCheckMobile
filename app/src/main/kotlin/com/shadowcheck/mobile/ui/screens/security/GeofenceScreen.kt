package com.shadowcheck.mobile.ui.screens.security

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shadowcheck.mobile.data.Geofence
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@Composable
fun GeofenceScreen(
    geofences: List<Geofence>,
    onAddGeofence: () -> Unit,
    onEditGeofence: (Geofence) -> Unit,
    onDeleteGeofence: (Geofence) -> Unit,
    onToggleGeofence: (Geofence) -> Unit
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Geofences",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${geofences.count { it.isActive }} active",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ShadowCheckColors.TextSecondary
                    )
                }
                
                IconButton(onClick = onAddGeofence) {
                    Icon(Icons.Default.Add, "Add geofence")
                }
            }
        }
        
        if (geofences.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        modifier = Modifier.size(64.dp),
                        tint = ShadowCheckColors.TextSecondary
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "No Geofences",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Create geofences to monitor specific areas",
                        color = ShadowCheckColors.TextSecondary
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onAddGeofence) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Add Geofence")
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(geofences) { geofence ->
                    GeofenceCard(
                        geofence = geofence,
                        onEdit = { onEditGeofence(geofence) },
                        onDelete = { onDeleteGeofence(geofence) },
                        onToggle = { onToggleGeofence(geofence) }
                    )
                }
            }
        }
    }
}

@Composable
fun GeofenceCard(
    geofence: Geofence,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggle: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (geofence.isActive) 
                ShadowCheckColors.Surface 
            else 
                ShadowCheckColors.Surface.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        tint = if (geofence.isActive) 
                            ShadowCheckColors.Primary 
                        else 
                            ShadowCheckColors.TextSecondary
                    )
                    Column {
                        Text(
                            text = geofence.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${geofence.radius.toInt()}m radius",
                            style = MaterialTheme.typography.bodySmall,
                            color = ShadowCheckColors.TextSecondary
                        )
                    }
                }
                
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "More")
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (geofence.isActive) "Disable" else "Enable") },
                            onClick = {
                                onToggle()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    if (geofence.isActive) Icons.Default.ToggleOff 
                                    else Icons.Default.ToggleOn,
                                    null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                onEdit()
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                onDelete()
                                showMenu = false
                            },
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.Delete, 
                                    null,
                                    tint = ShadowCheckColors.Error
                                ) 
                            }
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (geofence.notifyOnEntry) {
                    Chip(text = "Entry Alert", icon = Icons.Default.Login)
                }
                if (geofence.notifyOnExit) {
                    Chip(text = "Exit Alert", icon = Icons.Default.Logout)
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = "Location: ${String.format("%.6f", geofence.latitude)}, ${String.format("%.6f", geofence.longitude)}",
                style = MaterialTheme.typography.bodySmall,
                color = ShadowCheckColors.TextSecondary
            )
        }
    }
}

@Composable
fun Chip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        color = ShadowCheckColors.Primary.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                null,
                modifier = Modifier.size(14.dp),
                tint = ShadowCheckColors.Primary
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = ShadowCheckColors.Primary
            )
        }
    }
}
