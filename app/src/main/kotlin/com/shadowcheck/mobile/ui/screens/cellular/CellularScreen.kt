package com.shadowcheck.mobile.ui.screens.cellular

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowcheck.mobile.ui.viewmodel.CellularViewModel

@Composable
fun CellularScreen(viewModel: CellularViewModel = hiltViewModel()) {
    val towers by viewModel.towers.collectAsState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Found ${towers.size} cellular towers")
    }
}
