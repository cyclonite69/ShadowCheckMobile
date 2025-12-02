package com.shadowcheck.mobile.rebuilt.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppNavigation() {
    Scaffold { paddingValues ->
        Text(
            text = "ShadowCheck - Ready to integrate screens",
            modifier = Modifier.padding(paddingValues)
        )
    }
}
