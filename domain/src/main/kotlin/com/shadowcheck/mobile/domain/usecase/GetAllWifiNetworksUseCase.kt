package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.core.model.WifiNetwork
import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllWifiNetworksUseCase @Inject constructor(
    private val repository: WifiNetworkRepository
) {
    operator fun invoke(): Flow<List<WifiNetwork>> {
        return repository.getAllNetworks()
            .map { networks ->
                networks
                    .filter { it.ssid.isNotBlank() }
                    .sortedByDescending { it.timestamp }
            }
    }
}
