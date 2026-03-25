package com.shadowcheck.mobile.wifi.domain.repository

import com.shadowcheck.mobile.wifi.model.WifiNetwork
import kotlinx.coroutines.flow.Flow

interface WifiNetworkRepository {
    fun getAllNetworks(): Flow<List<WifiNetwork>>
    fun getNetworkBySsid(ssid: String): Flow<WifiNetwork?>
    fun getNetworksByBssid(bssid: String): Flow<List<WifiNetwork>>
    suspend fun insertNetwork(network: WifiNetwork): Long
    suspend fun insertNetworks(networks: List<WifiNetwork>)
    suspend fun updateNetwork(network: WifiNetwork)
    suspend fun deleteNetwork(ssid: String)
    fun searchNetworks(query: String): Flow<List<WifiNetwork>>
    suspend fun syncWithWiGLE(apiKey: String): Int
}
