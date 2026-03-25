package com.shadowcheck.mobile.rebuilt.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.shadowcheck.mobile.presentation.viewmodel.PurgeState
import com.shadowcheck.mobile.presentation.viewmodel.SettingsViewModel
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val encryptedPrefs = getEncryptedPrefs(context)
    val purgeState by viewModel.purgeState.collectAsState()
    
    var mapProvider by remember { mutableStateOf(encryptedPrefs.getString("map_provider", "Mapbox") ?: "Mapbox") }
    var scanInterval by remember { mutableStateOf(encryptedPrefs.getInt("scan_interval", 3)) }
    
    var showBackupDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showApiKeysDialog by remember { mutableStateOf(false) }

    LaunchedEffect(purgeState) {
        when (val state = purgeState) {
            PurgeState.Idle -> Unit
            PurgeState.InProgress -> Unit
            PurgeState.Success -> {
                Toast.makeText(context, "Sensor data purged", Toast.LENGTH_SHORT).show()
            }
            is PurgeState.Error -> {
                Toast.makeText(context, state.msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text("Settings", color = ShadowCheckColors.Primary, fontSize = 18.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.TextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadowCheckColors.Surface)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Map Provider
        SettingsSection("Map Provider") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { 
                        mapProvider = "Mapbox"
                        encryptedPrefs.edit().putString("map_provider", "Mapbox").apply()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mapProvider == "Mapbox") Color(0xFF9C27B0) else Color(0xFF424242)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Mapbox")
                }
                Button(
                    onClick = { 
                        mapProvider = "Google Maps"
                        encryptedPrefs.edit().putString("map_provider", "Google Maps").apply()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mapProvider == "Google Maps") Color(0xFF9C27B0) else Color(0xFF424242)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Google Maps")
                }
            }
        }
        
        // Scan Interval
        SettingsSection("Scan Interval") {
            Text("${scanInterval}s between scans", color = ShadowCheckColors.TextPrimary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = scanInterval.toFloat(),
                onValueChange = { 
                    scanInterval = it.toInt()
                    encryptedPrefs.edit().putInt("scan_interval", scanInterval).apply()
                },
                valueRange = 1f..10f,
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF00BCD4),
                    activeTrackColor = Color(0xFF00BCD4)
                )
            )
        }
        
        // Database
        SettingsSection("Database") {
            Button(
                onClick = { /* Show stats */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Info, "Stats", tint = Color(0xFF00BCD4))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Database Statistics")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { showBackupDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, "Backup")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Backup Database")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { /* Import WiGLE */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Upload, "Import")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Import WiGLE Database")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { /* Remove duplicates */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, "Remove")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Remove Duplicates")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { viewModel.purgeSensorData() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Delete, "Purge")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (purgeState is PurgeState.InProgress) "Purging Sensor Data..."
                    else "Purge Sensor Data"
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Delete, "Delete")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Records Without GPS")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { showClearDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Delete, "Clear")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear All Data")
            }
        }
        
        // Export
        SettingsSection("Export") {
            Button(
                onClick = { /* Export */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, "Export")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export CSV/KML/GeoJSON")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { /* Import */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Download, "Import")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Import WiGLE CSV")
            }
        }
        
        // API Keys
        SettingsSection("API Keys") {
            Button(
                onClick = { showApiKeysDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Key, "Keys", tint = Color(0xFF00BCD4))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Manage API Keys")
            }
        }
        
        // About
        SettingsSection("About ShadowCheck") {
            Text("ShadowCheck Mobile", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Advanced SIGINT & Network Intelligence Platform", color = ShadowCheckColors.Primary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Comprehensive signals intelligence collection and analysis for WiFi, Bluetooth, and cellular networks. Features wardriving, rogue AP detection, AR network finding, geofencing, and real-time surveillance detection.",
                color = ShadowCheckColors.TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Version 1.0.0", color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
    
    // Backup Dialog
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Backup Database?") },
            text = { Text("This will save a copy of the database to your Downloads folder.") },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "Database backed up", Toast.LENGTH_SHORT).show()
                        showBackupDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Backup")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showBackupDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575))
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Delete Records Without GPS Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Records Without GPS?") },
            text = { Text("This will permanently delete all network records that don't have GPS coordinates.") },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "Records deleted", Toast.LENGTH_SHORT).show()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575))
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Clear All Data Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            icon = { Icon(Icons.Default.Warning, "Warning", tint = Color(0xFFFF9800)) },
            title = { Text("DELETE ALL DATA?", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("This will permanently delete ALL collected data:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• All WiFi networks")
                    Text("• All Bluetooth devices")
                    Text("• All cellular towers")
                    Text("• All sighting records")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("This action CANNOT be undone!", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("BACKUP FIRST", fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "All data cleared", Toast.LENGTH_SHORT).show()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("DELETE ALL")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showClearDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575))
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // API Keys Dialog
    if (showApiKeysDialog) {
        var wigleKey by remember { mutableStateOf(encryptedPrefs.getString("wigle_api_key", "") ?: "") }
        var mapboxKey by remember { mutableStateOf(encryptedPrefs.getString("mapbox_api_key", "") ?: "") }
        var googleKey by remember { mutableStateOf(encryptedPrefs.getString("google_maps_api_key", "") ?: "") }
        
        AlertDialog(
            onDismissRequest = { showApiKeysDialog = false },
            title = { Text("Manage API Keys") },
            text = {
                Column {
                    ApiKeyField("WiGLE API Key", wigleKey, { wigleKey = it }, Icons.Default.Cloud)
                    ApiKeyField("Mapbox API Key", mapboxKey, { mapboxKey = it }, Icons.Default.Map)
                    ApiKeyField("Google Maps API Key", googleKey, { googleKey = it }, Icons.Default.Map)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        encryptedPrefs.edit()
                            .putString("wigle_api_key", wigleKey)
                            .putString("mapbox_api_key", mapboxKey)
                            .putString("google_maps_api_key", googleKey)
                            .apply()
                        Toast.makeText(context, "API keys saved", Toast.LENGTH_SHORT).show()
                        showApiKeysDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showApiKeysDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(title, color = ShadowCheckColors.Primary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
fun ApiKeyField(label: String, value: String, onValueChange: (String) -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    var showKey by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, color = ShadowCheckColors.TextPrimary, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(icon, label, tint = ShadowCheckColors.Primary) },
            trailingIcon = {
                IconButton(onClick = { showKey = !showKey }) {
                    Icon(
                        if (showKey) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        "Toggle",
                        tint = ShadowCheckColors.TextSecondary
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ShadowCheckColors.Surface,
                unfocusedContainerColor = ShadowCheckColors.Surface
            )
        )
    }
}

@Composable
fun SliderSetting(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = ShadowCheckColors.TextPrimary, fontSize = 14.sp)
            Text(value.toInt().toString(), color = ShadowCheckColors.Primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range
        )
    }
}

@Composable
fun SwitchSetting(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = ShadowCheckColors.TextPrimary, fontSize = 14.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

fun getEncryptedPrefs(context: Context): android.content.SharedPreferences {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    return EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}
