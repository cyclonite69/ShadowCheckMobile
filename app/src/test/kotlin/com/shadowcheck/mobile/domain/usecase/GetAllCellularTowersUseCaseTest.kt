package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.domain.model.CellularTower
import com.shadowcheck.mobile.domain.repository.CellularTowerRepository
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
class GetAllCellularTowersUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var repository: CellularTowerRepository

    @InjectMockKs
    private lateinit var useCase: GetAllCellularTowersUseCase

    @Test
    fun `invoke should return all cellular towers`() = runTest {
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
            ),
            CellularTower(
                cellId = 12347,
                lac = 3,
                mcc = 310,
                mnc = 410,
                signalStrength = -90,
                latitude = 40.7148,
                longitude = -74.0080,
                timestamp = 3000L
            )
        )
        every { repository.getAllTowers() } returns flowOf(towers)

        // When
        val result = useCase().first()

        // Then
        assertEquals(3, result.size)
    }

    @Test
    fun `invoke should return empty list when no towers available`() = runTest {
        // Given
        every { repository.getAllTowers() } returns flowOf(emptyList())

        // When
        val result = useCase().first()

        // Then
        assertTrue(result.isEmpty())
    }
}
