package com.shadowcheck.mobile.presentation.viewmodel

import com.shadowcheck.mobile.wifi.domain.usecase.GetAllWifiNetworksUseCase
import com.shadowcheck.mobile.wifi.domain.usecase.SearchWifiNetworksUseCase
import com.shadowcheck.mobile.wifi.domain.usecase.SyncWiGLEUseCase
import com.shadowcheck.mobile.wifi.model.WifiNetwork
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WifiViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var getAllWifiNetworks: GetAllWifiNetworksUseCase

    @MockK
    private lateinit var searchWifiNetworks: SearchWifiNetworksUseCase

    @MockK
    private lateinit var syncWiGLE: SyncWiGLEUseCase

    private lateinit var viewModel: WifiViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load all networks`() = runTest {
        // Given
        val networks = listOf(
            WifiNetwork("Network1", "bssid1", "WPA2", 2412, -70, 1000L),
            WifiNetwork("Network2", "bssid2", "WPA3", 2417, -50, 2000L)
        )
        every { getAllWifiNetworks() } returns flowOf(networks)

        // When
        viewModel = WifiViewModel(getAllWifiNetworks, searchWifiNetworks, syncWiGLE)
        advanceUntilIdle()

        // Then
        assertEquals(2, viewModel.networks.value.size)
        assertEquals("Network1", viewModel.networks.value[0].ssid)
    }

    @Test
    fun `search should update networks with search results`() = runTest {
        // Given
        val initialNetworks = listOf(
            WifiNetwork("Network1", "bssid1", "WPA2", 2412, -70, 1000L)
        )
        val searchResults = listOf(
            WifiNetwork("SearchResult", "bssid2", "WPA3", 2417, -50, 2000L)
        )
        every { getAllWifiNetworks() } returns flowOf(initialNetworks)
        every { searchWifiNetworks("test") } returns flowOf(searchResults)

        viewModel = WifiViewModel(getAllWifiNetworks, searchWifiNetworks, syncWiGLE)
        advanceUntilIdle()

        // When
        viewModel.search("test")
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.networks.value.size)
        assertEquals("SearchResult", viewModel.networks.value[0].ssid)
    }

    @Test
    fun `syncData should set loading state and sync with WiGLE`() = runTest {
        // Given
        val networks = emptyList<WifiNetwork>()
        every { getAllWifiNetworks() } returns flowOf(networks)
        coEvery { syncWiGLE("test-api-key") } returns Result.success(1)

        viewModel = WifiViewModel(getAllWifiNetworks, searchWifiNetworks, syncWiGLE)
        advanceUntilIdle()

        // When
        viewModel.syncData("test-api-key")

        // During sync
        assertTrue(viewModel.isLoading.value)

        advanceUntilIdle()

        // Then
        assertFalse(viewModel.isLoading.value)
        coVerify { syncWiGLE("test-api-key") }
    }

    @Test
    fun `syncData should handle errors gracefully`() = runTest {
        // Given
        val networks = emptyList<WifiNetwork>()
        every { getAllWifiNetworks() } returns flowOf(networks)
        coEvery { syncWiGLE("test-api-key") } returns Result.failure(Exception("Network error"))

        viewModel = WifiViewModel(getAllWifiNetworks, searchWifiNetworks, syncWiGLE)
        advanceUntilIdle()

        // When
        viewModel.syncData("test-api-key")
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.isLoading.value)
        assertEquals("Network error", viewModel.error.value)
    }

    @Test
    fun `loadNetworks should refresh network list`() = runTest {
        // Given
        val initialNetworks = listOf(
            WifiNetwork("Network1", "bssid1", "WPA2", 2412, -70, 1000L)
        )
        every { getAllWifiNetworks() } returns flowOf(initialNetworks)

        viewModel = WifiViewModel(getAllWifiNetworks, searchWifiNetworks, syncWiGLE)
        advanceUntilIdle()

        // When
        viewModel.loadNetworks()
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.networks.value.size)
    }
}
