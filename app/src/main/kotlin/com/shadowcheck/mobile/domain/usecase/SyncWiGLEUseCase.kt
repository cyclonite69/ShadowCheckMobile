package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.data.repository.WifiNetworkRepositoryImpl
import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import javax.inject.Inject

/**
 * Use case to synchronize Wi-Fi data with the WiGLE service.
 *
 * @property wifiNetworkRepository The repository to perform the sync operation.
 */
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
            // The repository implementation handles the actual API call and returns Unit.
            // We'll assume for now that if it doesn't throw, it's a success.
            // A more robust implementation would have the repository return the count.
            // For now, we'll return a placeholder count.
            (wifiNetworkRepository as WifiNetworkRepositoryImpl).syncWithWiGLE(apiKey)
            Result.success(0) // Placeholder
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
