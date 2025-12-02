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
import com.shadowcheck.mobile.data.CellularTower
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CellularListScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    var towers by remember { mutableStateOf<List<CellularTower>>(emptyList()) }
    var distinctCount by remember { mutableStateOf(0) }
    var sightingsCounts by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    
    LaunchedEffect(Unit) {
        val db = Room.databaseBuilder(context, ShadowCheckDatabase::class.java, "shadowcheck.db").build()
        db.cellularTowerDao().getDistinctFlow().collect { list ->
            towers = list
            distinctCount = db.cellularTowerDao().getUniqueCount()
            sightingsCounts = list.associate { it.cellId to db.cellularTowerDao().getSightingsCount(it.cellId) }
        }
    }
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("Cell Towers ($distinctCount distinct)", color = ShadowCheckColors.Primary, fontSize = 18.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.TextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadowCheckColors.Surface)
        )
        
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            items(towers) { tower ->
                CellularCard(tower, sightingsCounts[tower.cellId] ?: 1)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CellularCard(tower: CellularTower, sightings: Int) {
    val signalColor = when {
        tower.signalStrength > 3 -> Color(0xFF4CAF50)
        tower.signalStrength > 2 -> Color(0xFFFFC107)
        tower.signalStrength > 1 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
    
    val badgeColor = when (tower.networkType) {
        "LTE" -> Color(0xFFE91E63)
        "NR" -> Color(0xFF9C27B0)
        else -> Color(0xFF607D8B)
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
                        text = "Cell ${tower.cellId}",
                        color = ShadowCheckColors.Primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = badgeColor,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = tower.networkType ?: "GSM",
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "MCC: ${tower.mcc} MNC: ${tower.mnc} LAC: ${tower.lac}",
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
                    text = "-${100 - (tower.signalStrength * 20)}",
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
