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
            CellularTower("LTE", 310, 260, 12345, 1, 40.7128, -74.0060, -85, 1000L),
            CellularTower("5G", 310, 260, 12346, 2, 40.7138, -74.0070, -75, 2000L),
            CellularTower("LTE", 310, 410, 12347, 3, 40.7148, -74.0080, -90, 3000L)
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
