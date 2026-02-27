package com.skytrack.data.airports

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AirportRepositoryImplTest {

    private lateinit var repository: AirportRepositoryImpl
    private val localDataSource: AirportLocalDataSource = mockk()
    private val remoteDataSource: AirportRemoteDataSource = mockk(relaxed = true)

    private val jfkDto = AirportDto(
        iata = "JFK", icao = "KJFK", name = "John F. Kennedy International",
        city = "New York", country = "US",
        lat = 40.6413, lon = -73.7781, elevation = 4
    )

    private val lhrDto = AirportDto(
        iata = "LHR", icao = "EGLL", name = "London Heathrow",
        city = "London", country = "GB",
        lat = 51.4700, lon = -0.4543, elevation = 25
    )

    @Before
    fun setUp() {
        repository = AirportRepositoryImpl(localDataSource, remoteDataSource)
    }

    @Test
    fun `findByIata returns airport when found`() = runTest {
        coEvery { localDataSource.findByIata("JFK") } returns jfkDto

        val result = repository.findByIata("JFK")

        assertThat(result).isNotNull()
        assertThat(result!!.iata).isEqualTo("JFK")
        assertThat(result.city).isEqualTo("New York")
    }

    @Test
    fun `findByIata returns null for unknown airport`() = runTest {
        coEvery { localDataSource.findByIata("ZZZ") } returns null

        val result = repository.findByIata("ZZZ")

        assertThat(result).isNull()
    }

    @Test
    fun `search returns matching airports`() = runTest {
        coEvery { localDataSource.search("London") } returns listOf(lhrDto)

        val result = repository.search("London")

        assertThat(result).hasSize(1)
        assertThat(result.first().name).contains("Heathrow")
    }

    @Test
    fun `search returns empty list for no matches`() = runTest {
        coEvery { localDataSource.search("ZZZZ") } returns emptyList()

        val result = repository.search("ZZZZ")

        assertThat(result).isEmpty()
    }

    @Test
    fun `dto is mapped correctly to domain model`() = runTest {
        coEvery { localDataSource.findByIata("JFK") } returns jfkDto

        val airport = repository.findByIata("JFK")!!

        assertThat(airport.iata).isEqualTo("JFK")
        assertThat(airport.icao).isEqualTo("KJFK")
        assertThat(airport.latitude).isEqualTo(40.6413)
        assertThat(airport.longitude).isEqualTo(-73.7781)
        assertThat(airport.elevation).isEqualTo(4)
    }
}
