package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.core.model.WifiNetwork
import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchWifiNetworksUseCase @Inject constructor(
    private val repository: WifiNetworkRepository
) {
    operator fun invoke(query: String): Flow<List<WifiNetwork>> {
        if (query.length < 2) return kotlinx.coroutines.flow.flowOf(emptyList())
        
        return repository.searchNetworks(query)
            .map { networks ->
                networks
                    .filter { it.ssid.contains(query, ignoreCase = true) || it.bssid.contains(query, ignoreCase = true) }
                    .sortedByDescending { it.signalLevel }
            }
    }
}
