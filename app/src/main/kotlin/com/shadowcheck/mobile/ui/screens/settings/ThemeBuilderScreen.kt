package com.shadowcheck.mobile.ui.screens.settings

import androidx.compose.foundation.background
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import kotlin.math.*

@Composable
fun ThemeBuilderScreen(
    onSaveTheme: (ThemeConfig) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedColor by remember { mutableStateOf<ColorProperty?>(null) }
    var themeConfig by remember { mutableStateOf(ThemeConfig.default()) }
    var showSaved by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        // Load saved theme
        val prefs = context.getSharedPreferences("theme", Context.MODE_PRIVATE)
        themeConfig = ThemeConfig(
            background = Color(prefs.getLong("background", 0xFF0A1929)),
            surface = Color(prefs.getLong("surface", 0xFF1A3A4A)),
            primary = Color(prefs.getLong("primary", 0xFF00BCD4)),
            secondary = Color(prefs.getLong("secondary", 0xFF2196F3)),
            accent = Color(prefs.getLong("accent", 0xFF4CAF50)),
            error = Color(prefs.getLong("error", 0xFF8B0000)),
            textPrimary = Color(prefs.getLong("textPrimary", 0xFFFFFFFF)),
            textSecondary = Color(prefs.getLong("textSecondary", 0xFF90A4AE)),
            titleSize = prefs.getInt("titleSize", 20),
            bodySize = prefs.getInt("bodySize", 16),
            captionSize = prefs.getInt("captionSize", 12)
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShadowCheckColors.Background)
    ) {
        // Header
        Surface(
            color = ShadowCheckColors.Surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.Primary)
                }
                Text(
                    text = "Theme Builder",
                    color = ShadowCheckColors.TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { 
                    // Save theme
                    val prefs = context.getSharedPreferences("theme", Context.MODE_PRIVATE)
                    prefs.edit().apply {
                        putLong("background", themeConfig.background.value.toLong())
                        putLong("surface", themeConfig.surface.value.toLong())
                        putLong("primary", themeConfig.primary.value.toLong())
                        putLong("secondary", themeConfig.secondary.value.toLong())
                        putLong("accent", themeConfig.accent.value.toLong())
                        putLong("error", themeConfig.error.value.toLong())
                        putLong("textPrimary", themeConfig.textPrimary.value.toLong())
                        putLong("textSecondary", themeConfig.textSecondary.value.toLong())
                        putInt("titleSize", themeConfig.titleSize)
                        putInt("bodySize", themeConfig.bodySize)
                        putInt("captionSize", themeConfig.captionSize)
                        apply()
                    }
                    showSaved = true
                    onSaveTheme(themeConfig)
                }) {
                    Icon(Icons.Default.Save, "Save", tint = ShadowCheckColors.Accent)
                }
            }
        }
        
        Row(modifier = Modifier.fillMaxSize()) {
            // Left panel - Color list
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "Colors",
                    color = ShadowCheckColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                ColorPropertyItem("Background", themeConfig.background) {
                    selectedColor = ColorProperty.BACKGROUND
                }
                ColorPropertyItem("Surface", themeConfig.surface) {
                    selectedColor = ColorProperty.SURFACE
                }
                ColorPropertyItem("Primary", themeConfig.primary) {
                    selectedColor = ColorProperty.PRIMARY
                }
                ColorPropertyItem("Secondary", themeConfig.secondary) {
                    selectedColor = ColorProperty.SECONDARY
                }
                ColorPropertyItem("Accent", themeConfig.accent) {
                    selectedColor = ColorProperty.ACCENT
                }
                ColorPropertyItem("Error", themeConfig.error) {
                    selectedColor = ColorProperty.ERROR
                }
                ColorPropertyItem("Text Primary", themeConfig.textPrimary) {
                    selectedColor = ColorProperty.TEXT_PRIMARY
                }
                ColorPropertyItem("Text Secondary", themeConfig.textSecondary) {
                    selectedColor = ColorProperty.TEXT_SECONDARY
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Typography",
                    color = ShadowCheckColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                FontSizeSlider("Title Size", themeConfig.titleSize) {
                    themeConfig = themeConfig.copy(titleSize = it)
                }
                FontSizeSlider("Body Size", themeConfig.bodySize) {
                    themeConfig = themeConfig.copy(bodySize = it)
                }
                FontSizeSlider("Caption Size", themeConfig.captionSize) {
                    themeConfig = themeConfig.copy(captionSize = it)
                }
            }
            
            // Right panel - Color picker
            if (selectedColor != null) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(ShadowCheckColors.Surface)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Color Picker",
                        color = ShadowCheckColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    val currentColor = when (selectedColor) {
                        ColorProperty.BACKGROUND -> themeConfig.background
                        ColorProperty.SURFACE -> themeConfig.surface
                        ColorProperty.PRIMARY -> themeConfig.primary
                        ColorProperty.SECONDARY -> themeConfig.secondary
                        ColorProperty.ACCENT -> themeConfig.accent
                        ColorProperty.ERROR -> themeConfig.error
                        ColorProperty.TEXT_PRIMARY -> themeConfig.textPrimary
                        ColorProperty.TEXT_SECONDARY -> themeConfig.textSecondary
                        else -> Color.White
                    }
                    
                    ColorPicker(
                        color = currentColor,
                        onColorChange = { newColor ->
                            themeConfig = when (selectedColor) {
                                ColorProperty.BACKGROUND -> themeConfig.copy(background = newColor)
                                ColorProperty.SURFACE -> themeConfig.copy(surface = newColor)
                                ColorProperty.PRIMARY -> themeConfig.copy(primary = newColor)
                                ColorProperty.SECONDARY -> themeConfig.copy(secondary = newColor)
                                ColorProperty.ACCENT -> themeConfig.copy(accent = newColor)
                                ColorProperty.ERROR -> themeConfig.copy(error = newColor)
                                ColorProperty.TEXT_PRIMARY -> themeConfig.copy(textPrimary = newColor)
                                ColorProperty.TEXT_SECONDARY -> themeConfig.copy(textSecondary = newColor)
                                else -> themeConfig
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ColorPropertyItem(
    name: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color)
                .border(1.dp, ShadowCheckColors.TextSecondary)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = name,
            color = ShadowCheckColors.TextPrimary,
            fontSize = 16.sp
        )
    }
}

@Composable
fun FontSizeSlider(
    name: String,
    size: Int,
    onSizeChange: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                color = ShadowCheckColors.TextPrimary,
                fontSize = 14.sp
            )
            Text(
                text = "${size}sp",
                color = ShadowCheckColors.Primary,
                fontSize = 14.sp
            )
        }
        Slider(
            value = size.toFloat(),
            onValueChange = { onSizeChange(it.toInt()) },
            valueRange = 10f..40f,
            colors = SliderDefaults.colors(
                thumbColor = ShadowCheckColors.Primary,
                activeTrackColor = ShadowCheckColors.Primary
            )
        )
    }
}

@Composable
fun ColorPicker(
    color: Color,
    onColorChange: (Color) -> Unit
) {
    var hue by remember { mutableStateOf(0f) }
    var saturation by remember { mutableStateOf(1f) }
    var value by remember { mutableStateOf(1f) }
    var alpha by remember { mutableStateOf(1f) }
    
    LaunchedEffect(color) {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(color.hashCode(), hsv)
        hue = hsv[0]
        saturation = hsv[1]
        value = hsv[2]
        alpha = color.alpha
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(color)
                .border(1.dp, ShadowCheckColors.TextSecondary)
        )
        
        // Hue slider
        Text("Hue: ${hue.toInt()}°", color = ShadowCheckColors.TextPrimary)
        Slider(
            value = hue,
            onValueChange = {
                hue = it
                onColorChange(hsvToColor(hue, saturation, value, alpha))
            },
            valueRange = 0f..360f,
            colors = SliderDefaults.colors(
                thumbColor = ShadowCheckColors.Primary,
                activeTrackColor = ShadowCheckColors.Primary
            )
        )
        
        // Saturation slider
        Text("Saturation: ${(saturation * 100).toInt()}%", color = ShadowCheckColors.TextPrimary)
        Slider(
            value = saturation,
            onValueChange = {
                saturation = it
                onColorChange(hsvToColor(hue, saturation, value, alpha))
            },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = ShadowCheckColors.Primary,
                activeTrackColor = ShadowCheckColors.Primary
            )
        )
        
        // Value slider
        Text("Brightness: ${(value * 100).toInt()}%", color = ShadowCheckColors.TextPrimary)
        Slider(
            value = value,
            onValueChange = {
                value = it
                onColorChange(hsvToColor(hue, saturation, value, alpha))
            },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = ShadowCheckColors.Primary,
                activeTrackColor = ShadowCheckColors.Primary
            )
        )
        
        // Alpha slider
        Text("Opacity: ${(alpha * 100).toInt()}%", color = ShadowCheckColors.TextPrimary)
        Slider(
            value = alpha,
            onValueChange = {
                alpha = it
                onColorChange(hsvToColor(hue, saturation, value, alpha))
            },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = ShadowCheckColors.Primary,
                activeTrackColor = ShadowCheckColors.Primary
            )
        )
        
        // Hex input
        Text(
            text = "Hex: #${color.toHexString()}",
            color = ShadowCheckColors.Primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun hsvToColor(hue: Float, saturation: Float, value: Float, alpha: Float): Color {
    val hsv = floatArrayOf(hue, saturation, value)
    val rgb = android.graphics.Color.HSVToColor((alpha * 255).toInt(), hsv)
    return Color(rgb)
}

fun Color.toHexString(): String {
    val a = (alpha * 255).toInt()
    val r = (red * 255).toInt()
    val g = (green * 255).toInt()
    val b = (blue * 255).toInt()
    return "%02X%02X%02X%02X".format(a, r, g, b)
}

enum class ColorProperty {
    BACKGROUND, SURFACE, PRIMARY, SECONDARY, ACCENT, ERROR, TEXT_PRIMARY, TEXT_SECONDARY
}

data class ThemeConfig(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val error: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val titleSize: Int,
    val bodySize: Int,
    val captionSize: Int
) {
    companion object {
        fun default() = ThemeConfig(
            background = ShadowCheckColors.Background,
            surface = ShadowCheckColors.Surface,
            primary = ShadowCheckColors.Primary,
            secondary = ShadowCheckColors.Secondary,
            accent = ShadowCheckColors.Accent,
            error = ShadowCheckColors.Error,
            textPrimary = ShadowCheckColors.TextPrimary,
            textSecondary = ShadowCheckColors.TextSecondary,
            titleSize = 20,
            bodySize = 16,
            captionSize = 12
        )
    }
}
