package com.shadowcheck.mobile.rebuilt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.shadowcheck.mobile.data.ShadowCheckDatabase
import com.shadowcheck.mobile.data.BleDevice
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothListScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    var devices by remember { mutableStateOf<List<BleDevice>>(emptyList()) }
    var distinctCount by remember { mutableStateOf(0) }
    var sightingsCounts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    
    LaunchedEffect(Unit) {
        val db = Room.databaseBuilder(context, ShadowCheckDatabase::class.java, "shadowcheck.db").build()
        db.bleDeviceDao().getDistinctFlow().collect { list ->
            devices = list
            distinctCount = db.bleDeviceDao().getUniqueCount()
            sightingsCounts = list.associate { it.address to db.bleDeviceDao().getSightingsCount(it.address) }
        }
    }
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("Bluetooth Devices ($distinctCount distinct)", color = ShadowCheckColors.Primary, fontSize = 18.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.TextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadowCheckColors.Surface)
        )
        
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            items(devices) { device ->
                BluetoothCard(device, sightingsCounts[device.address] ?: 1)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun BluetoothCard(device: BleDevice, sightings: Int) {
    val signalColor = when {
        device.rssi > -60 -> Color(0xFF4CAF50)
        device.rssi > -70 -> Color(0xFFFFC107)
        device.rssi > -80 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface.copy(alpha = 0.7f)),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = device.name ?: "Unknown",
                        color = ShadowCheckColors.Primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFFFF9800),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "BLE",
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = device.address,
                    color = ShadowCheckColors.TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "$sightings sightings",
                    color = ShadowCheckColors.TextSecondary,
                    fontSize = 12.sp
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = device.rssi.toString(),
                    color = signalColor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "dBm",
                    color = ShadowCheckColors.TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}
