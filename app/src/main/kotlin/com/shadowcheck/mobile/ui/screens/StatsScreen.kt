package com.shadowcheck.mobile.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.shadowcheck.mobile.data.ShadowCheckDatabase
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(stats: com.shadowcheck.mobile.ui.screens.NetworkStats) {
    val context = LocalContext.current
    var wifiUnique by remember { mutableStateOf(0) }
    var wifiTotal by remember { mutableStateOf(0) }
    var btUnique by remember { mutableStateOf(0) }
    var btTotal by remember { mutableStateOf(0) }
    var cellUnique by remember { mutableStateOf(0) }
    var cellTotal by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (true) {
            val db = Room.databaseBuilder(context, ShadowCheckDatabase::class.java, "shadowcheck.db").build()
            wifiUnique = db.wifiNetworkDao().getUniqueCount()
            wifiTotal = db.wifiNetworkDao().getTotalCount()
            btUnique = db.bleDeviceDao().getUniqueCount()
            btTotal = db.bleDeviceDao().getTotalCount()
            cellUnique = db.cellularTowerDao().getUniqueCount()
            cellTotal = db.cellularTowerDao().getTotalCount()
            delay(5000)
        }
    }
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("Statistics") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = ShadowCheckColors.Surface,
                titleContentColor = ShadowCheckColors.TextPrimary
            )
        )
        
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            StatCard("WiFi Networks", wifiUnique, wifiTotal)
            Spacer(Modifier.height(12.dp))
            StatCard("Bluetooth Devices", btUnique, btTotal)
            Spacer(Modifier.height(12.dp))
            StatCard("Cellular Towers", cellUnique, cellTotal)
            Spacer(Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Observations", color = ShadowCheckColors.Primary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("${wifiTotal + btTotal + cellTotal}", color = ShadowCheckColors.TextPrimary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, unique: Int, total: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(title, color = ShadowCheckColors.TextSecondary, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text("$unique unique", color = ShadowCheckColors.Primary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text("Total", color = ShadowCheckColors.TextSecondary, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text("$total", color = ShadowCheckColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
