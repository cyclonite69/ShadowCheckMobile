package com.shadowcheck.mobile.ui.screens.security

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shadowcheck.mobile.data.WifiNetwork
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@Composable
fun ChannelAnalysisScreen(networks: List<WifiNetwork>) {
    val channelData2_4 = analyzeChannels(networks, 2400..2500)
    val channelData5 = analyzeChannels(networks, 5000..5900)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Surface(
            color = ShadowCheckColors.Surface,
            tonalElevation = 2.dp
        ) {
            Text(
                text = "Channel Analysis",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2.4 GHz
            ChannelBandCard(
                title = "2.4 GHz Band",
                subtitle = "Channels 1-14",
                channelData = channelData2_4,
                recommendedChannels = listOf(1, 6, 11)
            )
            
            // 5 GHz
            ChannelBandCard(
                title = "5 GHz Band",
                subtitle = "Channels 36-165",
                channelData = channelData5,
                recommendedChannels = listOf(36, 40, 44, 48, 149, 153, 157, 161)
            )
        }
    }
}

@Composable
fun ChannelBandCard(
    title: String,
    subtitle: String,
    channelData: Map<Int, Int>,
    recommendedChannels: List<Int>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = ShadowCheckColors.TextSecondary
            )
            
            Spacer(Modifier.height(16.dp))
            
            ChannelGraph(
                channelData = channelData,
                recommendedChannels = recommendedChannels,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Best channels
            val bestChannels = findBestChannels(channelData, recommendedChannels, 3)
            Text(
                text = "Recommended Channels:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = bestChannels.joinToString(", ") { "Channel $it" },
                style = MaterialTheme.typography.bodyMedium,
                color = ShadowCheckColors.SignalGood
            )
        }
    }
}

@Composable
fun ChannelGraph(
    channelData: Map<Int, Int>,
    recommendedChannels: List<Int>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 40f
        
        val graphWidth = width - padding * 2
        val graphHeight = height - padding * 2
        
        if (channelData.isEmpty()) return@Canvas
        
        val maxCount = channelData.values.maxOrNull() ?: 1
        val channels = channelData.keys.sorted()
        val barWidth = graphWidth / channels.size
        
        // Draw bars
        channels.forEachIndexed { index, channel ->
            val count = channelData[channel] ?: 0
            val barHeight = (count.toFloat() / maxCount) * graphHeight
            val x = padding + index * barWidth
            val y = padding + graphHeight - barHeight
            
            val color = when {
                count == 0 -> Color(0xFF4CAF50)
                count <= maxCount * 0.3f -> Color(0xFF8BC34A)
                count <= maxCount * 0.6f -> Color(0xFFFFC107)
                else -> Color(0xFFF44336)
            }
            
            drawRect(
                color = color,
                topLeft = Offset(x + 2, y),
                size = Size(barWidth - 4, barHeight)
            )
            
            // Highlight recommended channels
            if (channel in recommendedChannels) {
                drawRect(
                    color = Color(0xFF2196F3),
                    topLeft = Offset(x + 2, y),
                    size = Size(barWidth - 4, barHeight),
                    alpha = 0.3f
                )
            }
        }
        
        // Draw axes
        drawLine(
            color = Color.Gray,
            start = Offset(padding, padding + graphHeight),
            end = Offset(padding + graphWidth, padding + graphHeight),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Gray,
            start = Offset(padding, padding),
            end = Offset(padding, padding + graphHeight),
            strokeWidth = 2f
        )
    }
}

private fun analyzeChannels(networks: List<WifiNetwork>, frequencyRange: IntRange): Map<Int, Int> {
    return networks
        .filter { it.frequency in frequencyRange }
        .groupBy { it.channel }
        .mapValues { it.value.size }
}

private fun findBestChannels(
    channelData: Map<Int, Int>,
    recommendedChannels: List<Int>,
    count: Int
): List<Int> {
    return recommendedChannels
        .sortedBy { channelData[it] ?: 0 }
        .take(count)
}
