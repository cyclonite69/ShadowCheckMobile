package com.shadowcheck.mobile.ui.screens.maps

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MapScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Map View",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Google Maps / Mapbox integration coming soon",
            modifier = Modifier.padding(16.dp)
        )
    }
}
