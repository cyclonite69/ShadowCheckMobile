package com.shadowcheck.mobile.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.shadowcheck.mobile.domain.model.ThreatDetection
import com.shadowcheck.mobile.domain.model.ThreatSeverity
import com.shadowcheck.mobile.domain.model.ThreatType
import com.shadowcheck.mobile.wifi.domain.repository.WifiNetworkRepository
import com.shadowcheck.mobile.wifi.model.WifiNetwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NetworkMonitorService(
    private val context: Context,
    private val wifiNetworkRepository: WifiNetworkRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    
    private val _connectionEvents = MutableStateFlow<WiFiConnectionEvent?>(null)
    val connectionEvents: StateFlow<WiFiConnectionEvent?> = _connectionEvents
    
    private val _disconnectionEvents = MutableStateFlow<WiFiDisconnectionEvent?>(null)
    val disconnectionEvents: StateFlow<WiFiDisconnectionEvent?> = _disconnectionEvents
    
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        
        override fun onAvailable(network: Network) {
            Log.d("NetworkMonitor", "Network available: $network")
        }
        
        override fun onLost(network: Network) {
            Log.d("NetworkMonitor", "Network lost: $network")
        }
        
        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val wifiInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    capabilities.transportInfo as? WifiInfo
                } else {
                    null
                }
                
                wifiInfo?.let { info ->
                    Log.d("NetworkMonitor", 
                        "WiFi: BSSID=${info.bssid} RSSI=${info.rssi} " +
                        "freq=${info.frequency} linkSpeed=${info.linkSpeed}")
                    
                    scope.launch {
                        saveWiFiConnection(info)
                    }
                }
            }
        }
        
        @RequiresApi(Build.VERSION_CODES.S)
        fun onWifiConnectionChanged(
            network: Network,
            wifiInfo: WifiInfo,
            connectionState: Int
        ) {
            Log.d("NetworkMonitor", 
                "WiFi Connection Changed: BSSID=${wifiInfo.bssid} " +
                "RSSI=${wifiInfo.rssi} freq=${wifiInfo.frequency} " +
                "linkSpeed=${wifiInfo.linkSpeed} state=$connectionState")
            
            val event = WiFiConnectionEvent(
                bssid = wifiInfo.bssid ?: "unknown",
                ssid = wifiInfo.ssid ?: "unknown",
                rssi = wifiInfo.rssi,
                frequency = wifiInfo.frequency,
                linkSpeed = wifiInfo.linkSpeed,
                connectionState = connectionState,
                timestamp = System.currentTimeMillis()
            )
            
            _connectionEvents.value = event
            
            scope.launch {
                saveWiFiConnection(wifiInfo)
            }
        }
        
        @RequiresApi(Build.VERSION_CODES.S)
        fun onDisconnected(network: Network, reason: Int) {
            val reasonText = getDisconnectReason(reason)
            Log.e("NetworkMonitor", "WiFi Disconnected! Reason code = $reason ($reasonText)")
            
            val event = WiFiDisconnectionEvent(
                reason = reason,
                reasonText = reasonText,
                timestamp = System.currentTimeMillis()
            )
            
            _disconnectionEvents.value = event
            
            // Detect deauth attacks
            if (reason == 6 || reason == 7) { // DEAUTH_LEAVING or DEAUTH_DISASSOC
                scope.launch {
                    detectDeauthAttack(reason, reasonText)
                }
            }
        }
    }
    
    fun startMonitoring() {
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        
        connectivityManager.registerNetworkCallback(request, networkCallback)
        Log.d("NetworkMonitor", "Network monitoring started")
    }
    
    fun stopMonitoring() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
        Log.d("NetworkMonitor", "Network monitoring stopped")
    }
    
    private suspend fun saveWiFiConnection(wifiInfo: WifiInfo) {
        val network = WifiNetwork(
            ssid = wifiInfo.ssid ?: "",
            bssid = wifiInfo.bssid ?: return,
            capabilities = "",
            frequency = wifiInfo.frequency,
            signalLevel = wifiInfo.rssi,
            timestamp = System.currentTimeMillis(),
            latitude = 0.0,
            longitude = 0.0
        )
        
        wifiNetworkRepository.insertNetwork(network)
    }
    
    private suspend fun detectDeauthAttack(reason: Int, reasonText: String) {
        val threat = ThreatDetection(
            type = ThreatType.DEAUTH_ATTACK,
            severity = ThreatSeverity.HIGH,
            title = "Deauthentication Attack Detected",
            description = "WiFi disconnected with reason: $reasonText (code $reason). " +
                    "This may indicate a deauth attack or network interference.",
            confidence = 0.7f,
            timestamp = System.currentTimeMillis()
        )
        
        // Store threat detection
        Log.w("NetworkMonitor", "THREAT: ${threat.title}")
    }
    
    private fun getDisconnectReason(reason: Int): String {
        return when (reason) {
            0 -> "REASON_UNSPECIFIED"
            1 -> "REASON_NETWORK_REMOVED"
            2 -> "REASON_NETWORK_DISABLED"
            3 -> "REASON_NETWORK_LOST"
            4 -> "REASON_NETWORK_FAILED"
            5 -> "REASON_NETWORK_METERED"
            6 -> "DEAUTH_LEAVING" // Deauth attack indicator
            7 -> "DEAUTH_DISASSOC" // Disassociation
            8 -> "REASON_NETWORK_CAPTIVE_PORTAL"
            else -> "UNKNOWN_REASON_$reason"
        }
    }
}

data class WiFiConnectionEvent(
    val bssid: String,
    val ssid: String,
    val rssi: Int,
    val frequency: Int,
    val linkSpeed: Int,
    val connectionState: Int,
    val timestamp: Long
)

data class WiFiDisconnectionEvent(
    val reason: Int,
    val reasonText: String,
    val timestamp: Long
)
