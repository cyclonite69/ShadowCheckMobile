package com.shadowcheck.mobile.wifi.domain.usecase

import com.shadowcheck.mobile.wifi.domain.repository.WifiNetworkRepository
import com.shadowcheck.mobile.wifi.model.WifiNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllWifiNetworksUseCase @Inject constructor(
    private val repository: WifiNetworkRepository
) {
    operator fun invoke(): Flow<List<WifiNetwork>> {
        return repository.getAllNetworks().map { networks ->
            networks
                .filter { it.ssid.isNotBlank() }
                .sortedByDescending { it.timestamp }
        }
    }
}
