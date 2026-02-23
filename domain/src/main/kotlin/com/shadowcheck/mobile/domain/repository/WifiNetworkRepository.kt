package com.shadowcheck.mobile.domain.repository

import com.shadowcheck.mobile.core.model.WifiNetwork
import kotlinx.coroutines.flow.Flow

interface WifiNetworkRepository {
    fun getAllNetworks(): Flow<List<WifiNetwork>>
    fun getNetworkById(id: Long): Flow<WifiNetwork?>
    fun getNetworksByBssid(bssid: String): Flow<List<WifiNetwork>>
    suspend fun insertNetwork(network: WifiNetwork): Long
    suspend fun updateNetwork(network: WifiNetwork)
    suspend fun deleteNetwork(network: WifiNetwork)
    suspend fun searchNetworks(query: String): Flow<List<WifiNetwork>>
    suspend fun syncWithWiGLE(apiKey: String): Result<Int>
}
