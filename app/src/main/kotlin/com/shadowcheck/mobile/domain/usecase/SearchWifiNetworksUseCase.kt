package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.core.model.WifiNetwork
import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to search for Wi-Fi networks based on a query.
 *
 * @property wifiNetworkRepository The repository to search for Wi-Fi networks.
 */
@Singleton
class SearchWifiNetworksUseCase @Inject constructor(
    private val wifiNetworkRepository: WifiNetworkRepository
) {
    /**
     * @param query The search term to use. Must be at least 2 characters.
     * @return A [Flow] of [WifiNetwork] lists, sorted by signal strength descending.
     */
    operator fun invoke(query: String): Flow<List<WifiNetwork>> {
        if (query.isBlank() || query.length < 2) {
            return emptyFlow()
        }
        return wifiNetworkRepository.searchNetworks(query).map { networks ->
            networks.sortedByDescending { it.signalLevel }
        }
    }
}
