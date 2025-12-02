package com.shadowcheck.mobile.ui.screens.wifi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowcheck.mobile.ui.viewmodel.WifiViewModel

@Composable
fun WifiScreen(viewModel: WifiViewModel = hiltViewModel()) {
    val networks by viewModel.networks.collectAsState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Found ${networks.size} Wi-Fi networks")
    }
}
