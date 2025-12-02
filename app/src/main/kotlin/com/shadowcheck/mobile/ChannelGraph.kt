package com.shadowcheck.mobile
import androidx.compose.ui.graphics.nativeCanvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@Composable
fun ChannelBandGraph(
    title: String,
    subtitle: String,
    channelData: List<Triple<Int, Int, Int>>, // channel, count, maxSignal
    channelRange: IntRange,
    bandColor: Color,
    recommendedChannels: List<Int> = emptyList()
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = bandColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = ShadowCheckColors.TextSecondary
            )
            
            Spacer(Modifier.height(16.dp))
            
            ChannelGraph(
                channelData = channelData,
                channelRange = channelRange,
                bandColor = bandColor,
                recommendedChannels = recommendedChannels,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LegendItem("Low", ShadowCheckColors.SignalGood)
                LegendItem("Medium", ShadowCheckColors.SignalMedium)
                LegendItem("High", ShadowCheckColors.Error)
                if (recommendedChannels.isNotEmpty()) {
                    LegendItem("Recommended", bandColor)
                }
            }
        }
    }
}

@Composable
fun ChannelGraph(
    channelData: List<Triple<Int, Int, Int>>,
    channelRange: IntRange,
    bandColor: Color,
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
        
        val maxCount = channelData.maxOfOrNull { it.second } ?: 1
        val barCount = channelRange.count()
        val barWidth = graphWidth / barCount
        
        // Draw Y-axis labels
        for (i in 0..5) {
            val y = padding + graphHeight - (i * graphHeight / 5)
            val label = (maxCount * i / 5).toString()
            
            drawContext.canvas.nativeCanvas.drawText(
                label,
                5f,
                y,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 24f
                }
            )
        }
        
        // Draw bars
        channelRange.forEachIndexed { index, channel ->
            val data = channelData.find { it.first == channel }
            val count = data?.second ?: 0
            
            if (count > 0) {
                val barHeight = (count.toFloat() / maxCount) * graphHeight
                val x = padding + index * barWidth
                val y = padding + graphHeight - barHeight
                
                val color = when {
                    count > maxCount * 0.7f -> Color(0xFFF44336)
                    count > maxCount * 0.4f -> Color(0xFFFFC107)
                    else -> Color(0xFF4CAF50)
                }
                
                // Draw bar with gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(color.copy(alpha = 0.8f), color.copy(alpha = 0.4f))
                    ),
                    topLeft = Offset(x + 2, y),
                    size = Size(barWidth - 4, barHeight)
                )
                
                // Highlight recommended channels
                if (channel in recommendedChannels) {
                    drawRect(
                        color = bandColor,
                        topLeft = Offset(x + 2, y),
                        size = Size(barWidth - 4, barHeight),
                        alpha = 0.3f
                    )
                }
            }
            
            // Draw channel labels
            if (barCount <= 14 || index % 5 == 0) {
                drawContext.canvas.nativeCanvas.drawText(
                    channel.toString(),
                    padding + (index + 0.5f) * barWidth - 8,
                    padding + graphHeight + 30,
                    android.graphics.Paint().apply {
                        color = if (channel in recommendedChannels) 
                            android.graphics.Color.CYAN 
                        else 
                            android.graphics.Color.GRAY
                        textSize = 24f
                    }
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

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            modifier = Modifier.size(16.dp),
            color = color,
            shape = MaterialTheme.shapes.small
        ) {}
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
