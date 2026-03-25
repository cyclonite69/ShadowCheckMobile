package com.shadowcheck.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowcheck.mobile.presentation.viewmodel.StatsViewModel
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    stats: com.shadowcheck.mobile.ui.screens.NetworkStats,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("Statistics") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = ShadowCheckColors.Surface,
                titleContentColor = ShadowCheckColors.TextPrimary
            )
        )
        
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            StatCard("WiFi Networks", uiState.wifiUnique, uiState.wifiTotal)
            Spacer(Modifier.height(12.dp))
            StatCard("Bluetooth Devices", uiState.btUnique, uiState.btTotal)
            Spacer(Modifier.height(12.dp))
            StatCard("Cellular Towers", uiState.cellUnique, uiState.cellTotal)
            Spacer(Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Observations", color = ShadowCheckColors.Primary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${uiState.wifiTotal + uiState.btTotal + uiState.cellTotal}",
                        color = ShadowCheckColors.TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
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
