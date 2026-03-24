package com.shadowcheck.mobile.wifi.domain.usecase

import com.shadowcheck.mobile.wifi.domain.repository.WifiNetworkRepository
import javax.inject.Inject

class SyncWiGLEUseCase @Inject constructor(
    private val repository: WifiNetworkRepository
) {
    suspend operator fun invoke(apiKey: String): Result<Int> {
        if (apiKey.isBlank()) {
            return Result.failure(IllegalArgumentException("API key cannot be blank"))
        }

        return runCatching { repository.syncWithWiGLE(apiKey) }
    }
}
