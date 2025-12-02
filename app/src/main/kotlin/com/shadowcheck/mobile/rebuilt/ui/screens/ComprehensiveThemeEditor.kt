package com.shadowcheck.mobile.rebuilt.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import com.shadowcheck.mobile.rebuilt.ui.theme.ElementStyle
import com.shadowcheck.mobile.rebuilt.ui.theme.ThemeConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComprehensiveThemeEditor(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    var config by remember { mutableStateOf(ThemeConfig.load(context)) }
    var selectedTab by remember { mutableStateOf(0) }
    var sidebarCollapsed by remember { mutableStateOf(false) }
    
    // Auto-save on change
    LaunchedEffect(config) {
        config.save(context)
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Editor Panel - collapsible
            if (!sidebarCollapsed) {
                Column(
                    modifier = Modifier.width(300.dp).fillMaxHeight()
                        .background(Color(0xFF1A1A1A).copy(alpha = 0.95f))
                        .verticalScroll(rememberScrollState()).padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                            }
                            Text("Theme Studio", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { sidebarCollapsed = true }) {
                            Icon(Icons.Default.ChevronLeft, "Collapse", tint = Color.White)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TabRow(selectedTabIndex = selectedTab, modifier = Modifier.fillMaxWidth()) {
                        Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Colors", fontSize = 9.sp) })
                        Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Buttons", fontSize = 9.sp) })
                        Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Cards", fontSize = 9.sp) })
                        Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Text", fontSize = 9.sp) })
                        Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text("Icons", fontSize = 9.sp) })
                        Tab(selected = selectedTab == 5, onClick = { selectedTab = 5 }, text = { Text("Elements", fontSize = 9.sp) })
                        Tab(selected = selectedTab == 6, onClick = { selectedTab = 6 }, text = { Text("More", fontSize = 9.sp) })
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    when (selectedTab) {
                        0 -> ColorsTab(config) { config = it }
                        1 -> ButtonsTab(config) { config = it }
                        2 -> CardsTab(config) { config = it }
                        3 -> TypographyTab(config) { config = it }
                        4 -> IconsTab(config) { config = it }
                        5 -> ElementsTab(config) { config = it }
                        6 -> MoreTab(config, context) { config = it }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(onClick = { config = ThemeConfig(); config.save(context) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Reset to Default")
                    }
                }
            }
            
            // Live Preview Panel (remaining space)
            Box(modifier = Modifier.fillMaxSize()) {
                LivePreview(config, Modifier.fillMaxSize())
                
                // Expand button when collapsed
                if (sidebarCollapsed) {
                    IconButton(
                        onClick = { sidebarCollapsed = false },
                        modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                            .background(Color(0xFF1A1A1A).copy(alpha = 0.8f), shape = MaterialTheme.shapes.small)
                    ) {
                        Icon(Icons.Default.ChevronRight, "Expand", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ColorsTab(config: ThemeConfig, onChange: (ThemeConfig) -> Unit) {
    Column {
        Text("Colors", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        ColorPicker("Primary", config.primaryColor) { onChange(config.copy(primaryColor = it)) }
        ColorPicker("Accent", config.accentColor) { onChange(config.copy(accentColor = it)) }
        ColorPicker("Surface", config.surfaceColor) { onChange(config.copy(surfaceColor = it)) }
        ColorPicker("Background", config.backgroundColor) { onChange(config.copy(backgroundColor = it)) }
        ColorPicker("Error", config.errorColor) { onChange(config.copy(errorColor = it)) }
        ColorPicker("Text Primary", config.textPrimaryColor) { onChange(config.copy(textPrimaryColor = it)) }
        ColorPicker("Text Secondary", config.textSecondaryColor) { onChange(config.copy(textSecondaryColor = it)) }
    }
}

@Composable
fun ButtonsTab(config: ThemeConfig, onChange: (ThemeConfig) -> Unit) {
    Column {
        Text("Button Styling", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        SliderControl("Corner Radius", config.buttonCornerRadius.value, 0f, 50f) { 
            onChange(config.copy(buttonCornerRadius = it.dp)) 
        }
        SliderControl("Elevation", config.buttonElevation.value, 0f, 20f) { 
            onChange(config.copy(buttonElevation = it.dp)) 
        }
        SliderControl("Pressed Elevation", config.buttonPressedElevation.value, 0f, 20f) { 
            onChange(config.copy(buttonPressedElevation = it.dp)) 
        }
        SliderControl("Text Size", config.buttonTextSize.value, 10f, 24f) { 
            onChange(config.copy(buttonTextSize = it.sp)) 
        }
        SliderControl("Padding", config.buttonPadding.value, 8f, 32f) { 
            onChange(config.copy(buttonPadding = it.dp)) 
        }
        SliderControl("Border Width", config.buttonBorderWidth.value, 0f, 5f) { 
            onChange(config.copy(buttonBorderWidth = it.dp)) 
        }
        ColorPicker("Border Color", config.buttonBorderColor) { onChange(config.copy(buttonBorderColor = it)) }
    }
}

@Composable
fun CardsTab(config: ThemeConfig, onChange: (ThemeConfig) -> Unit) {
    Column {
        Text("Card Styling", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        SliderControl("Corner Radius", config.cardCornerRadius.value, 0f, 50f) { 
            onChange(config.copy(cardCornerRadius = it.dp)) 
        }
        SliderControl("Elevation", config.cardElevation.value, 0f, 20f) { 
            onChange(config.copy(cardElevation = it.dp)) 
        }
        SliderControl("Padding", config.cardPadding.value, 8f, 32f) { 
            onChange(config.copy(cardPadding = it.dp)) 
        }
        SliderControl("Border Width", config.cardBorderWidth.value, 0f, 5f) { 
            onChange(config.copy(cardBorderWidth = it.dp)) 
        }
        ColorPicker("Border Color", config.cardBorderColor) { onChange(config.copy(cardBorderColor = it)) }
    }
}

@Composable
fun IconsTab(config: ThemeConfig, onChange: (ThemeConfig) -> Unit) {
    val iconOptions = listOf("Wifi", "WifiTethering", "SignalWifi4Bar", "Router", "Bluetooth", "BluetoothConnected", "BluetoothSearching", 
        "CellTower", "SignalCellular4Bar", "NetworkCell", "Map", "MapOutlined", "Explore", "Home", "HomeOutlined", "House",
        "Settings", "SettingsOutlined", "Tune", "Search", "SearchOutlined", "FindInPage", "Star", "StarOutline", "StarBorder",
        "PlayArrow", "PlayCircle", "PlayCircleOutline", "Favorite", "FavoriteBorder", "LocationOn", "LocationSearching", "MyLocation")
    
    Column {
        Text("Icon Selection", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        IconPicker("WiFi Icon", config.wifiIcon, iconOptions) { onChange(config.copy(wifiIcon = it)) }
        IconPicker("Bluetooth Icon", config.bluetoothIcon, iconOptions) { onChange(config.copy(bluetoothIcon = it)) }
        IconPicker("Cellular Icon", config.cellularIcon, iconOptions) { onChange(config.copy(cellularIcon = it)) }
        IconPicker("Map Icon", config.mapIcon, iconOptions) { onChange(config.copy(mapIcon = it)) }
        IconPicker("Home Icon", config.homeIcon, iconOptions) { onChange(config.copy(homeIcon = it)) }
        IconPicker("Settings Icon", config.settingsIcon, iconOptions) { onChange(config.copy(settingsIcon = it)) }
        IconPicker("Search Icon", config.searchIcon, iconOptions) { onChange(config.copy(searchIcon = it)) }
        IconPicker("Star Icon", config.starIcon, iconOptions) { onChange(config.copy(starIcon = it)) }
        IconPicker("Play Icon", config.playIcon, iconOptions) { onChange(config.copy(playIcon = it)) }
    }
}

@Composable
fun IconPicker(label: String, currentIcon: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(currentIcon)
                    Icon(getIconByName(currentIcon), currentIcon, tint = Color.White)
                }
            }
            
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { iconName ->
                    DropdownMenuItem(
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(getIconByName(iconName), iconName, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(iconName)
                            }
                        },
                        onClick = { onSelect(iconName); expanded = false }
                    )
                }
            }
        }
    }
}

fun getIconByName(name: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (name) {
        "Wifi" -> Icons.Default.Wifi
        "WifiTethering" -> Icons.Default.WifiTethering
        "SignalWifi4Bar" -> Icons.Default.SignalWifi4Bar
        "Router" -> Icons.Default.Router
        "Bluetooth" -> Icons.Default.Bluetooth
        "BluetoothConnected" -> Icons.Default.BluetoothConnected
        "BluetoothSearching" -> Icons.Default.BluetoothSearching
        "CellTower" -> Icons.Default.CellTower
        "SignalCellular4Bar" -> Icons.Default.SignalCellular4Bar
        "NetworkCell" -> Icons.Default.NetworkCell
        "Map" -> Icons.Default.Map
        "MapOutlined" -> Icons.Default.Map
        "Explore" -> Icons.Default.Explore
        "Home" -> Icons.Default.Home
        "HomeOutlined" -> Icons.Default.Home
        "House" -> Icons.Default.Home
        "Settings" -> Icons.Default.Settings
        "SettingsOutlined" -> Icons.Default.Settings
        "Tune" -> Icons.Default.Tune
        "Search" -> Icons.Default.Search
        "SearchOutlined" -> Icons.Default.Search
        "FindInPage" -> Icons.Default.FindInPage
        "Star" -> Icons.Default.Star
        "StarOutline" -> Icons.Default.StarBorder
        "StarBorder" -> Icons.Default.StarBorder
        "PlayArrow" -> Icons.Default.PlayArrow
        "PlayCircle" -> Icons.Default.PlayCircle
        "PlayCircleOutline" -> Icons.Default.PlayCircleOutline
        "Favorite" -> Icons.Default.Favorite
        "FavoriteBorder" -> Icons.Default.FavoriteBorder
        "LocationOn" -> Icons.Default.LocationOn
        "LocationSearching" -> Icons.Default.LocationSearching
        "MyLocation" -> Icons.Default.MyLocation
        else -> Icons.Default.Help
    }
}

@Composable
fun ElementsTab(config: ThemeConfig, onChange: (ThemeConfig) -> Unit) {
    var selectedElement by remember { mutableStateOf("Home Cards") }
    val elements = listOf("Home Cards", "List Cards", "Detail Cards", "Sidebar Items", "Top Bar")
    
    Column {
        Text("Per-Element Customization", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Element selector
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            elements.forEach { element ->
                FilterChip(
                    selected = selectedElement == element,
                    onClick = { selectedElement = element },
                    label = { Text(element, fontSize = 10.sp) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (selectedElement) {
            "Home Cards" -> ElementStyleEditor(config.homeScreenCards) { 
                onChange(config.copy(homeScreenCards = it)) 
            }
            "List Cards" -> ElementStyleEditor(config.listScreenCards) { 
                onChange(config.copy(listScreenCards = it)) 
            }
            "Detail Cards" -> ElementStyleEditor(config.detailScreenCards) { 
                onChange(config.copy(detailScreenCards = it)) 
            }
            "Sidebar Items" -> ElementStyleEditor(config.sidebarItems) { 
                onChange(config.copy(sidebarItems = it)) 
            }
            "Top Bar" -> ElementStyleEditor(config.topBarStyle) { 
                onChange(config.copy(topBarStyle = it)) 
            }
        }
    }
}

@Composable
fun ElementStyleEditor(style: ElementStyle, onChange: (ElementStyle) -> Unit) {
    Column {
        Text("Dimensions", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        
        if (style.cardHeight.value > 0) {
            SliderControl("Height", style.cardHeight.value, 50f, 300f) { 
                onChange(style.copy(cardHeight = it.dp)) 
            }
        }
        if (style.cardWidth.value > 0) {
            SliderControl("Width", style.cardWidth.value, 50f, 300f) { 
                onChange(style.copy(cardWidth = it.dp)) 
            }
        }
        SliderControl("Font Size", style.fontSize.value, 10f, 32f) { 
            onChange(style.copy(fontSize = it.sp)) 
        }
        SliderControl("Icon Size", style.iconSize.value, 16f, 48f) { 
            onChange(style.copy(iconSize = it.dp)) 
        }
        SliderControl("Padding", style.padding.value, 0f, 32f) { 
            onChange(style.copy(padding = it.dp)) 
        }
        SliderControl("Margin", style.margin.value, 0f, 32f) { 
            onChange(style.copy(margin = it.dp)) 
        }
        SliderControl("Corner Radius", style.cornerRadius.value, 0f, 50f) { 
            onChange(style.copy(cornerRadius = it.dp)) 
        }
        SliderControl("Elevation", style.elevation.value, 0f, 20f) { 
            onChange(style.copy(elevation = it.dp)) 
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Layout & Positioning", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        
        DropdownSelector("Horizontal Align", style.horizontalAlignment, listOf("Start", "Center", "End")) {
            onChange(style.copy(horizontalAlignment = it))
        }
        DropdownSelector("Vertical Align", style.verticalAlignment, listOf("Top", "Center", "Bottom")) {
            onChange(style.copy(verticalAlignment = it))
        }
        DropdownSelector("Arrangement", style.arrangement, listOf("Start", "Center", "End", "SpaceBetween", "SpaceEvenly", "SpaceAround")) {
            onChange(style.copy(arrangement = it))
        }
        
        SliderControl("Item Spacing", style.itemSpacing.value, 0f, 32f) { 
            onChange(style.copy(itemSpacing = it.dp)) 
        }
        SliderControl("Offset X", style.offsetX.value, -100f, 100f) { 
            onChange(style.copy(offsetX = it.dp)) 
        }
        SliderControl("Offset Y", style.offsetY.value, -100f, 100f) { 
            onChange(style.copy(offsetY = it.dp)) 
        }
    }
}

@Composable
fun DropdownSelector(label: String, current: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color.White, fontSize = 12.sp)
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text(current, fontSize = 12.sp)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { onSelect(option); expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
fun TypographyTab(config: ThemeConfig, onChange: (ThemeConfig) -> Unit) {
    Column {
        Text("Typography", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        SliderControl("Heading Size", config.headingSize.value, 16f, 40f) { 
            onChange(config.copy(headingSize = it.sp)) 
        }
        SliderControl("Body Size", config.bodySize.value, 10f, 20f) { 
            onChange(config.copy(bodySize = it.sp)) 
        }
        SliderControl("Caption Size", config.captionSize.value, 8f, 16f) { 
            onChange(config.copy(captionSize = it.sp)) 
        }
    }
}

@Composable
fun MoreTab(config: ThemeConfig, context: Context, onChange: (ThemeConfig) -> Unit) {
    Column {
        Text("More Settings", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        SliderControl("Icon Size", config.iconSize.value, 16f, 48f) { 
            onChange(config.copy(iconSize = it.dp)) 
        }
        ColorPicker("Icon Color", config.iconColor) { onChange(config.copy(iconColor = it)) }
        
        SliderControl("Animation Duration (ms)", config.animationDuration.toFloat(), 100f, 1000f) { 
            onChange(config.copy(animationDuration = it.toInt())) 
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Enable Animations", color = Color.White)
            Switch(checked = config.enableAnimations, onCheckedChange = { onChange(config.copy(enableAnimations = it)) })
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Dark Mode", color = Color.White)
            Switch(checked = config.isDarkMode, onCheckedChange = { onChange(config.copy(isDarkMode = it)) })
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Theme Management", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        
        Button(
            onClick = { exportTheme(config, context) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Icon(Icons.Default.Download, "Export")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Export Theme")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = { /* Import will be handled via file picker */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Icon(Icons.Default.Upload, "Import")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Import Theme")
        }
    }
}

fun exportTheme(config: ThemeConfig, context: Context) {
    val json = """
    {
        "primaryColor": "${config.primaryColor.value.toString(16)}",
        "accentColor": "${config.accentColor.value.toString(16)}",
        "surfaceColor": "${config.surfaceColor.value.toString(16)}",
        "backgroundColor": "${config.backgroundColor.value.toString(16)}",
        "buttonCornerRadius": ${config.buttonCornerRadius.value},
        "cardCornerRadius": ${config.cardCornerRadius.value},
        "iconSize": ${config.iconSize.value},
        "wifiIcon": "${config.wifiIcon}",
        "bluetoothIcon": "${config.bluetoothIcon}",
        "cellularIcon": "${config.cellularIcon}",
        "isDarkMode": ${config.isDarkMode}
    }
    """.trimIndent()
    
    // Save to clipboard
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = android.content.ClipData.newPlainText("Theme Config", json)
    clipboard.setPrimaryClip(clip)
    
    android.widget.Toast.makeText(context, "Theme exported to clipboard!", android.widget.Toast.LENGTH_SHORT).show()
}

@Composable
fun LivePreview(config: ThemeConfig, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(config.backgroundColor)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Live Preview", color = config.primaryColor, fontSize = config.headingSize, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Sample Card
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(config.surfaceColor, shape = MaterialTheme.shapes.medium)
                .border(config.cardBorderWidth, config.cardBorderColor, shape = MaterialTheme.shapes.medium)
                .padding(config.cardPadding)
        ) {
            Column {
                Text("Sample Card", color = config.primaryColor, fontSize = config.bodySize, fontWeight = FontWeight.Bold)
                Text("This shows your theme", color = config.textSecondaryColor, fontSize = config.captionSize)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Sample Buttons
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
                .border(config.buttonBorderWidth, config.buttonBorderColor, shape = MaterialTheme.shapes.medium),
            colors = ButtonDefaults.buttonColors(containerColor = config.accentColor),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = config.buttonElevation, pressedElevation = config.buttonPressedElevation),
            contentPadding = PaddingValues(config.buttonPadding)
        ) {
            Text("Primary Button", fontSize = config.buttonTextSize)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Text("Outlined Button", color = config.primaryColor, fontSize = config.buttonTextSize)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Wifi, "WiFi", tint = config.iconColor, modifier = Modifier.size(config.iconSize))
            Icon(Icons.Default.Bluetooth, "BT", tint = config.iconColor, modifier = Modifier.size(config.iconSize))
            Icon(Icons.Default.CellTower, "Cell", tint = config.iconColor, modifier = Modifier.size(config.iconSize))
        }
    }
}

@Composable
fun ColorPicker(label: String, color: Color, onColorChange: (Color) -> Unit) {
    var hue by remember { mutableStateOf(0f) }
    var sat by remember { mutableStateOf(0.5f) }
    var value by remember { mutableStateOf(0.5f) }
    
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Slider(value = hue, onValueChange = { hue = it; onColorChange(Color.hsv(hue * 360, sat, value)) }, valueRange = 0f..1f)
        Slider(value = sat, onValueChange = { sat = it; onColorChange(Color.hsv(hue * 360, sat, value)) }, valueRange = 0f..1f)
        Slider(value = value, onValueChange = { value = it; onColorChange(Color.hsv(hue * 360, sat, value)) }, valueRange = 0f..1f)
        Box(modifier = Modifier.fillMaxWidth().height(30.dp).background(color))
    }
}

@Composable
fun SliderControl(label: String, value: Float, min: Float, max: Float, onChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.White, fontSize = 14.sp)
            Text("${value.toInt()}", color = Color.White, fontSize = 14.sp)
        }
        Slider(value = value, onValueChange = onChange, valueRange = min..max)
    }
}
