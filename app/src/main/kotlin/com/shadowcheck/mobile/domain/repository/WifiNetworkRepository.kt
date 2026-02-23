package com.shadowcheck.mobile.domain.repository

import com.shadowcheck.mobile.domain.model.WifiNetwork
import kotlinx.coroutines.flow.Flow

interface WifiNetworkRepository {
    fun getAllNetworks(): Flow<List<WifiNetwork>>
    fun getNetworkById(ssid: String): Flow<WifiNetwork?>
    fun getNetworksByBssid(bssid: String): Flow<List<WifiNetwork>>
    suspend fun insertNetwork(network: WifiNetwork): Long
    suspend fun updateNetwork(network: WifiNetwork)
    suspend fun deleteNetwork(ssid: String)

    /**
     * Searches for Wi-Fi networks matching the given query string across various fields (e.g., SSID, BSSID).
     *
     * @param query The search query string.
     * @return A [Flow] emitting a list of [WifiNetwork] objects that match the query.
     */
    fun searchNetworks(query: String): Flow<List<WifiNetwork>>

    /**
     * Synchronizes local Wi-Fi network data with the WiGLE.net database.
     * This operation typically involves fetching data from the WiGLE API and updating the local store.
     *
     * @param apiKey The API key for authenticating with the WiGLE.net service.
     */
    suspend fun syncWithWiGLE(apiKey: String): Int
}
