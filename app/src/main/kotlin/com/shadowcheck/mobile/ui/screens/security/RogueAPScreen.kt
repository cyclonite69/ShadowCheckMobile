package com.shadowcheck.mobile.ui.screens.security

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shadowcheck.mobile.data.WifiNetwork
import com.shadowcheck.mobile.rebuilt.presentation.theme.ShadowCheckColors

data class RogueAPCandidate(
    val network: WifiNetwork,
    val suspicionScore: Float,
    val reasons: List<String>
)

@Composable
fun RogueAPScreen(
    networks: List<WifiNetwork>,
    onNetworkClick: (WifiNetwork) -> Unit
) {
    val rogueAPs = detectRogueAPs(networks)
    
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = ShadowCheckColors.Surface,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Rogue AP Detection",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${rogueAPs.size} suspicious networks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (rogueAPs.isEmpty()) ShadowCheckColors.SignalGood 
                           else ShadowCheckColors.SignalMedium
                )
            }
        }
        
        if (rogueAPs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        null,
                        modifier = Modifier.size(64.dp),
                        tint = ShadowCheckColors.SignalGood
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "No Rogue APs Detected",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rogueAPs) { candidate ->
                    RogueAPCard(candidate, onNetworkClick)
                }
            }
        }
    }
}

@Composable
fun RogueAPCard(candidate: RogueAPCandidate, onClick: (WifiNetwork) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick(candidate.network) },
        colors = CardDefaults.cardColors(
            containerColor = when {
                candidate.suspicionScore > 0.7f -> ShadowCheckColors.Error.copy(alpha = 0.1f)
                candidate.suspicionScore > 0.4f -> ShadowCheckColors.SignalMedium.copy(alpha = 0.1f)
                else -> ShadowCheckColors.Surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = candidate.network.ssid.ifEmpty { "Hidden Network" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = candidate.network.bssid,
                        style = MaterialTheme.typography.bodySmall,
                        color = ShadowCheckColors.TextSecondary
                    )
                }
                
                Surface(
                    color = when {
                        candidate.suspicionScore > 0.7f -> ShadowCheckColors.Error
                        candidate.suspicionScore > 0.4f -> ShadowCheckColors.SignalMedium
                        else -> ShadowCheckColors.Primary
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "${(candidate.suspicionScore * 100).toInt()}%",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            Text(
                text = "Suspicious Indicators:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = ShadowCheckColors.TextSecondary
            )
            
            candidate.reasons.forEach { reason ->
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = ShadowCheckColors.SignalMedium
                    )
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun detectRogueAPs(networks: List<WifiNetwork>): List<RogueAPCandidate> {
    val candidates = mutableListOf<RogueAPCandidate>()
    
    // Group by SSID
    val networksBySSID = networks.groupBy { it.ssid }
    
    networks.forEach { network ->
        val reasons = mutableListOf<String>()
        var score = 0f
        
        // Hidden network with strong signal
        if (network.ssid.isEmpty() && network.signalLevel > -50) {
            reasons.add("Hidden network with strong signal")
            score += 0.3f
        }
        
        // Duplicate SSID (Evil Twin)
        networksBySSID[network.ssid]?.let { duplicates ->
            if (duplicates.size > 1 && network.ssid.isNotEmpty()) {
                reasons.add("Multiple APs with same SSID (Evil Twin)")
                score += 0.5f
            }
        }
        
        // Open network with common name
        val commonNames = listOf("Free WiFi", "Public WiFi", "Guest", "attwifi", "xfinitywifi")
        if (commonNames.any { network.ssid.contains(it, ignoreCase = true) } &&
            !network.capabilities.contains("WPA")) {
            reasons.add("Open network with suspicious name")
            score += 0.4f
        }
        
        // Weak/no encryption
        if (!network.capabilities.contains("WPA")) {
            reasons.add("No WPA encryption")
            score += 0.2f
        }
        
        // Suspicious vendor
        val suspiciousOUIs = listOf("00:00:00", "FF:FF:FF")
        if (suspiciousOUIs.any { network.bssid.startsWith(it) }) {
            reasons.add("Suspicious MAC address")
            score += 0.3f
        }
        
        if (reasons.isNotEmpty()) {
            candidates.add(RogueAPCandidate(network, score.coerceAtMost(1f), reasons))
        }
    }
    
    return candidates.sortedByDescending { it.suspicionScore }
}
