package com.skytrack.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.skytrack.domain.model.*
import com.skytrack.domain.repository.FlightRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ObserveActiveFlightUseCaseTest {

    private val flightRepository: FlightRepository = mockk()

    private val jfk = Airport(
        iata = "JFK", icao = "KJFK", name = "JFK", city = "New York",
        country = "US", latitude = 40.6413, longitude = -73.7781, elevation = 4
    )
    private val lhr = Airport(
        iata = "LHR", icao = "EGLL", name = "Heathrow", city = "London",
        country = "GB", latitude = 51.47, longitude = -0.4543, elevation = 25
    )

    @Test
    fun `emits active flight from repository`() = runTest {
        val flight = Flight(
            id = "test-1", departure = jfk, arrival = lhr,
            config = FlightConfig("JFK", "LHR", 5540.0),
            state = TrackingState.TRACKING
        )
        every { flightRepository.observeActiveFlight() } returns flowOf(flight)
        val useCase = ObserveActiveFlightUseCase(flightRepository)

        val result = useCase.execute().first()

        assertThat(result).isNotNull()
        assertThat(result!!.id).isEqualTo("test-1")
        assertThat(result.state).isEqualTo(TrackingState.TRACKING)
    }

    @Test
    fun `emits null when no active flight`() = runTest {
        every { flightRepository.observeActiveFlight() } returns flowOf(null)
        val useCase = ObserveActiveFlightUseCase(flightRepository)

        val result = useCase.execute().first()

        assertThat(result).isNull()
    }
}
