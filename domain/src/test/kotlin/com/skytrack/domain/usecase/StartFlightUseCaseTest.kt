package com.skytrack.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.skytrack.domain.model.*
import com.skytrack.domain.repository.AirportRepository
import com.skytrack.domain.repository.FlightRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class StartFlightUseCaseTest {

    private lateinit var useCase: StartFlightUseCase
    private val flightRepository: FlightRepository = mockk(relaxed = true)
    private val airportRepository: AirportRepository = mockk()

    private val jfkAirport = Airport(
        iata = "JFK", icao = "KJFK", name = "John F. Kennedy International",
        city = "New York", country = "US",
        latitude = 40.6413, longitude = -73.7781, elevation = 4
    )

    private val lhrAirport = Airport(
        iata = "LHR", icao = "EGLL", name = "London Heathrow",
        city = "London", country = "GB",
        latitude = 51.4700, longitude = -0.4543, elevation = 25
    )

    @Before
    fun setUp() {
        useCase = StartFlightUseCase(flightRepository, airportRepository)
    }

    @Test
    fun `successful flight creation returns Result success`() = runTest {
        coEvery { airportRepository.findByIata("JFK") } returns jfkAirport
        coEvery { airportRepository.findByIata("LHR") } returns lhrAirport
        coEvery { flightRepository.startFlight(any()) } returns "test-id"

        val result = useCase.execute("JFK", "LHR")

        assertThat(result.isSuccess).isTrue()
        val flight = result.getOrThrow()
        assertThat(flight.departure.iata).isEqualTo("JFK")
        assertThat(flight.arrival.iata).isEqualTo("LHR")
        assertThat(flight.state).isEqualTo(TrackingState.ACQUIRING)
        assertThat(flight.config.totalDistanceKm).isGreaterThan(1000.0)
        coVerify { flightRepository.startFlight(any()) }
    }

    @Test
    fun `departure airport not found returns failure`() = runTest {
        coEvery { airportRepository.findByIata("XXX") } returns null
        coEvery { airportRepository.findByIata("LHR") } returns lhrAirport

        val result = useCase.execute("XXX", "LHR")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
        assertThat(result.exceptionOrNull()?.message).contains("XXX")
    }

    @Test
    fun `arrival airport not found returns failure`() = runTest {
        coEvery { airportRepository.findByIata("JFK") } returns jfkAirport
        coEvery { airportRepository.findByIata("XXX") } returns null

        val result = useCase.execute("JFK", "XXX")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("XXX")
    }

    @Test
    fun `same departure and arrival returns failure`() = runTest {
        coEvery { airportRepository.findByIata("JFK") } returns jfkAirport

        val result = useCase.execute("JFK", "JFK")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("departure equals arrival")
    }

    @Test
    fun `flight config contains correct IATA codes`() = runTest {
        coEvery { airportRepository.findByIata("JFK") } returns jfkAirport
        coEvery { airportRepository.findByIata("LHR") } returns lhrAirport
        coEvery { flightRepository.startFlight(any()) } returns "test-id"

        val flight = useCase.execute("JFK", "LHR").getOrThrow()

        assertThat(flight.config.departureIata).isEqualTo("JFK")
        assertThat(flight.config.arrivalIata).isEqualTo("LHR")
        assertThat(flight.config.totalDistanceKm).isWithin(100.0).of(5540.0)
    }

    @Test
    fun `flight id is a valid UUID`() = runTest {
        coEvery { airportRepository.findByIata("JFK") } returns jfkAirport
        coEvery { airportRepository.findByIata("LHR") } returns lhrAirport
        coEvery { flightRepository.startFlight(any()) } returns "test-id"

        val flight = useCase.execute("JFK", "LHR").getOrThrow()

        // UUID format: 8-4-4-4-12 hex digits
        assertThat(flight.id).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
    }
}
