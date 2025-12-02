package com.shadowcheck.mobile.ui.screens.lists

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
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
import com.shadowcheck.mobile.models.WiFiFilters
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkListScreen(
    networks: List<Any>,
    filters: WiFiFilters,
    onFiltersChange: (WiFiFilters) -> Unit,
    onNetworkClick: (Any) -> Unit,
    onExport: () -> Unit
) {
    val context = LocalContext.current
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    var wifiNetworks by remember { mutableStateOf<List<ScanResult>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                wifiNetworks = wifiManager.scanResults ?: emptyList()
            }
            delay(3000)
        }
    }
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("WiFi Networks (${wifiNetworks.size})") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = ShadowCheckColors.Surface,
                titleContentColor = ShadowCheckColors.TextPrimary
            )
        )
        
        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            items(wifiNetworks) { network ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(network.SSID.ifEmpty { "Hidden Network" }, color = ShadowCheckColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("BSSID: ${network.BSSID}", color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                        Row {
                            Text("Signal: ${network.level} dBm", color = ShadowCheckColors.Primary, fontSize = 12.sp)
                            Spacer(Modifier.width(16.dp))
                            Text("Freq: ${network.frequency} MHz", color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                        }
                        Text(network.capabilities, color = ShadowCheckColors.TextSecondary, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
