package com.skytrack.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.skytrack.domain.model.Position
import com.skytrack.domain.repository.LocationRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetCurrentPositionUseCaseTest {

    private lateinit var useCase: GetCurrentPositionUseCase
    private val locationRepository: LocationRepository = mockk()

    private val testPosition = Position(
        latitude = 52.52,
        longitude = 13.405,
        altitudeMeters = 10000.0,
        speedKmh = 850.0,
        headingDegrees = 90f,
        accuracy = 5f
    )

    @Before
    fun setUp() {
        useCase = GetCurrentPositionUseCase(locationRepository)
    }

    @Test
    fun `execute returns position flow from repository`() = runTest {
        every { locationRepository.locationFlow() } returns flowOf(testPosition)

        val position = useCase.execute().first()

        assertThat(position.latitude).isEqualTo(52.52)
        assertThat(position.longitude).isEqualTo(13.405)
        assertThat(position.altitudeMeters).isEqualTo(10000.0)
        assertThat(position.speedKmh).isEqualTo(850.0)
    }

    @Test
    fun `lastKnown returns position from repository`() = runTest {
        coEvery { locationRepository.lastKnownPosition() } returns testPosition

        val position = useCase.lastKnown()

        assertThat(position).isNotNull()
        assertThat(position!!.latitude).isEqualTo(52.52)
    }

    @Test
    fun `lastKnown returns null when no position available`() = runTest {
        coEvery { locationRepository.lastKnownPosition() } returns null

        val position = useCase.lastKnown()

        assertThat(position).isNull()
    }
}
