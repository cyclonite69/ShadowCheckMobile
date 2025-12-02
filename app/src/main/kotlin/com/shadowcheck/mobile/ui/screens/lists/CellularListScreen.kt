package com.shadowcheck.mobile.ui.screens.lists

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.CellInfo
import android.telephony.TelephonyManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.shadowcheck.mobile.models.CellularFilters
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CellularListScreen(
    towers: List<Any>,
    filters: CellularFilters,
    onFiltersChange: (CellularFilters) -> Unit,
    onTowerClick: (Any) -> Unit,
    onExport: () -> Unit
) {
    val context = LocalContext.current
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    var cellTowers by remember { mutableStateOf<List<CellInfo>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                cellTowers = telephonyManager.allCellInfo ?: emptyList()
            }
            delay(5000)
        }
    }
    
    Column(modifier = Modifier.fillMaxSize().background(ShadowCheckColors.Background)) {
        TopAppBar(
            title = { Text("Cellular Towers (${cellTowers.size})") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = ShadowCheckColors.Surface,
                titleContentColor = ShadowCheckColors.TextPrimary
            )
        )
        
        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            items(cellTowers) { cell ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = ShadowCheckColors.Surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(cell.javaClass.simpleName, color = ShadowCheckColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Registered: ${cell.isRegistered}", color = ShadowCheckColors.TextSecondary, fontSize = 12.sp)
                        Text("Strength: ${cell.cellSignalStrength.level}", color = ShadowCheckColors.Primary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
