package com.shadowcheck.mobile.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.rainbowShimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    val shimmerColors = listOf(
        Color(0xFF00BCD4).copy(alpha = 0.3f),
        Color(0xFF2196F3).copy(alpha = 0.3f),
        Color(0xFF4CAF50).copy(alpha = 0.3f),
        Color(0xFF00BCD4).copy(alpha = 0.3f)
    )
    
    background(
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(progress * 1000f, 0f),
            end = Offset(progress * 1000f + 500f, 500f)
        )
    )
}
