package com.shadowcheck.mobile.rebuilt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlaybackScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Playback Feature",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Historical network playback coming soon",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
