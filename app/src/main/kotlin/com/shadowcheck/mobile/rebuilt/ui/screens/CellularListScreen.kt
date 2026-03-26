package com.shadowcheck.mobile.rebuilt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowcheck.mobile.core.model.CellularTower
import com.shadowcheck.mobile.presentation.viewmodel.CellularListViewModel
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CellularListScreen(
    viewModel: CellularListViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onTowerClick: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("Cell Towers (${uiState.distinctCount} distinct)", color = ShadowCheckColors.Primary, fontSize = 18.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.TextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadowCheckColors.Surface)
        )
        
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(uiState.towers) { tower ->
                CellularCard(
                    tower = tower,
                    sightings = uiState.sightingsCounts[tower.cellId] ?: 0,
                    onClick = { onTowerClick(tower.cellId) }
                )
            }
        }
    }
}

@Composable
fun CellularCard(tower: CellularTower, sightings: Int, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Cell ID: ${tower.cellId}", fontWeight = FontWeight.Bold, color = Color.White)
            Text("MCC: ${tower.mcc} MNC: ${tower.mnc}", fontSize = 12.sp, color = ShadowCheckColors.TextSecondary)
            Text("$sightings sightings", fontSize = 10.sp, color = ShadowCheckColors.Accent)
        }
    }
}
