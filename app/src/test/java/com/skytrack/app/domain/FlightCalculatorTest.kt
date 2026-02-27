package com.skytrack.app.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.math.abs

class FlightCalculatorTest {

    // ─── Haversine Distance ───────────────────────────────────────────────────

    @Test
    fun `haversineDistance FRA to JFK should be approximately 6198 km`() {
        // Frankfurt: 50.0333°N, 8.5706°E
        // JFK:       40.6413°N, 73.7781°W
        val dist = FlightCalculator.haversineDistance(
            lat1 = 50.0333, lon1 = 8.5706,
            lat2 = 40.6413, lon2 = -73.7781
        )
        // Allow ±50 km tolerance
        assertThat(dist).isWithin(50.0).of(6198.0)
    }

    @Test
    fun `haversineDistance same point should be zero`() {
        val dist = FlightCalculator.haversineDistance(51.5, -0.1, 51.5, -0.1)
        assertThat(dist).isWithin(0.001).of(0.0)
    }

    @Test
    fun `haversineDistance LHR to SYD should be approximately 16985 km`() {
        val dist = FlightCalculator.haversineDistance(
            lat1 = 51.4775, lon1 = -0.4614,
            lat2 = -33.9461, lon2 = 151.1772
        )
        // London to Sydney ~16,985 km
        assertThat(dist).isWithin(100.0).of(16985.0)
    }

    @Test
    fun `haversineDistance DXB to LAX should be approximately 13404 km`() {
        val dist = FlightCalculator.haversineDistance(
            lat1 = 25.2528, lon1 = 55.3644,
            lat2 = 33.9425, lon2 = -118.4081
        )
        assertThat(dist).isWithin(100.0).of(13404.0)
    }

    @Test
    fun `haversineDistance antipodal points should be approximately half Earth circumference`() {
        val dist = FlightCalculator.haversineDistance(0.0, 0.0, 0.0, 180.0)
        // Half circumference = π * 6371 ≈ 20015 km
        assertThat(dist).isWithin(1.0).of(20015.0)
    }

    // ─── Bearing ─────────────────────────────────────────────────────────────

    @Test
    fun `calculateBearing due east should be 90 degrees`() {
        val bearing = FlightCalculator.calculateBearing(0.0, 0.0, 0.0, 10.0)
        assertThat(bearing).isWithin(1.0).of(90.0)
    }

    @Test
    fun `calculateBearing due west should be 270 degrees`() {
        val bearing = FlightCalculator.calculateBearing(0.0, 10.0, 0.0, 0.0)
        assertThat(bearing).isWithin(1.0).of(270.0)
    }

    @Test
    fun `calculateBearing due north should be 0 degrees`() {
        val bearing = FlightCalculator.calculateBearing(0.0, 0.0, 10.0, 0.0)
        assertThat(bearing).isWithin(1.0).of(0.0)
    }

    @Test
    fun `calculateBearing due south should be 180 degrees`() {
        val bearing = FlightCalculator.calculateBearing(10.0, 0.0, 0.0, 0.0)
        assertThat(bearing).isWithin(1.0).of(180.0)
    }

    @Test
    fun `calculateBearing FRA to JFK should be roughly westward`() {
        val bearing = FlightCalculator.calculateBearing(
            lat1 = 50.0333, lon1 = 8.5706,
            lat2 = 40.6413, lon2 = -73.7781
        )
        // Great-circle from FRA to JFK is ~282° (northwest then west)
        assertThat(bearing).isWithin(10.0).of(282.0)
    }

    @Test
    fun `calculateBearing result should always be in range 0 to 360`() {
        val testCases = listOf(
            Pair(Pair(51.5, -0.1), Pair(40.6, -73.8)),   // LHR → JFK
            Pair(Pair(35.5, 139.8), Pair(37.5, 127.0)),  // Tokyo → Seoul
            Pair(Pair(-33.9, 151.2), Pair(-27.4, 153.1)) // Sydney → Brisbane
        )
        testCases.forEach { (from, to) ->
            val bearing = FlightCalculator.calculateBearing(from.first, from.second, to.first, to.second)
            assertThat(bearing).isAtLeast(0.0)
            assertThat(bearing).isLessThan(360.0)
        }
    }

    // ─── Progress Calculation ─────────────────────────────────────────────────

    @Test
    fun `calculateProgress at departure should be 0 percent`() {
        val progress = FlightCalculator.calculateProgress(
            currentLat = 50.0333, currentLon = 8.5706,    // = FRA
            departureLat = 50.0333, departureLon = 8.5706,
            arrivalLat = 40.6413, arrivalLon = -73.7781,
            previousSmoothedProgress = 0.0
        )
        assertThat(progress.rawProgressPercent).isWithin(1.0).of(0.0)
    }

    @Test
    fun `calculateProgress at arrival should be 100 percent`() {
        val progress = FlightCalculator.calculateProgress(
            currentLat = 40.6413, currentLon = -73.7781,  // = JFK
            departureLat = 50.0333, departureLon = 8.5706,
            arrivalLat = 40.6413, arrivalLon = -73.7781,
            previousSmoothedProgress = 100.0
        )
        assertThat(progress.rawProgressPercent).isWithin(1.0).of(100.0)
    }

    @Test
    fun `calculateProgress midpoint should be approximately 50 percent`() {
        // Use midpoint of great circle from FRA to JFK
        val midpoints = FlightCalculator.greatCirclePoints(
            50.0333, 8.5706,
            40.6413, -73.7781,
            numPoints = 100
        )
        val mid = midpoints[50]
        val progress = FlightCalculator.calculateProgress(
            currentLat = mid.first, currentLon = mid.second,
            departureLat = 50.0333, departureLon = 8.5706,
            arrivalLat = 40.6413, arrivalLon = -73.7781,
            previousSmoothedProgress = 50.0
        )
        // The midpoint in Cartesian space may not be exactly 50% along orthodrome,
        // but should be within ±5%
        assertThat(progress.rawProgressPercent).isWithin(5.0).of(50.0)
    }

    @Test
    fun `calculateProgress never goes below 0 or above 100`() {
        // Simulate a position past the destination
        val progress = FlightCalculator.calculateProgress(
            currentLat = 38.0, currentLon = -80.0,  // Past JFK
            departureLat = 50.0333, departureLon = 8.5706,
            arrivalLat = 40.6413, arrivalLon = -73.7781,
            previousSmoothedProgress = 99.0
        )
        assertThat(progress.rawProgressPercent).isAtLeast(0.0)
        assertThat(progress.rawProgressPercent).isAtMost(100.0)
        assertThat(progress.smoothedProgressPercent).isAtLeast(0.0)
        assertThat(progress.smoothedProgressPercent).isAtMost(100.0)
    }

    // ─── EMA Smoothing ────────────────────────────────────────────────────────

    @Test
    fun `calculateProgress with alpha 0 should not update smoothed value`() {
        val progress = FlightCalculator.calculateProgress(
            currentLat = 45.0, currentLon = -30.0,
            departureLat = 50.0333, departureLon = 8.5706,
            arrivalLat = 40.6413, arrivalLon = -73.7781,
            previousSmoothedProgress = 30.0,
            alpha = 0.0
        )
        assertThat(progress.smoothedProgressPercent).isWithin(0.01).of(30.0)
    }

    @Test
    fun `calculateProgress with alpha 1 should fully update smoothed value`() {
        val progress = FlightCalculator.calculateProgress(
            currentLat = 50.0333, currentLon = 8.5706,  // at departure = 0%
            departureLat = 50.0333, departureLon = 8.5706,
            arrivalLat = 40.6413, arrivalLon = -73.7781,
            previousSmoothedProgress = 50.0,
            alpha = 1.0
        )
        // With alpha=1, smoothed = raw
        assertThat(progress.smoothedProgressPercent).isWithin(1.0).of(progress.rawProgressPercent)
    }

    @Test
    fun `calculateProgress EMA smoothing should be between previous and raw`() {
        val progress = FlightCalculator.calculateProgress(
            currentLat = 45.0, currentLon = -30.0,
            departureLat = 50.0333, departureLon = 8.5706,
            arrivalLat = 40.6413, arrivalLon = -73.7781,
            previousSmoothedProgress = 20.0,
            alpha = 0.1
        )
        val minVal = minOf(progress.rawProgressPercent, 20.0)
        val maxVal = maxOf(progress.rawProgressPercent, 20.0)
        assertThat(progress.smoothedProgressPercent).isAtLeast(minVal - 0.01)
        assertThat(progress.smoothedProgressPercent).isAtMost(maxVal + 0.01)
    }

    // ─── ETA ─────────────────────────────────────────────────────────────────

    @Test
    fun `calculateETA with positive speed returns future timestamp`() {
        val now = System.currentTimeMillis()
        val eta = FlightCalculator.calculateETA(1000.0, 900.0)
        assertThat(eta).isNotNull()
        assertThat(eta!!).isGreaterThan(now)
    }

    @Test
    fun `calculateETA with zero speed returns null`() {
        val eta = FlightCalculator.calculateETA(1000.0, 0.0)
        assertThat(eta).isNull()
    }

    @Test
    fun `calculateETA with negative speed returns null`() {
        val eta = FlightCalculator.calculateETA(1000.0, -10.0)
        assertThat(eta).isNull()
    }

    @Test
    fun `calculateETA 1000km at 900kmh should be approximately 1 hour from now`() {
        val now = System.currentTimeMillis()
        val eta = FlightCalculator.calculateETA(1000.0, 900.0)!!
        val remainingMs = eta - now
        val expectedMs = (1000.0 / 900.0 * 3_600_000L).toLong()
        assertThat(remainingMs).isWithin(1000L).of(expectedMs)
    }

    @Test
    fun `calculateETA zero remaining distance should be approximately now`() {
        val now = System.currentTimeMillis()
        val eta = FlightCalculator.calculateETA(0.0, 900.0)!!
        assertThat(eta).isWithin(1000L).of(now)
    }

    // ─── Great Circle Points ─────────────────────────────────────────────────

    @Test
    fun `greatCirclePoints returns correct number of points`() {
        val points = FlightCalculator.greatCirclePoints(0.0, 0.0, 10.0, 10.0, numPoints = 50)
        assertThat(points).hasSize(51) // 0..50 inclusive
    }

    @Test
    fun `greatCirclePoints first point should be start coordinates`() {
        val start = Pair(50.0333, 8.5706)
        val points = FlightCalculator.greatCirclePoints(start.first, start.second, 40.6413, -73.7781)
        assertThat(points.first().first).isWithin(0.001).of(start.first)
        assertThat(points.first().second).isWithin(0.001).of(start.second)
    }

    @Test
    fun `greatCirclePoints last point should be end coordinates`() {
        val end = Pair(40.6413, -73.7781)
        val points = FlightCalculator.greatCirclePoints(50.0333, 8.5706, end.first, end.second)
        assertThat(points.last().first).isWithin(0.001).of(end.first)
        assertThat(points.last().second).isWithin(0.001).of(end.second)
    }

    // ─── Speed Calculation ────────────────────────────────────────────────────

    @Test
    fun `calculateSpeedKmh stationary should return zero`() {
        val speed = FlightCalculator.calculateSpeedKmh(
            lat1 = 50.0, lon1 = 10.0, timeMs1 = 0L,
            lat2 = 50.0, lon2 = 10.0, timeMs2 = 60_000L
        )
        assertThat(speed).isWithin(0.01).of(0.0)
    }

    @Test
    fun `calculateSpeedKmh zero time delta should return zero`() {
        val speed = FlightCalculator.calculateSpeedKmh(
            lat1 = 50.0, lon1 = 10.0, timeMs1 = 0L,
            lat2 = 50.5, lon2 = 10.5, timeMs2 = 0L
        )
        assertThat(speed).isWithin(0.01).of(0.0)
    }
}
