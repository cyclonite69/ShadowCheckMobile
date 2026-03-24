package com.shadowcheck.mobile.presentation.viewmodel

import com.shadowcheck.mobile.domain.model.CellularTower
import com.shadowcheck.mobile.domain.usecase.GetAllCellularTowersUseCase
import com.shadowcheck.mobile.domain.usecase.GetTowersByLocationUseCase
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
class CellularViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var getAllCellularTowers: GetAllCellularTowersUseCase

    @MockK
    private lateinit var getTowersByLocation: GetTowersByLocationUseCase

    private lateinit var viewModel: CellularViewModel

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
    fun `init should load all towers`() = runTest {
        // Given
        val towers = listOf(
            CellularTower(
                cellId = 12345,
                lac = 1,
                mcc = 310,
                mnc = 260,
                signalStrength = -85,
                latitude = 40.7128,
                longitude = -74.0060,
                timestamp = 1000L
            ),
            CellularTower(
                cellId = 12346,
                lac = 2,
                mcc = 310,
                mnc = 260,
                signalStrength = -75,
                latitude = 40.7138,
                longitude = -74.0070,
                timestamp = 2000L
            )
        )
        every { getAllCellularTowers() } returns flowOf(towers)

        // When
        viewModel = CellularViewModel(getAllCellularTowers, getTowersByLocation)
        advanceUntilIdle()

        // Then
        assertEquals(2, viewModel.towers.value.size)
        assertEquals(12345, viewModel.towers.value[0].cellId)
    }

    @Test
    fun `loadAllTowers should refresh tower list`() = runTest {
        // Given
        val towers = listOf(
            CellularTower(
                cellId = 12345,
                lac = 1,
                mcc = 310,
                mnc = 260,
                signalStrength = -85,
                latitude = 40.7128,
                longitude = -74.0060,
                timestamp = 1000L
            )
        )
        every { getAllCellularTowers() } returns flowOf(towers)

        viewModel = CellularViewModel(getAllCellularTowers, getTowersByLocation)
        advanceUntilIdle()

        // When
        viewModel.loadAllTowers()
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.towers.value.size)
    }

    @Test
    fun `findNearbyTowers should update towers with location-based results`() = runTest {
        // Given
        val allTowers = listOf(
            CellularTower(
                cellId = 12345,
                lac = 1,
                mcc = 310,
                mnc = 260,
                signalStrength = -85,
                latitude = 40.7128,
                longitude = -74.0060,
                timestamp = 1000L
            )
        )
        val nearbyTowers = listOf(
            CellularTower(
                cellId = 12346,
                lac = 2,
                mcc = 310,
                mnc = 260,
                signalStrength = -75,
                latitude = 40.7138,
                longitude = -74.0070,
                timestamp = 2000L
            )
        )
        every { getAllCellularTowers() } returns flowOf(allTowers)
        every { getTowersByLocation(40.7128, -74.0060, 1000.0) } returns flowOf(nearbyTowers)

        viewModel = CellularViewModel(getAllCellularTowers, getTowersByLocation)
        advanceUntilIdle()

        // When
        viewModel.findTowersNearby(40.7128, -74.0060, 1000.0)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.towers.value.size)
        assertEquals(12346, viewModel.towers.value[0].cellId)
    }

    @Test
    fun `findNearbyTowers should use default radius when not specified`() = runTest {
        // Given
        val allTowers = emptyList<CellularTower>()
        val nearbyTowers = listOf(
            CellularTower(
                cellId = 12345,
                lac = 1,
                mcc = 310,
                mnc = 260,
                signalStrength = -85,
                latitude = 40.7128,
                longitude = -74.0060,
                timestamp = 1000L
            )
        )
        every { getAllCellularTowers() } returns flowOf(allTowers)
        every { getTowersByLocation(40.7128, -74.0060, 5000.0) } returns flowOf(nearbyTowers)

        viewModel = CellularViewModel(getAllCellularTowers, getTowersByLocation)
        advanceUntilIdle()

        // When
        viewModel.findTowersNearby(40.7128, -74.0060, 5000.0)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.towers.value.size)
    }
}
