package com.shadowcheck.mobile.rebuilt.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatmapScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    var timeSlots by remember { mutableStateOf<Map<Pair<Int, Int>, Int>>(emptyMap()) }
    var selectedType by remember { mutableStateOf("WiFi") }
    var searchQuery by remember { mutableStateOf("") }
    var weekOffset by remember { mutableStateOf(0) }
    var networks by remember { mutableStateOf<List<String>>(emptyList()) }
    
    val startOfWeek = Calendar.getInstance().apply {
        add(Calendar.WEEK_OF_YEAR, -weekOffset)
        set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }.timeInMillis
    
    val endOfWeek = Calendar.getInstance().apply {
        timeInMillis = startOfWeek
        add(Calendar.DAY_OF_YEAR, 7)
    }.timeInMillis
    
    LaunchedEffect(selectedType, searchQuery, weekOffset) {
        val db = Room.databaseBuilder(context, ShadowCheckDatabase::class.java, "shadowcheck.db").build()
        when (selectedType) {
            "WiFi" -> db.wifiNetworkDao().getAllFlow().collect { allNetworks ->
                networks = allNetworks.map { it.ssid.ifBlank { it.bssid } }.distinct()
                val filtered = if (searchQuery.isBlank()) allNetworks 
                    else allNetworks.filter { it.ssid.contains(searchQuery, true) || it.bssid.contains(searchQuery, true) }
                timeSlots = filtered.filter { it.timestamp in startOfWeek..endOfWeek }
                    .groupBy { Pair(getDayOfWeek(it.timestamp), getHourOfDay(it.timestamp)) }
                    .mapValues { it.value.size }
            }
            "Bluetooth" -> db.bleDeviceDao().getAllFlow().collect { allDevices ->
                networks = allDevices.mapNotNull { it.name ?: it.address }.distinct()
                val filtered = if (searchQuery.isBlank()) allDevices
                    else allDevices.filter { (it.name?.contains(searchQuery, true) == true) || it.address.contains(searchQuery, true) }
                timeSlots = filtered.filter { it.timestamp in startOfWeek..endOfWeek }
                    .groupBy { Pair(getDayOfWeek(it.timestamp), getHourOfDay(it.timestamp)) }
                    .mapValues { it.value.size }
            }
            "Cellular" -> db.cellularTowerDao().getAllFlow().collect { allTowers ->
                networks = allTowers.map { "Cell ${it.cellId}" }.distinct()
                val filtered = if (searchQuery.isBlank()) allTowers
                    else allTowers.filter { it.cellId.toString().contains(searchQuery) }
                timeSlots = filtered.filter { it.timestamp in startOfWeek..endOfWeek }
                    .groupBy { Pair(getDayOfWeek(it.timestamp), getHourOfDay(it.timestamp)) }
                    .mapValues { it.value.size }
            }
        }
    }
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("Temporal Heatmap", color = ShadowCheckColors.Primary, fontSize = 18.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = ShadowCheckColors.TextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = ShadowCheckColors.Surface)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(selected = selectedType == "WiFi", onClick = { selectedType = "WiFi"; searchQuery = "" }, label = { Text("WiFi") })
            FilterChip(selected = selectedType == "Bluetooth", onClick = { selectedType = "Bluetooth"; searchQuery = "" }, label = { Text("Bluetooth") })
            FilterChip(selected = selectedType == "Cellular", onClick = { selectedType = "Cellular"; searchQuery = "" }, label = { Text("Cellular") })
        }
        
        var showDropdown by remember { mutableStateOf(false) }
        val filteredNetworks = networks.filter { it.contains(searchQuery, ignoreCase = true) }.take(10)
        
        Box {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    showDropdown = it.isNotEmpty()
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text("Search network...") },
                leadingIcon = { Icon(Icons.Default.Search, "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = ""; showDropdown = false }) {
                            Icon(Icons.Default.Close, "Clear")
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = ShadowCheckColors.Surface,
                    unfocusedContainerColor = ShadowCheckColors.Surface
                )
            )
            
            if (showDropdown && filteredNetworks.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 60.dp),
                    colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.heightIn(max = 200.dp).verticalScroll(rememberScrollState())) {
                        filteredNetworks.forEach { network ->
                            Text(
                                text = network,
                                modifier = Modifier.fillMaxWidth().clickable { 
                                    searchQuery = network
                                    showDropdown = false
                                }.padding(16.dp),
                                color = ShadowCheckColors.TextPrimary
                            )
                            Divider(color = ShadowCheckColors.Background)
                        }
                    }
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            IconButton(onClick = { weekOffset++ }) {
                Icon(Icons.Default.ArrowBack, "Previous Week", tint = ShadowCheckColors.Primary)
            }
            Text(
                getWeekLabel(weekOffset),
                color = ShadowCheckColors.Primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { if (weekOffset > 0) weekOffset-- }) {
                Icon(Icons.Default.ArrowForward, "Next Week", tint = ShadowCheckColors.Primary)
            }
        }
        
        Text(
            "Activity by Day/Hour",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ShadowCheckColors.Primary
        )
        
        TemporalHeatmapCanvas(timeSlots)
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Legend", fontWeight = FontWeight.Bold, color = ShadowCheckColors.Primary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    LegendItem(Color(0xFF1A237E), "Low")
                    LegendItem(Color(0xFF0D47A1), "Medium")
                    LegendItem(Color(0xFF01579B), "High")
                    LegendItem(Color(0xFF006064), "Very High")
                }
            }
        }
    }
}

@Composable
fun TemporalHeatmapCanvas(timeSlots: Map<Pair<Int, Int>, Int>) {
    val maxCount = timeSlots.values.maxOrNull() ?: 1
    
    Column {
        Canvas(modifier = Modifier.fillMaxWidth().height(300.dp).padding(16.dp)) {
            val cellWidth = size.width / 24
            val cellHeight = size.height / 7
            
            for (day in 0..6) {
                for (hour in 0..23) {
                    val count = timeSlots[Pair(day, hour)] ?: 0
                    val intensity = if (maxCount > 0) count.toFloat() / maxCount else 0f
                    
                    val color = when {
                        intensity == 0f -> Color(0xFF263238)
                        intensity < 0.25f -> Color(0xFF1A237E)
                        intensity < 0.5f -> Color(0xFF0D47A1)
                        intensity < 0.75f -> Color(0xFF01579B)
                        else -> Color(0xFF006064)
                    }
                    
                    drawRect(
                        color = color,
                        topLeft = Offset(hour * cellWidth, day * cellHeight),
                        size = Size(cellWidth - 2, cellHeight - 2)
                    )
                }
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            (0..23 step 3).forEach { hour ->
                Text("${hour}h", fontSize = 10.sp, color = ShadowCheckColors.TextSecondary)
            }
        }
        
        Column(modifier = Modifier.padding(start = 4.dp)) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(day, fontSize = 10.sp, color = ShadowCheckColors.TextSecondary, modifier = Modifier.height(42.dp))
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Box(modifier = Modifier.size(16.dp).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 12.sp, color = ShadowCheckColors.TextPrimary)
    }
}

fun getHourOfDay(timestamp: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    return calendar.get(Calendar.HOUR_OF_DAY)
}

fun getDayOfWeek(timestamp: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    return calendar.get(Calendar.DAY_OF_WEEK) - 1
}

fun getWeekLabel(weekOffset: Int): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.WEEK_OF_YEAR, -weekOffset)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    val start = SimpleDateFormat("MMM d", Locale.getDefault()).format(cal.time)
    cal.add(Calendar.DAY_OF_YEAR, 6)
    val end = SimpleDateFormat("MMM d", Locale.getDefault()).format(cal.time)
    return if (weekOffset == 0) "This Week ($start - $end)" else "$start - $end"
}
