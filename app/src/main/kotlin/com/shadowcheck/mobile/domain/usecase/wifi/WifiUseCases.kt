package com.shadowcheck.mobile.domain.usecase.wifi

import com.shadowcheck.mobile.domain.model.WifiNetwork
import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllWifiNetworksUseCase @Inject constructor(
    private val repository: WifiNetworkRepository
) {
    operator fun invoke(): Flow<List<WifiNetwork>> = repository.getAllNetworks()
}

class SearchWifiNetworksUseCase @Inject constructor(
    private val repository: WifiNetworkRepository
) {
    operator fun invoke(query: String): Flow<List<WifiNetwork>> = repository.searchNetworks(query)
}

class SyncWigleDataUseCase @Inject constructor(
    private val repository: WifiNetworkRepository
) {
    suspend operator fun invoke(apiKey: String) = repository.syncWithWiGLE(apiKey)
}

class GetNetworkByBssidUseCase @Inject constructor(
    private val repository: WifiNetworkRepository
) {
    operator fun invoke(bssid: String): Flow<List<WifiNetwork>> = repository.getNetworksByBssid(bssid)
}

class InsertWifiNetworkUseCase @Inject constructor(
    private val repository: WifiNetworkRepository
) {
    suspend operator fun invoke(network: WifiNetwork) = repository.insertNetwork(network)
}
