package com.shadowcheck.mobile.rebuilt.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowcheck.mobile.presentation.viewmodel.HeatmapViewModel
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatmapScreen(
    viewModel: HeatmapViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("Heatmap", color = ShadowCheckColors.Primary, fontSize = 18.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.TextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadowCheckColors.Surface)
        )
        
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { viewModel.selectLayer("wifi") }) {
                Text("WiFi")
            }
            Button(onClick = { viewModel.selectLayer("bluetooth") }) {
                Text("Bluetooth")
            }
            Button(onClick = { viewModel.selectLayer("cellular") }) {
                Text("Cellular")
            }
        }
        
        val heatmapData = when (uiState.selectedLayer) {
            "wifi" -> uiState.wifiHeatmap
            "bluetooth" -> uiState.btHeatmap
            "cellular" -> uiState.cellHeatmap
            else -> uiState.wifiHeatmap
        }
        
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            heatmapData.forEach { point ->
                drawCircle(
                    color = Color(0xFFFF5722).copy(alpha = point.intensity.toFloat()),
                    radius = 20f,
                    center = Offset(point.x.toFloat(), point.y.toFloat())
                )
            }
        }
    }
}
