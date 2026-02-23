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
            CellularTower("LTE", 310, 260, 12345, 1, 40.7128, -74.0060, -85, 1000L),
            CellularTower("5G", 310, 260, 12346, 2, 40.7138, -74.0070, -75, 2000L)
        )
        every { getAllCellularTowers() } returns flowOf(towers)

        // When
        viewModel = CellularViewModel(getAllCellularTowers, getTowersByLocation)
        advanceUntilIdle()

        // Then
        assertEquals(2, viewModel.towers.value.size)
        assertEquals("LTE", viewModel.towers.value[0].type)
    }

    @Test
    fun `loadAllTowers should refresh tower list`() = runTest {
        // Given
        val towers = listOf(
            CellularTower("5G", 310, 260, 12345, 1, 40.7128, -74.0060, -85, 1000L)
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
            CellularTower("LTE", 310, 260, 12345, 1, 40.7128, -74.0060, -85, 1000L)
        )
        val nearbyTowers = listOf(
            CellularTower("5G", 310, 260, 12346, 2, 40.7138, -74.0070, -75, 2000L)
        )
        every { getAllCellularTowers() } returns flowOf(allTowers)
        every { getTowersByLocation(40.7128, -74.0060, 1000.0) } returns flowOf(nearbyTowers)

        viewModel = CellularViewModel(getAllCellularTowers, getTowersByLocation)
        advanceUntilIdle()

        // When
        viewModel.findNearbyTowers(40.7128, -74.0060, 1000.0)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.towers.value.size)
        assertEquals("5G", viewModel.towers.value[0].type)
    }

    @Test
    fun `findNearbyTowers should use default radius when not specified`() = runTest {
        // Given
        val allTowers = emptyList<CellularTower>()
        val nearbyTowers = listOf(
            CellularTower("LTE", 310, 260, 12345, 1, 40.7128, -74.0060, -85, 1000L)
        )
        every { getAllCellularTowers() } returns flowOf(allTowers)
        every { getTowersByLocation(40.7128, -74.0060, 5000.0) } returns flowOf(nearbyTowers)

        viewModel = CellularViewModel(getAllCellularTowers, getTowersByLocation)
        advanceUntilIdle()

        // When
        viewModel.findNearbyTowers(40.7128, -74.0060)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.towers.value.size)
    }
}
