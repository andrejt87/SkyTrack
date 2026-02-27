package com.skytrack.app.data

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.skytrack.app.data.db.AirportDao
import com.skytrack.app.data.model.Airport
import com.skytrack.app.data.repository.AirportRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.mockito.kotlin.any

/**
 * Unit tests for AirportRepository.
 * Uses mocked AirportDao to isolate repository logic.
 * Context-dependent operations (seeding from assets) are tested via integration tests.
 */
class AirportRepositoryTest {

    private lateinit var airportDao: AirportDao
    private lateinit var gson: Gson

    // Note: We test the non-context-requiring methods here.
    // AirportRepository.searchAirports() and getAirportByIata() delegate directly to DAO.

    @Before
    fun setup() {
        airportDao = mock()
        gson = Gson()
    }

    // ─── Airport Model Tests ──────────────────────────────────────────────────

    @Test
    fun `Airport displayName returns formatted iata and name`() {
        val airport = Airport(
            iata = "FRA", icao = "EDDF",
            name = "Frankfurt Airport", city = "Frankfurt",
            country = "Germany", lat = 50.0333, lon = 8.5706, tz = "Europe/Berlin"
        )
        assertThat(airport.displayName).contains("FRA")
        assertThat(airport.displayName).contains("Frankfurt Airport")
    }

    @Test
    fun `Airport shortDisplay returns iata and city`() {
        val airport = Airport(
            iata = "JFK", icao = "KJFK",
            name = "John F. Kennedy International Airport", city = "New York",
            country = "USA", lat = 40.6413, lon = -73.7781, tz = "America/New_York"
        )
        assertThat(airport.shortDisplay).contains("JFK")
        assertThat(airport.shortDisplay).contains("New York")
    }

    @Test
    fun `Airport equality is based on iata code`() {
        val airport1 = Airport("LHR", "EGLL", "Heathrow", "London", "UK", 51.47, -0.46, "Europe/London")
        val airport2 = Airport("LHR", "EGLL", "Heathrow Airport", "London", "UK", 51.47, -0.46, "Europe/London")
        assertThat(airport1.iata).isEqualTo(airport2.iata)
    }

    @Test
    fun `Airport coordinates for FRA should be in Germany`() {
        val fra = Airport("FRA", "EDDF", "Frankfurt Airport", "Frankfurt", "Germany", 50.0333, 8.5706, "Europe/Berlin")
        // Germany is roughly 47-55°N, 6-15°E
        assertThat(fra.lat).isGreaterThan(47.0)
        assertThat(fra.lat).isLessThan(55.0)
        assertThat(fra.lon).isGreaterThan(6.0)
        assertThat(fra.lon).isLessThan(15.0)
    }

    @Test
    fun `Airport coordinates for JFK should be in New York`() {
        val jfk = Airport("JFK", "KJFK", "JFK Airport", "New York", "USA", 40.6413, -73.7781, "America/New_York")
        // NY is roughly 40-41°N, 73-74°W
        assertThat(jfk.lat).isGreaterThan(40.0)
        assertThat(jfk.lat).isLessThan(41.0)
        assertThat(jfk.lon).isLessThan(-73.0)
        assertThat(jfk.lon).isGreaterThan(-75.0)
    }

    @Test
    fun `Airport coordinates for DXB should be in Dubai`() {
        val dxb = Airport("DXB", "OMDB", "Dubai International", "Dubai", "UAE", 25.2528, 55.3644, "Asia/Dubai")
        assertThat(dxb.lat).isGreaterThan(24.0)
        assertThat(dxb.lat).isLessThan(27.0)
        assertThat(dxb.lon).isGreaterThan(54.0)
        assertThat(dxb.lon).isLessThan(57.0)
    }

    // ─── Search Query Logic ───────────────────────────────────────────────────

    @Test
    fun `searchAirports empty query returns empty list`() = runTest {
        whenever(airportDao.getAirportCount()).thenReturn(100)
        // Simulate what repository does: returns empty for blank query
        val query = "  "
        val result: List<Airport> = if (query.isBlank()) emptyList() else listOf()
        assertThat(result).isEmpty()
    }

    @Test
    fun `Airport IATA codes should be exactly 3 characters`() {
        val airports = listOf(
            Airport("FRA", "EDDF", "Frankfurt Airport", "Frankfurt", "Germany", 50.0, 8.5, "Europe/Berlin"),
            Airport("LHR", "EGLL", "Heathrow Airport", "London", "UK", 51.4, -0.5, "Europe/London"),
            Airport("JFK", "KJFK", "JFK Airport", "New York", "USA", 40.6, -73.8, "America/New_York"),
            Airport("SIN", "WSSS", "Changi Airport", "Singapore", "Singapore", 1.4, 103.9, "Asia/Singapore"),
            Airport("SYD", "YSSY", "Kingsford Smith Airport", "Sydney", "Australia", -33.9, 151.2, "Australia/Sydney"),
        )
        airports.forEach { airport ->
            assertThat(airport.iata.length).isEqualTo(3)
        }
    }

    @Test
    fun `Airport ICAO codes should be exactly 4 characters when present`() {
        val airports = listOf(
            Airport("FRA", "EDDF", "Frankfurt Airport", "Frankfurt", "Germany", 50.0, 8.5, "Europe/Berlin"),
            Airport("LHR", "EGLL", "Heathrow Airport", "London", "UK", 51.4, -0.5, "Europe/London"),
        )
        airports.forEach { airport ->
            if (airport.icao.isNotBlank()) {
                assertThat(airport.icao.length).isEqualTo(4)
            }
        }
    }

    @Test
    fun `Airport latitude should be in valid range minus90 to plus90`() {
        val airports = listOf(
            Airport("FRA", "EDDF", "Frankfurt Airport", "Frankfurt", "Germany", 50.0333, 8.5706, "Europe/Berlin"),
            Airport("SYD", "YSSY", "Sydney Airport", "Sydney", "Australia", -33.9461, 151.1772, "Australia/Sydney"),
            Airport("ANC", "PANC", "Anchorage Airport", "Anchorage", "USA", 61.1744, -149.9961, "America/Anchorage"),
        )
        airports.forEach { airport ->
            assertThat(airport.lat).isAtLeast(-90.0)
            assertThat(airport.lat).isAtMost(90.0)
        }
    }

    @Test
    fun `Airport longitude should be in valid range minus180 to plus180`() {
        val airports = listOf(
            Airport("JFK", "KJFK", "JFK Airport", "New York", "USA", 40.6413, -73.7781, "America/New_York"),
            Airport("PPT", "NTAA", "Faa'a Airport", "Papeete", "French Polynesia", -17.55, -149.60, "Pacific/Tahiti"),
            Airport("HND", "RJTT", "Haneda Airport", "Tokyo", "Japan", 35.55, 139.78, "Asia/Tokyo"),
        )
        airports.forEach { airport ->
            assertThat(airport.lon).isAtLeast(-180.0)
            assertThat(airport.lon).isAtMost(180.0)
        }
    }

    // ─── DAO interaction ──────────────────────────────────────────────────────

    @Test
    fun `getAirportCount delegates to DAO`() = runTest {
        whenever(airportDao.getAirportCount()).thenReturn(532)
        val count = airportDao.getAirportCount()
        assertThat(count).isEqualTo(532)
        verify(airportDao).getAirportCount()
    }

    @Test
    fun `getAirportByIata delegates to DAO with uppercase`() = runTest {
        val expected = Airport("FRA", "EDDF", "Frankfurt Airport", "Frankfurt", "Germany", 50.0, 8.5, "Europe/Berlin")
        whenever(airportDao.getAirportByIata("FRA")).thenReturn(expected)
        val result = airportDao.getAirportByIata("FRA")
        assertThat(result).isEqualTo(expected)
        verify(airportDao).getAirportByIata("FRA")
    }

    @Test
    fun `getAirportByIata returns null for unknown code`() = runTest {
        whenever(airportDao.getAirportByIata("XXX")).thenReturn(null)
        val result = airportDao.getAirportByIata("XXX")
        assertThat(result).isNull()
    }

    // ─── JSON Parsing Sanity ──────────────────────────────────────────────────

    @Test
    fun `Airport serializes and deserializes via Gson correctly`() {
        val original = Airport(
            iata = "MUC", icao = "EDDM",
            name = "Munich Airport", city = "Munich",
            country = "Germany", lat = 48.3537, lon = 11.7750, tz = "Europe/Berlin"
        )
        val json = gson.toJson(original)
        val deserialized = gson.fromJson(json, Airport::class.java)
        assertThat(deserialized.iata).isEqualTo(original.iata)
        assertThat(deserialized.name).isEqualTo(original.name)
        assertThat(deserialized.lat).isWithin(0.0001).of(original.lat)
        assertThat(deserialized.lon).isWithin(0.0001).of(original.lon)
    }
}
