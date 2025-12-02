package com.shadowcheck.mobile.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .blur(10.dp)
    ) {
        content()
    }
}

@Composable
fun FrostedGlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White.copy(alpha = 0.15f),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
    ) {
        content()
    }
}
