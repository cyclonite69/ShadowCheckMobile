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
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class GetAllWifiNetworksUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var repository: WifiNetworkRepository

    @InjectMockKs
    private lateinit var useCase: GetAllWifiNetworksUseCase

    @Test
    fun `invoke should return all networks filtered and sorted by timestamp`() = runTest {
        // Given
        val networks = listOf(
            WifiNetwork("Network1", "bssid1", "WPA2", 2412, -70, 1000L),
            WifiNetwork("Network2", "bssid2", "WPA3", 2417, -50, 3000L),
            WifiNetwork("Network3", "bssid3", "WPA2", 2422, -60, 2000L)
        )
        every { repository.getAllNetworks() } returns flowOf(networks)

        // When
        val result = useCase().first()

        // Then
        assertEquals(3, result.size)
        // Should be sorted by timestamp descending (3000L, 2000L, 1000L)
        assertEquals(3000L, result[0].timestamp)
        assertEquals(2000L, result[1].timestamp)
        assertEquals(1000L, result[2].timestamp)
    }

    @Test
    fun `invoke should filter out networks with blank SSID`() = runTest {
        // Given
        val networks = listOf(
            WifiNetwork("Network1", "bssid1", "WPA2", 2412, -70, 1000L),
            WifiNetwork("", "bssid2", "WPA3", 2417, -50, 2000L),
            WifiNetwork("Network3", "bssid3", "WPA2", 2422, -60, 3000L)
        )
        every { repository.getAllNetworks() } returns flowOf(networks)

        // When
        val result = useCase().first()

        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.ssid.isNotBlank() })
    }

    @Test
    fun `invoke should return empty list when no networks available`() = runTest {
        // Given
        every { repository.getAllNetworks() } returns flowOf(emptyList())

        // When
        val result = useCase().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke should handle networks with same timestamp`() = runTest {
        // Given
        val networks = listOf(
            WifiNetwork("Network1", "bssid1", "WPA2", 2412, -70, 2000L),
            WifiNetwork("Network2", "bssid2", "WPA3", 2417, -50, 2000L),
            WifiNetwork("Network3", "bssid3", "WPA2", 2422, -60, 2000L)
        )
        every { repository.getAllNetworks() } returns flowOf(networks)

        // When
        val result = useCase().first()

        // Then
        assertEquals(3, result.size)
        assertTrue(result.all { it.timestamp == 2000L })
    }
}
