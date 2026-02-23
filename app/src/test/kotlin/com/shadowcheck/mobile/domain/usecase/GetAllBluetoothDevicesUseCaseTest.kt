package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.domain.model.BluetoothDevice
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
            BluetoothDevice("Device1", "AA:BB:CC:DD:EE:01", "Classic", -70, 1000L),
            BluetoothDevice("Device2", "AA:BB:CC:DD:EE:02", "BLE", -50, 2000L),
            BluetoothDevice("Device3", "AA:BB:CC:DD:EE:03", "Dual", -60, 3000L)
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
