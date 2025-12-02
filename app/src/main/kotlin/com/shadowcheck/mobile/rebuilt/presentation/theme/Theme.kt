package com.shadowcheck.mobile.rebuilt.presentation.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Original ShadowCheck Colors
object ShadowCheckColors {
    val Background = Color(0xFF0A1929)
    val Surface = Color(0xFF1A3A4A)
    val Primary = Color(0xFF00BCD4)
    val Secondary = Color(0xFF2196F3)
    val Accent = Color(0xFF4CAF50)
    val Error = Color(0xFF8B0000)
    val TextPrimary = Color.White
    val TextSecondary = Color(0xFF90A4AE)
    val SignalGood = Color(0xFF4CAF50)
    val SignalMedium = Color(0xFFFFA726)
    val SignalWeak = Color(0xFFEF5350)
}

private val DarkColorScheme = darkColorScheme(
    primary = ShadowCheckColors.Primary,
    secondary = ShadowCheckColors.Secondary,
    tertiary = ShadowCheckColors.Accent,
    background = ShadowCheckColors.Background,
    surface = ShadowCheckColors.Surface,
    error = ShadowCheckColors.Error,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = ShadowCheckColors.TextPrimary,
    onSurface = ShadowCheckColors.TextPrimary,
    onError = Color.White
)

@Composable
fun ShadowCheckTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}
