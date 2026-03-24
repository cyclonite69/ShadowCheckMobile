package com.shadowcheck.mobile.presentation.viewmodel

import com.shadowcheck.mobile.domain.model.BluetoothDevice
import com.shadowcheck.mobile.domain.usecase.GetAllBluetoothDevicesUseCase
import com.shadowcheck.mobile.domain.usecase.GetNearbyBluetoothDevicesUseCase
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class BluetoothViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var getAllBluetoothDevices: GetAllBluetoothDevicesUseCase

    @MockK
    private lateinit var getNearbyBluetoothDevices: GetNearbyBluetoothDevicesUseCase

    private lateinit var viewModel: BluetoothViewModel

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
    fun `init should load all devices`() = runTest {
        // Given
        val devices = listOf(
            BluetoothDevice(
                macAddress = "AA:BB:CC:DD:EE:01",
                name = "Device1",
                type = 1,
                rssi = -70,
                timestamp = 1000L
            ),
            BluetoothDevice(
                macAddress = "AA:BB:CC:DD:EE:02",
                name = "Device2",
                type = 2,
                rssi = -50,
                timestamp = 2000L
            )
        )
        every { getAllBluetoothDevices() } returns flowOf(devices)

        // When
        viewModel = BluetoothViewModel(getAllBluetoothDevices, getNearbyBluetoothDevices)
        advanceUntilIdle()

        // Then
        assertEquals(2, viewModel.devices.value.size)
        assertEquals("Device1", viewModel.devices.value[0].name)
    }

    @Test
    fun `loadAllDevices should refresh device list`() = runTest {
        // Given
        val devices = listOf(
            BluetoothDevice(
                macAddress = "AA:BB:CC:DD:EE:01",
                name = "Device1",
                type = 1,
                rssi = -70,
                timestamp = 1000L
            )
        )
        every { getAllBluetoothDevices() } returns flowOf(devices)

        viewModel = BluetoothViewModel(getAllBluetoothDevices, getNearbyBluetoothDevices)
        advanceUntilIdle()

        // When
        viewModel.loadAllDevices()
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.devices.value.size)
    }

    @Test
    fun `findNearbyDevices should update devices with nearby results`() = runTest {
        // Given
        val allDevices = listOf(
            BluetoothDevice(
                macAddress = "AA:BB:CC:DD:EE:01",
                name = "Device1",
                type = 1,
                rssi = -70,
                timestamp = 1000L
            )
        )
        val nearbyDevices = listOf(
            BluetoothDevice(
                macAddress = "AA:BB:CC:DD:EE:02",
                name = "NearbyDevice",
                type = 2,
                rssi = -50,
                timestamp = 2000L
            )
        )
        every { getAllBluetoothDevices() } returns flowOf(allDevices)
        every { getNearbyBluetoothDevices(-70) } returns flowOf(nearbyDevices)

        viewModel = BluetoothViewModel(getAllBluetoothDevices, getNearbyBluetoothDevices)
        advanceUntilIdle()

        // When
        viewModel.findNearbyDevices(-70)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.devices.value.size)
        assertEquals("NearbyDevice", viewModel.devices.value[0].name)
    }

    @Test
    fun `findNearbyDevices should use default threshold when not specified`() = runTest {
        // Given
        val allDevices = emptyList<BluetoothDevice>()
        val nearbyDevices = listOf(
            BluetoothDevice(
                macAddress = "AA:BB:CC:DD:EE:01",
                name = "NearbyDevice",
                type = 2,
                rssi = -60,
                timestamp = 1000L
            )
        )
        every { getAllBluetoothDevices() } returns flowOf(allDevices)
        every { getNearbyBluetoothDevices(-70) } returns flowOf(nearbyDevices)

        viewModel = BluetoothViewModel(getAllBluetoothDevices, getNearbyBluetoothDevices)
        advanceUntilIdle()

        // When
        viewModel.findNearbyDevices()
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.devices.value.size)
    }
}
