// package com.shadowcheck.mobile.ui.components
//
// import androidx.compose.foundation.layout.*
// import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.Search
// import androidx.compose.material3.*
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.text.font.FontWeight
// import androidx.compose.ui.unit.dp
// import com.shadowcheck.mobile.models.WiFiFilters
// import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors
//
// @Composable
// fun FilterPanel(
//    filters: WiFiFilters,
//    onFiltersChange: (WiFiFilters) -> Unit,
//    modifier: Modifier = Modifier
// ) {
//    Surface(
//        modifier = modifier,
//        color = ShadowCheckColors.Surface,
//        tonalElevation = 1.dp
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            Text(
//                text = "Filters",
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold
//            )
//
//            OutlinedTextField(
//                value = filters.searchQuery,
//                onValueChange = { onFiltersChange(filters.copy(searchQuery = it)) },
//                label = { Text("Search") },
//                modifier = Modifier.fillMaxWidth(),
//                leadingIcon = { Icon(Icons.Default.Search, null) }
//            )
//
//            Text("Signal: ${filters.minSignalStrength} to ${filters.maxSignalStrength} dBm")
//            RangeSlider(
//                value = filters.minSignalStrength.toFloat()..filters.maxSignalStrength.toFloat(),
//                onValueChange = { range ->
//                    onFiltersChange(
//                        filters.copy(
//                            minSignalStrength = range.start.toInt(),
//                            maxSignalStrength = range.endInclusive.toInt()
//                        )
//                    )
//                },
//                valueRange = -100f..0f
//            )
//
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Only with GPS")
//                Switch(
//                    checked = filters.onlyWithLocation,
//                    onCheckedChange = { onFiltersChange(filters.copy(onlyWithLocation = it)) }
//                )
//            }
//        }
//    }
// }