package com.skytrack.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.skytrack.domain.model.Airport
import com.skytrack.domain.repository.AirportRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SearchAirportUseCaseTest {

    private lateinit var useCase: SearchAirportUseCase
    private val airportRepository: AirportRepository = mockk()

    private val jfk = Airport(
        iata = "JFK", icao = "KJFK", name = "John F. Kennedy International",
        city = "New York", country = "US",
        latitude = 40.6413, longitude = -73.7781, elevation = 4
    )

    @Before
    fun setUp() {
        useCase = SearchAirportUseCase(airportRepository)
    }

    @Test
    fun `query shorter than 2 chars returns empty list`() = runTest {
        val result = useCase.execute("J")
        assertThat(result).isEmpty()
    }

    @Test
    fun `single char query returns empty list`() = runTest {
        val result = useCase.execute("A")
        assertThat(result).isEmpty()
    }

    @Test
    fun `empty query returns empty list`() = runTest {
        val result = useCase.execute("")
        assertThat(result).isEmpty()
    }

    @Test
    fun `valid query delegates to repository`() = runTest {
        coEvery { airportRepository.search("JFK") } returns listOf(jfk)

        val result = useCase.execute("JFK")

        assertThat(result).hasSize(1)
        assertThat(result.first().iata).isEqualTo("JFK")
        coVerify { airportRepository.search("JFK") }
    }

    @Test
    fun `two char query is valid`() = runTest {
        coEvery { airportRepository.search("JF") } returns listOf(jfk)

        val result = useCase.execute("JF")

        assertThat(result).hasSize(1)
    }

    @Test
    fun `findByIata converts to uppercase`() = runTest {
        coEvery { airportRepository.findByIata("JFK") } returns jfk

        val result = useCase.findByIata("jfk")

        assertThat(result).isEqualTo(jfk)
        coVerify { airportRepository.findByIata("JFK") }
    }

    @Test
    fun `findByIata returns null for unknown airport`() = runTest {
        coEvery { airportRepository.findByIata("XXX") } returns null

        val result = useCase.findByIata("XXX")

        assertThat(result).isNull()
    }
}
