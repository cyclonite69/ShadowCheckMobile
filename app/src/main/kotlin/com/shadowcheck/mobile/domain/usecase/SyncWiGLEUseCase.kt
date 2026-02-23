package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.data.repository.WifiNetworkRepositoryImpl
import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to synchronize Wi-Fi data with the WiGLE service.
 *
 * @property wifiNetworkRepository The repository to perform the sync operation.
 */
@Singleton
class SyncWiGLEUseCase @Inject constructor(
    private val wifiNetworkRepository: WifiNetworkRepository
) {
    /**
     * @param apiKey The WiGLE API key.
     * @return A [Result] containing the number of synced networks on success, or an exception on failure.
     */
    suspend operator fun invoke(apiKey: String): Result<Int> {
        if (apiKey.isBlank()) {
            return Result.failure(IllegalArgumentException("API key cannot be blank."))
        }
        return try {
            val syncedCount = wifiNetworkRepository.syncWithWiGLE(apiKey)
            Result.success(syncedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
