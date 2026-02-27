package com.skytrack.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.skytrack.domain.model.*
import com.skytrack.domain.repository.FlightRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class GetFlightStatsUseCaseTest {

    private lateinit var useCase: GetFlightStatsUseCase
    private val flightRepository: FlightRepository = mockk()

    private val jfk = Airport(
        iata = "JFK", icao = "KJFK", name = "JFK", city = "New York",
        country = "US", latitude = 40.6413, longitude = -73.7781, elevation = 4
    )
    private val lhr = Airport(
        iata = "LHR", icao = "EGLL", name = "Heathrow", city = "London",
        country = "GB", latitude = 51.47, longitude = -0.4543, elevation = 25
    )
    private val fra = Airport(
        iata = "FRA", icao = "EDDF", name = "Frankfurt", city = "Frankfurt",
        country = "DE", latitude = 50.0333, longitude = 8.5706, elevation = 111
    )

    @Before
    fun setUp() {
        useCase = GetFlightStatsUseCase(flightRepository)
    }

    @Test
    fun `empty flight list returns default stats`() = runTest {
        coEvery { flightRepository.getAllFlights() } returns emptyList()

        val stats = useCase.execute()

        assertThat(stats.totalFlights).isEqualTo(0)
        assertThat(stats.totalDistanceKm).isEqualTo(0.0)
        assertThat(stats.totalDurationHours).isEqualTo(0.0)
        assertThat(stats.topAirports).isEmpty()
    }

    @Test
    fun `single flight calculates stats correctly`() = runTest {
        val flight = Flight(
            id = "1", departure = jfk, arrival = lhr,
            config = FlightConfig("JFK", "LHR", totalDistanceKm = 5540.0),
            log = FlightLog(totalDurationSeconds = 25200), // 7 hours
            createdAt = Instant.now()
        )
        coEvery { flightRepository.getAllFlights() } returns listOf(flight)

        val stats = useCase.execute()

        assertThat(stats.totalFlights).isEqualTo(1)
        assertThat(stats.totalDistanceKm).isWithin(1.0).of(5540.0)
        assertThat(stats.totalDurationHours).isEqualTo(7.0)
    }

    @Test
    fun `multiple flights aggregate correctly`() = runTest {
        val flights = listOf(
            Flight("1", jfk, lhr, FlightConfig("JFK", "LHR", 5540.0),
                FlightLog(totalDurationSeconds = 25200), createdAt = Instant.now()),
            Flight("2", lhr, fra, FlightConfig("LHR", "FRA", 650.0),
                FlightLog(totalDurationSeconds = 5400), createdAt = Instant.now()),
            Flight("3", fra, jfk, FlightConfig("FRA", "JFK", 6200.0),
                FlightLog(totalDurationSeconds = 28800), createdAt = Instant.now())
        )
        coEvery { flightRepository.getAllFlights() } returns flights

        val stats = useCase.execute()

        assertThat(stats.totalFlights).isEqualTo(3)
        assertThat(stats.totalDistanceKm).isWithin(1.0).of(12390.0)
        assertThat(stats.totalDurationHours).isWithin(0.01).of(16.5) // (25200+5400+28800)/3600
    }

    @Test
    fun `top airports are ranked by visit count`() = runTest {
        // JFK appears 3 times (dep, dep, arr), LHR 2 times, FRA 1 time
        val flights = listOf(
            Flight("1", jfk, lhr, FlightConfig("JFK", "LHR", 5540.0), createdAt = Instant.now()),
            Flight("2", jfk, fra, FlightConfig("JFK", "FRA", 6200.0), createdAt = Instant.now()),
            Flight("3", lhr, jfk, FlightConfig("LHR", "JFK", 5540.0), createdAt = Instant.now())
        )
        coEvery { flightRepository.getAllFlights() } returns flights

        val stats = useCase.execute()

        assertThat(stats.topAirports).isNotEmpty()
        assertThat(stats.topAirports.first().airport.iata).isEqualTo("JFK")
        assertThat(stats.topAirports.first().count).isEqualTo(3)
    }
}
