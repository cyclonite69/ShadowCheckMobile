package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.domain.model.WifiNetwork
import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to get all Wi-Fi networks, filtering out those with empty SSIDs
 * and sorting them by the last time they were seen.
 *
 * @property wifiNetworkRepository The repository to fetch Wi-Fi network data.
 */
@Singleton
class GetAllWifiNetworksUseCase @Inject constructor(
    private val wifiNetworkRepository: WifiNetworkRepository
) {
    /**
     * @return A [Flow] of [WifiNetwork] lists, sorted by last seen descending.
     */
    operator fun invoke(): Flow<List<WifiNetwork>> {
        return wifiNetworkRepository.getAllNetworks().map { networks ->
            networks
                .filter { it.ssid.isNotBlank() }
                .sortedByDescending { it.timestamp }
        }
    }
}
