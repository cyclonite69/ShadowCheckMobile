package com.shadowcheck.mobile.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@Composable
fun PulsingDot(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Box(
        modifier = modifier
            .size(12.dp)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(12.dp),
            strokeWidth = 2.dp,
            color = ShadowCheckColors.Primary
        )
    }
}

@Composable
fun FadeInText(
    text: String,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(1000))
    ) {
        Text(text = text, modifier = modifier)
    }
}

@Composable
fun SlideInFromBottom(
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(500)
        ) + fadeIn()
    ) {
        content()
    }
}

@Composable
fun ExpandableCard(
    expanded: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        content()
    }
}
