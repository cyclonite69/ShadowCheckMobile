package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.domain.model.WifiNetwork
import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SearchWifiNetworksUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var repository: WifiNetworkRepository

    @InjectMockKs
    private lateinit var useCase: SearchWifiNetworksUseCase

    @Test
    fun `invoke with valid query should return filtered and sorted results`() = runTest {
        // Given
        val unsortedNetworks = listOf(
            WifiNetwork("ssid1", "bssid1", "WPA2", 2412, -70, 1000L),
            WifiNetwork("ssid2", "bssid2", "WPA2", 2417, -50, 2000L)
        )
        every { repository.searchNetworks("test") } returns flowOf(unsortedNetworks)

        // When
        val result = useCase("test").first()

        // Then
        assertEquals(2, result.size)
        assertEquals(-50, result[0].level) // Check if sorted by signal strength
        assertEquals(-70, result[1].level)
    }

    @Test
    fun `invoke with blank query should return empty flow`() = runTest {
        // When
        val result = useCase("").toList()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke with short query should return empty flow`() = runTest {
        // When
        val result = useCase("a").toList()

        // Then
        assertTrue(result.isEmpty())
    }
}
