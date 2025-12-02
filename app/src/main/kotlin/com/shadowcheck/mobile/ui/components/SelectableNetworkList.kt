package com.shadowcheck.mobile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shadowcheck.mobile.data.WifiNetwork
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

@Composable
fun SelectableNetworkList(
    networks: List<WifiNetwork>,
    selectedNetworks: Set<Long>,
    onNetworkToggle: (WifiNetwork) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(networks, key = { it.id }) { network ->
            SelectableNetworkItem(
                network = network,
                isSelected = network.id in selectedNetworks,
                onToggle = { onNetworkToggle(network) }
            )
        }
    }
}

@Composable
fun SelectableNetworkItem(
    network: WifiNetwork,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                ShadowCheckColors.Primary.copy(alpha = 0.1f)
            else 
                ShadowCheckColors.Surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.CheckCircle 
                                 else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isSelected) ShadowCheckColors.Primary 
                          else ShadowCheckColors.TextSecondary
                )
                
                Column {
                    Text(
                        text = network.ssid.ifEmpty { "Hidden Network" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = network.bssid,
                        style = MaterialTheme.typography.bodySmall,
                        color = ShadowCheckColors.TextSecondary
                    )
                }
            }
            
            Text(
                text = "${network.signalLevel} dBm",
                style = MaterialTheme.typography.bodyMedium,
                color = ShadowCheckColors.TextSecondary
            )
        }
    }
}
