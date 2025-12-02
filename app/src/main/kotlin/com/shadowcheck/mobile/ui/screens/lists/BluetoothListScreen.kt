package com.shadowcheck.mobile.ui.screens.lists

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.shadowcheck.mobile.models.BluetoothFilters
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothListScreen(
    devices: List<Any>,
    filters: BluetoothFilters,
    onFiltersChange: (BluetoothFilters) -> Unit,
    onDeviceClick: (Any) -> Unit,
    onExport: () -> Unit
) {
    val context = LocalContext.current
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    var btDevices by remember { mutableStateOf<List<ScanResult>>(emptyList()) }
    
    val scanCallback = remember {
        object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                btDevices = (btDevices + result).distinctBy { it.device.address }
            }
        }
    }
    
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            bluetoothManager.adapter?.bluetoothLeScanner?.startScan(scanCallback)
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                bluetoothManager.adapter?.bluetoothLeScanner?.stopScan(scanCallback)
            }
        }
    }
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("Bluetooth Devices (${btDevices.size})") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = ShadowCheckColors.Surface,
                titleContentColor = ShadowCheckColors.TextPrimary
            )
        )
        
        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            items(btDevices) { device ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(device.device.name ?: "Unknown Device", color = ShadowCheckColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Address: ${device.device.address}", color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                        Text("RSSI: ${device.rssi} dBm", color = ShadowCheckColors.Primary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
