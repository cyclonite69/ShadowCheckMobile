package com.shadowcheck.mobile.ui.screens.bluetooth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowcheck.mobile.presentation.viewmodel.BluetoothViewModel

@Composable
fun BluetoothScreen(viewModel: BluetoothViewModel = hiltViewModel()) {
    val devices by viewModel.devices.collectAsState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Found ${devices.size} Bluetooth devices")
    }
}
