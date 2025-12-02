package com.shadowcheck.mobile.rebuilt.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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
fun ChannelDistributionScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    var channelCounts by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var totalNetworks by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        val db = Room.databaseBuilder(context, ShadowCheckDatabase::class.java, "shadowcheck.db").build()
        while (true) {
            db.wifiNetworkDao().getDistinctFlow().collect { networks ->
                totalNetworks = networks.size
                channelCounts = networks.groupBy { getChannelFromFreq(it.frequency) }
                    .mapValues { it.value.size }
            }
            delay(3000)
        }
    }
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("Channel Distribution", color = ShadowCheckColors.Primary, fontSize = 18.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.TextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadowCheckColors.Surface)
        )
        
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Text(
                "2.4 GHz Band",
                color = ShadowCheckColors.Primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            ChannelGraph(
                channels = (1..14).toList(),
                counts = channelCounts,
                maxCount = channelCounts.values.maxOrNull() ?: 1
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "5 GHz Band",
                color = ShadowCheckColors.Primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            ChannelGraph(
                channels = listOf(36, 40, 44, 48, 52, 56, 60, 64, 100, 104, 108, 112, 116, 120, 124, 128, 132, 136, 140, 144, 149, 153, 157, 161, 165),
                counts = channelCounts,
                maxCount = channelCounts.values.maxOrNull() ?: 1
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Summary", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ShadowCheckColors.Primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total Networks: $totalNetworks", color = ShadowCheckColors.TextPrimary)
                    Text("Most Congested: Channel ${channelCounts.maxByOrNull { it.value }?.key ?: "N/A"}", color = ShadowCheckColors.TextPrimary)
                    Text("Least Congested: Channel ${channelCounts.minByOrNull { it.value }?.key ?: "N/A"}", color = ShadowCheckColors.TextPrimary)
                }
            }
        }
    }
}

@Composable
fun ChannelGraph(channels: List<Int>, counts: Map<Int, Int>, maxCount: Int) {
    Column {
        Canvas(
            modifier = Modifier.fillMaxWidth().height(180.dp).background(ShadowCheckColors.Surface.copy(alpha = 0.3f))
        ) {
            val barWidth = size.width / channels.size
            val maxHeight = size.height
            
            channels.forEachIndexed { index, channel ->
                val count = counts[channel] ?: 0
                val barHeight = if (maxCount > 0) (count.toFloat() / maxCount) * maxHeight else 0f
                
                val barColor = when {
                    count == 0 -> Color.Gray
                    count < maxCount * 0.3 -> Color(0xFF4CAF50)
                    count < maxCount * 0.6 -> Color(0xFFFFC107)
                    else -> Color(0xFFF44336)
                }
                
                drawRect(
                    color = barColor,
                    topLeft = Offset(index * barWidth + 2, size.height - barHeight),
                    size = Size(barWidth - 4, barHeight)
                )
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            channels.forEach { channel ->
                Text(
                    text = channel.toString(),
                    fontSize = 10.sp,
                    color = ShadowCheckColors.TextSecondary,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

private fun getChannelFromFreq(freq: Int): Int {
    return when (freq) {
        in 2412..2484 -> (freq - 2407) / 5
        in 5170..5825 -> when (freq) {
            5180 -> 36; 5200 -> 40; 5220 -> 44; 5240 -> 48
            5260 -> 52; 5280 -> 56; 5300 -> 60; 5320 -> 64
            5500 -> 100; 5520 -> 104; 5540 -> 108; 5560 -> 112
            5580 -> 116; 5600 -> 120; 5620 -> 124; 5640 -> 128
            5660 -> 132; 5680 -> 136; 5700 -> 140; 5720 -> 144
            5745 -> 149; 5765 -> 153; 5785 -> 157; 5805 -> 161; 5825 -> 165
            else -> 0
        }
        else -> 0
    }
}
