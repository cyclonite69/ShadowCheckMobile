package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import javax.inject.Inject

class SyncWiGLEUseCase @Inject constructor(
    private val repository: WifiNetworkRepository
) {
    suspend operator fun invoke(apiKey: String): Result<Int> {
        return repository.syncWithWiGLE(apiKey)
    }
}
