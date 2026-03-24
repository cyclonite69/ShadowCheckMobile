package com.shadowcheck.mobile.wifi.data.repository

import com.shadowcheck.mobile.wifi.data.local.dao.WifiNetworkDao
import com.shadowcheck.mobile.wifi.data.local.entity.WifiNetworkEntity
import com.shadowcheck.mobile.wifi.data.remote.WiGLEApiService
import com.shadowcheck.mobile.wifi.data.remote.dto.WigleNetworkDto
import com.shadowcheck.mobile.wifi.data.remote.dto.WigleWifiSearchResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import kotlin.time.Duration.Companion.seconds

@ExperimentalCoroutinesApi
class WifiNetworkRepositoryImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var wifiDao: WifiNetworkDao

    @MockK
    private lateinit var wigleService: WiGLEApiService
    
    // @InjectMockKs(overrideValues = true)
    private lateinit var repository: WifiNetworkRepositoryImpl
    
    // Test data
    private val networkEntity1 = WifiNetworkEntity("bssid1", "ssid1", "WPA2", 2412, -50, 1000L)
    private val networkEntity2 = WifiNetworkEntity("bssid2", "ssid2", "WPA2", 2417, -60, 2000L)
    private val networkEntities = listOf(networkEntity1, networkEntity2)

    @Before
    fun setUp() {
        // This is a bit of a hack to inject the dispatcher. A better way would be a test-specific DI module.
        // For this case, we'll manually create the repository.
        repository = WifiNetworkRepositoryImpl(wifiDao, wigleService, kotlinx.coroutines.Dispatchers.Unconfined)
    }

    @Test
    fun `getAllNetworks should return networks from DAO`() = runTest(timeout = 10.seconds) {
        // Given
        every { wifiDao.getAllNetworks() } returns flowOf(networkEntities)

        // When
        val result = repository.getAllNetworks().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("ssid1", result[0].ssid)
    }

    @Test
    fun `insertNetwork should call DAO and return result`() = runTest(timeout = 10.seconds) {
        // Given
        coEvery { wifiDao.insertNetwork(any()) } returns 1L

        // When
        val result = repository.insertNetwork(networkEntity1.toModel())

        // Then
        assertEquals(1L, result)
        coVerify { wifiDao.insertNetwork(networkEntity1) }
    }

    @Test
    fun `searchNetworks should return filtered data from DAO`() = runTest(timeout = 10.seconds) {
        // Given
        every { wifiDao.searchNetworks(any()) } returns flowOf(listOf(networkEntity1))

        // When
        val result = repository.searchNetworks("ssid1").first()

        // Then
        assertEquals(1, result.size)
        assertEquals("ssid1", result[0].ssid)
    }
    
    @Test
    fun `syncWithWiGLE should call service and insert results`() = runTest(timeout = 10.seconds) {
        // Given
        val dto = WigleNetworkDto(0.0, 0.0, "ssid_from_wigle", "bssid_from_wigle", 1, "WPA3", "", -55)
        val response = WigleWifiSearchResponse(true, listOf(dto))
        coEvery { wigleService.searchNetworks(any()) } returns Response.success(response)
        coEvery { wifiDao.insertAll(any()) } returns Unit

        // When
        repository.syncWithWiGLE("fake_api_key")

        // Then
        coVerify { wigleService.searchNetworks("Basic fake_api_key") }
        coVerify { wifiDao.insertAll(any()) }
    }
}
