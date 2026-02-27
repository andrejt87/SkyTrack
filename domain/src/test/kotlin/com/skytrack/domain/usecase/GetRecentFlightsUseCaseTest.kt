package com.skytrack.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.skytrack.domain.model.*
import com.skytrack.domain.repository.FlightRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant

class GetRecentFlightsUseCaseTest {

    private val flightRepository: FlightRepository = mockk()

    private val airport = Airport(
        iata = "JFK", icao = "KJFK", name = "JFK", city = "NY",
        country = "US", latitude = 40.6413, longitude = -73.7781, elevation = 4
    )

    @Test
    fun `returns flights sorted by createdAt descending`() = runTest {
        val now = Instant.now()
        val flights = listOf(
            Flight("1", airport, airport, FlightConfig("JFK", "JFK", 0.0),
                createdAt = now.minusSeconds(3600)),
            Flight("2", airport, airport, FlightConfig("JFK", "JFK", 0.0),
                createdAt = now),
            Flight("3", airport, airport, FlightConfig("JFK", "JFK", 0.0),
                createdAt = now.minusSeconds(7200))
        )
        coEvery { flightRepository.getAllFlights() } returns flights
        val useCase = GetRecentFlightsUseCase(flightRepository)

        val result = useCase.execute()

        assertThat(result.first().id).isEqualTo("2") // most recent
        assertThat(result.last().id).isEqualTo("3") // oldest
    }

    @Test
    fun `respects limit parameter`() = runTest {
        val flights = (1..20).map {
            Flight("$it", airport, airport, FlightConfig("JFK", "JFK", 0.0),
                createdAt = Instant.now().minusSeconds(it.toLong() * 60))
        }
        coEvery { flightRepository.getAllFlights() } returns flights
        val useCase = GetRecentFlightsUseCase(flightRepository)

        val result = useCase.execute(limit = 5)

        assertThat(result).hasSize(5)
    }

    @Test
    fun `default limit is 10`() = runTest {
        val flights = (1..20).map {
            Flight("$it", airport, airport, FlightConfig("JFK", "JFK", 0.0),
                createdAt = Instant.now().minusSeconds(it.toLong() * 60))
        }
        coEvery { flightRepository.getAllFlights() } returns flights
        val useCase = GetRecentFlightsUseCase(flightRepository)

        val result = useCase.execute()

        assertThat(result).hasSize(10)
    }

    @Test
    fun `empty list returns empty`() = runTest {
        coEvery { flightRepository.getAllFlights() } returns emptyList()
        val useCase = GetRecentFlightsUseCase(flightRepository)

        val result = useCase.execute()

        assertThat(result).isEmpty()
    }
}
