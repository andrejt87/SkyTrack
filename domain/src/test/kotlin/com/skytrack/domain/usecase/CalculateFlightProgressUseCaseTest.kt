package com.skytrack.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.skytrack.domain.model.LatLng
import org.junit.Before
import org.junit.Test

class CalculateFlightProgressUseCaseTest {

    private lateinit var useCase: CalculateFlightProgressUseCase

    // JFK (New York) and LHR (London)
    private val jfk = LatLng(latitude = 40.6413, longitude = -73.7781)
    private val lhr = LatLng(latitude = 51.4700, longitude = -0.4543)

    @Before
    fun setUp() {
        useCase = CalculateFlightProgressUseCase()
    }

    @Test
    fun `progress at departure is zero`() {
        val result = useCase.execute(
            currentPosition = jfk,
            departure = jfk,
            arrival = lhr
        )
        assertThat(result.percentage).isWithin(1f).of(0f)
        assertThat(result.distanceCoveredKm).isWithin(1.0).of(0.0)
    }

    @Test
    fun `progress at arrival is close to 100`() {
        val result = useCase.execute(
            currentPosition = lhr,
            departure = jfk,
            arrival = lhr
        )
        assertThat(result.percentage).isGreaterThan(80f) // EMA smoothing means first call won't reach 100
    }

    @Test
    fun `progress is monotonically increasing`() {
        // Simulate moving from JFK to LHR in steps
        val midpoint = LatLng(latitude = 50.0, longitude = -30.0)
        val result1 = useCase.execute(currentPosition = jfk, departure = jfk, arrival = lhr)
        val result2 = useCase.execute(currentPosition = midpoint, departure = jfk, arrival = lhr)
        // Even if we "go back", monotonic guarantee holds
        val result3 = useCase.execute(currentPosition = jfk, departure = jfk, arrival = lhr)

        assertThat(result2.percentage).isAtLeast(result1.percentage)
        assertThat(result3.percentage).isAtLeast(result2.percentage) // Monotonic: never decreases
    }

    @Test
    fun `total distance is consistent`() {
        val result = useCase.execute(currentPosition = jfk, departure = jfk, arrival = lhr)
        // JFK to LHR is approximately 5540 km
        assertThat(result.totalDistanceKm).isWithin(100.0).of(5540.0)
    }

    @Test
    fun `distance remaining plus covered equals total`() {
        val midpoint = LatLng(latitude = 50.0, longitude = -30.0)
        val result = useCase.execute(currentPosition = midpoint, departure = jfk, arrival = lhr)
        val sum = result.distanceCoveredKm + result.distanceRemainingKm
        assertThat(sum).isWithin(1.0).of(result.totalDistanceKm)
    }

    @Test
    fun `reset clears max progress`() {
        val midpoint = LatLng(latitude = 50.0, longitude = -30.0)
        useCase.execute(currentPosition = midpoint, departure = jfk, arrival = lhr)
        useCase.reset()
        val result = useCase.execute(currentPosition = jfk, departure = jfk, arrival = lhr)
        assertThat(result.percentage).isWithin(1f).of(0f)
    }

    @Test
    fun `haversineKm companion function matches GeoMath range`() {
        val distance = CalculateFlightProgressUseCase.haversineKm(jfk, lhr)
        assertThat(distance).isWithin(100.0).of(5540.0)
    }

    @Test
    fun `same departure and arrival gives zero total distance`() {
        val result = useCase.execute(currentPosition = jfk, departure = jfk, arrival = jfk)
        assertThat(result.totalDistanceKm).isWithin(0.1).of(0.0)
        assertThat(result.percentage).isWithin(0.1f).of(0f)
    }
}
