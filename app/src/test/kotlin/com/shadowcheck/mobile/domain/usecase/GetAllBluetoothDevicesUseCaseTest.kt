package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.core.model.BluetoothDevice
import com.shadowcheck.mobile.domain.repository.BluetoothDeviceRepository
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
class GetAllBluetoothDevicesUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var repository: BluetoothDeviceRepository

    @InjectMockKs
    private lateinit var useCase: GetAllBluetoothDevicesUseCase

    @Test
    fun `invoke should return all bluetooth devices`() = runTest {
        // Given
        val devices = listOf(
            BluetoothDevice(
                macAddress = "AA:BB:CC:DD:EE:01",
                name = "Device1",
                rssi = -70,
                timestamp = 1000L,
                deviceType = 1
            ),
            BluetoothDevice(
                macAddress = "AA:BB:CC:DD:EE:02",
                name = "Device2",
                rssi = -50,
                timestamp = 2000L,
                deviceType = 2
            ),
            BluetoothDevice(
                macAddress = "AA:BB:CC:DD:EE:03",
                name = "Device3",
                rssi = -60,
                timestamp = 3000L,
                deviceType = 3
            )
        )
        every { repository.getAllDevices() } returns flowOf(devices)

        // When
        val result = useCase().first()

        // Then
        assertEquals(3, result.size)
    }

    @Test
    fun `invoke should return empty list when no devices available`() = runTest {
        // Given
        every { repository.getAllDevices() } returns flowOf(emptyList())

        // When
        val result = useCase().first()

        // Then
        assertTrue(result.isEmpty())
    }
}
