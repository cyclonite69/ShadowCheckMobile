package com.shadowcheck.mobile.rebuilt.ui.theme

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ElementStyle(
    val cardHeight: Dp = 140.dp,
    val cardWidth: Dp = 100.dp,
    val fontSize: TextUnit = 14.sp,
    val iconSize: Dp = 24.dp,
    val padding: Dp = 16.dp,
    val margin: Dp = 8.dp,
    val cornerRadius: Dp = 12.dp,
    val elevation: Dp = 4.dp,
    val backgroundColor: Color = Color.Transparent,
    val textColor: Color = Color.White,
    // Layout positioning
    val horizontalAlignment: String = "Center", // Start, Center, End
    val verticalAlignment: String = "Top", // Top, Center, Bottom
    val arrangement: String = "SpaceEvenly", // Start, Center, End, SpaceBetween, SpaceEvenly, SpaceAround
    val itemSpacing: Dp = 8.dp,
    val offsetX: Dp = 0.dp,
    val offsetY: Dp = 0.dp
)

data class ThemeConfig(
    // Colors
    val primaryColor: Color = Color(0xFF00BCD4),
    val accentColor: Color = Color(0xFF4CAF50),
    val surfaceColor: Color = Color(0xFF1E3A3A),
    val backgroundColor: Color = Color(0xFF0D1B2A),
    val errorColor: Color = Color(0xFFF44336),
    val textPrimaryColor: Color = Color(0xFFFFFFFF),
    val textSecondaryColor: Color = Color(0xFFB0BEC5),
    
    // Button styles
    val buttonCornerRadius: Dp = 8.dp,
    val buttonElevation: Dp = 4.dp,
    val buttonPressedElevation: Dp = 8.dp,
    val buttonTextSize: TextUnit = 14.sp,
    val buttonPadding: Dp = 16.dp,
    val buttonBorderWidth: Dp = 1.dp,
    val buttonBorderColor: Color = Color(0xFF00BCD4),
    
    // Card styles
    val cardCornerRadius: Dp = 12.dp,
    val cardElevation: Dp = 4.dp,
    val cardPadding: Dp = 16.dp,
    val cardBorderWidth: Dp = 0.dp,
    val cardBorderColor: Color = Color.Transparent,
    
    // Typography
    val fontFamily: String = "Default",
    val headingSize: TextUnit = 24.sp,
    val bodySize: TextUnit = 14.sp,
    val captionSize: TextUnit = 12.sp,
    val lineHeight: TextUnit = 20.sp,
    
    // Spacing
    val spacingSmall: Dp = 8.dp,
    val spacingMedium: Dp = 16.dp,
    val spacingLarge: Dp = 24.dp,
    
    // Icons - which icon to use for each element
    val wifiIcon: String = "Wifi",
    val bluetoothIcon: String = "Bluetooth",
    val cellularIcon: String = "CellTower",
    val mapIcon: String = "Map",
    val homeIcon: String = "Home",
    val settingsIcon: String = "Settings",
    val searchIcon: String = "Search",
    val starIcon: String = "Star",
    val playIcon: String = "PlayArrow",
    
    // Icon styling
    val iconSize: Dp = 24.dp,
    val iconColor: Color = Color(0xFF00BCD4),
    
    // Animations
    val animationDuration: Int = 300,
    val enableAnimations: Boolean = true,
    
    // Mode
    val isDarkMode: Boolean = true,
    
    // Per-screen element customization
    val homeScreenCards: ElementStyle = ElementStyle(cardHeight = 140.dp, cardWidth = 100.dp),
    val listScreenCards: ElementStyle = ElementStyle(cardHeight = 100.dp, cardWidth = 0.dp),
    val detailScreenCards: ElementStyle = ElementStyle(cardHeight = 120.dp, cardWidth = 0.dp),
    val sidebarItems: ElementStyle = ElementStyle(cardHeight = 48.dp, fontSize = 16.sp, padding = 12.dp),
    val topBarStyle: ElementStyle = ElementStyle(cardHeight = 64.dp, fontSize = 18.sp, padding = 16.dp)
) {
    fun save(context: Context) {
        val prefs = context.getSharedPreferences("theme_config", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putInt("primaryColor", primaryColor.hashCode())
            putInt("accentColor", accentColor.hashCode())
            putInt("surfaceColor", surfaceColor.hashCode())
            putInt("backgroundColor", backgroundColor.hashCode())
            putInt("errorColor", errorColor.hashCode())
            putInt("textPrimaryColor", textPrimaryColor.hashCode())
            putInt("textSecondaryColor", textSecondaryColor.hashCode())
            
            putFloat("buttonCornerRadius", buttonCornerRadius.value)
            putFloat("buttonElevation", buttonElevation.value)
            putFloat("buttonPressedElevation", buttonPressedElevation.value)
            putFloat("buttonTextSize", buttonTextSize.value)
            putFloat("buttonPadding", buttonPadding.value)
            putFloat("buttonBorderWidth", buttonBorderWidth.value)
            putInt("buttonBorderColor", buttonBorderColor.hashCode())
            
            putFloat("cardCornerRadius", cardCornerRadius.value)
            putFloat("cardElevation", cardElevation.value)
            putFloat("cardPadding", cardPadding.value)
            
            putString("fontFamily", fontFamily)
            putFloat("headingSize", headingSize.value)
            putFloat("bodySize", bodySize.value)
            putFloat("captionSize", captionSize.value)
            
            putFloat("spacingSmall", spacingSmall.value)
            putFloat("spacingMedium", spacingMedium.value)
            putFloat("spacingLarge", spacingLarge.value)
            
            putString("wifiIcon", wifiIcon)
            putString("bluetoothIcon", bluetoothIcon)
            putString("cellularIcon", cellularIcon)
            putString("mapIcon", mapIcon)
            putString("homeIcon", homeIcon)
            putString("settingsIcon", settingsIcon)
            putString("searchIcon", searchIcon)
            putString("starIcon", starIcon)
            putString("playIcon", playIcon)
            
            putFloat("iconSize", iconSize.value)
            putInt("iconColor", iconColor.hashCode())
            
            putInt("animationDuration", animationDuration)
            putBoolean("enableAnimations", enableAnimations)
            putBoolean("isDarkMode", isDarkMode)
            
            apply()
        }
    }
    
    companion object {
        fun load(context: Context): ThemeConfig {
            val prefs = context.getSharedPreferences("theme_config", Context.MODE_PRIVATE)
            return ThemeConfig(
                primaryColor = Color(prefs.getInt("primaryColor", 0xFF00BCD4.toInt())),
                accentColor = Color(prefs.getInt("accentColor", 0xFF4CAF50.toInt())),
                surfaceColor = Color(prefs.getInt("surfaceColor", 0xFF1E3A3A.toInt())),
                backgroundColor = Color(prefs.getInt("backgroundColor", 0xFF0D1B2A.toInt())),
                errorColor = Color(prefs.getInt("errorColor", 0xFFF44336.toInt())),
                textPrimaryColor = Color(prefs.getInt("textPrimaryColor", 0xFFFFFFFF.toInt())),
                textSecondaryColor = Color(prefs.getInt("textSecondaryColor", 0xFFB0BEC5.toInt())),
                
                buttonCornerRadius = prefs.getFloat("buttonCornerRadius", 8f).dp,
                buttonElevation = prefs.getFloat("buttonElevation", 4f).dp,
                buttonPressedElevation = prefs.getFloat("buttonPressedElevation", 8f).dp,
                buttonTextSize = prefs.getFloat("buttonTextSize", 14f).sp,
                buttonPadding = prefs.getFloat("buttonPadding", 16f).dp,
                buttonBorderWidth = prefs.getFloat("buttonBorderWidth", 1f).dp,
                buttonBorderColor = Color(prefs.getInt("buttonBorderColor", 0xFF00BCD4.toInt())),
                
                cardCornerRadius = prefs.getFloat("cardCornerRadius", 12f).dp,
                cardElevation = prefs.getFloat("cardElevation", 4f).dp,
                cardPadding = prefs.getFloat("cardPadding", 16f).dp,
                
                fontFamily = prefs.getString("fontFamily", "Default") ?: "Default",
                headingSize = prefs.getFloat("headingSize", 24f).sp,
                bodySize = prefs.getFloat("bodySize", 14f).sp,
                captionSize = prefs.getFloat("captionSize", 12f).sp,
                
                spacingSmall = prefs.getFloat("spacingSmall", 8f).dp,
                spacingMedium = prefs.getFloat("spacingMedium", 16f).dp,
                spacingLarge = prefs.getFloat("spacingLarge", 24f).dp,
                
                wifiIcon = prefs.getString("wifiIcon", "Wifi") ?: "Wifi",
                bluetoothIcon = prefs.getString("bluetoothIcon", "Bluetooth") ?: "Bluetooth",
                cellularIcon = prefs.getString("cellularIcon", "CellTower") ?: "CellTower",
                mapIcon = prefs.getString("mapIcon", "Map") ?: "Map",
                homeIcon = prefs.getString("homeIcon", "Home") ?: "Home",
                settingsIcon = prefs.getString("settingsIcon", "Settings") ?: "Settings",
                searchIcon = prefs.getString("searchIcon", "Search") ?: "Search",
                starIcon = prefs.getString("starIcon", "Star") ?: "Star",
                playIcon = prefs.getString("playIcon", "PlayArrow") ?: "PlayArrow",
                
                iconSize = prefs.getFloat("iconSize", 24f).dp,
                iconColor = Color(prefs.getInt("iconColor", 0xFF00BCD4.toInt())),
                
                animationDuration = prefs.getInt("animationDuration", 300),
                enableAnimations = prefs.getBoolean("enableAnimations", true),
                isDarkMode = prefs.getBoolean("isDarkMode", true)
            )
        }
    }
}
