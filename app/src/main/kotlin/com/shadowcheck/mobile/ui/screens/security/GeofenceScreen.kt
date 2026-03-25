package com.shadowcheck.mobile.ui.screens.security

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowcheck.mobile.core.model.Geofence
import com.shadowcheck.mobile.presentation.viewmodel.GeofenceViewModel
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@Composable
fun GeofenceRoute(
    viewModel: GeofenceViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var editingGeofence by remember { mutableStateOf<Geofence?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    GeofenceScreen(
        geofences = uiState.geofences,
        onAddGeofence = {
            editingGeofence = null
            showEditor = true
        },
        onEditGeofence = { geofence ->
            editingGeofence = geofence
            showEditor = true
        },
        onDeleteGeofence = viewModel::deleteGeofence,
        onToggleGeofence = viewModel::toggleGeofence
    )

    if (showEditor) {
        GeofenceEditorDialog(
            initialGeofence = editingGeofence,
            onDismiss = { showEditor = false },
            onSave = { name, latitude, longitude, radius, notifyOnEntry, notifyOnExit, isActive ->
                val lat = latitude.toDoubleOrNull()
                val lon = longitude.toDoubleOrNull()
                val rad = radius.toFloatOrNull()

                if (name.isBlank() || lat == null || lon == null || rad == null) {
                    Toast.makeText(context, "Enter valid geofence values", Toast.LENGTH_SHORT).show()
                    return@GeofenceEditorDialog
                }

                viewModel.saveGeofence(
                    Geofence(
                        id = editingGeofence?.id ?: 0,
                        name = name,
                        latitude = lat,
                        longitude = lon,
                        radius = rad,
                        isActive = isActive,
                        notifyOnEntry = notifyOnEntry,
                        notifyOnExit = notifyOnExit,
                        createdAt = editingGeofence?.createdAt ?: System.currentTimeMillis()
                    )
                )
                showEditor = false
            }
        )
    }
}

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
fun GeofenceEditorDialog(
    initialGeofence: Geofence?,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        latitude: String,
        longitude: String,
        radius: String,
        notifyOnEntry: Boolean,
        notifyOnExit: Boolean,
        isActive: Boolean
    ) -> Unit
) {
    var name by remember(initialGeofence) { mutableStateOf(initialGeofence?.name ?: "") }
    var latitude by remember(initialGeofence) { mutableStateOf(initialGeofence?.latitude?.toString() ?: "") }
    var longitude by remember(initialGeofence) { mutableStateOf(initialGeofence?.longitude?.toString() ?: "") }
    var radius by remember(initialGeofence) { mutableStateOf(initialGeofence?.radius?.toString() ?: "") }
    var notifyOnEntry by remember(initialGeofence) {
        mutableStateOf(initialGeofence?.notifyOnEntry ?: true)
    }
    var notifyOnExit by remember(initialGeofence) {
        mutableStateOf(initialGeofence?.notifyOnExit ?: true)
    }
    var isActive by remember(initialGeofence) { mutableStateOf(initialGeofence?.isActive ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initialGeofence == null) "Add Geofence" else "Edit Geofence")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = radius,
                    onValueChange = { radius = it },
                    label = { Text("Radius (m)") },
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notify on entry")
                    Switch(checked = notifyOnEntry, onCheckedChange = { notifyOnEntry = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notify on exit")
                    Switch(checked = notifyOnExit, onCheckedChange = { notifyOnExit = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Active")
                    Switch(checked = isActive, onCheckedChange = { isActive = it })
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(name, latitude, longitude, radius, notifyOnEntry, notifyOnExit, isActive)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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
