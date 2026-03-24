package com.shadowcheck.mobile.wifi.domain.usecase

import com.shadowcheck.mobile.wifi.domain.repository.WifiNetworkRepository
import com.shadowcheck.mobile.wifi.model.WifiNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchWifiNetworksUseCase @Inject constructor(
    private val repository: WifiNetworkRepository
) {
    operator fun invoke(query: String): Flow<List<WifiNetwork>> {
        if (query.isBlank() || query.length < 2) {
            return emptyFlow()
        }

        return repository.searchNetworks(query).map { networks ->
            networks.sortedByDescending { it.signalLevel }
        }
    }
}
