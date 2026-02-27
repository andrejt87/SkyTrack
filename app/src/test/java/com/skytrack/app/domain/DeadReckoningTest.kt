package com.skytrack.app.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.math.abs
import kotlin.math.sqrt

class DeadReckoningTest {

    private val earthRadiusKm = 6371.0

    // ─── Basic position extrapolation ─────────────────────────────────────────

    @Test
    fun `extrapolatePosition with zero speed returns same position`() {
        val (lat, lon) = DeadReckoning.extrapolatePosition(
            lat = 50.0, lon = 10.0,
            bearingDeg = 90.0,
            speedKmh = 0.0,
            elapsedMs = 60_000L
        )
        assertThat(lat).isWithin(0.0001).of(50.0)
        assertThat(lon).isWithin(0.0001).of(10.0)
    }

    @Test
    fun `extrapolatePosition with zero elapsed time returns same position`() {
        val (lat, lon) = DeadReckoning.extrapolatePosition(
            lat = 50.0, lon = 10.0,
            bearingDeg = 90.0,
            speedKmh = 900.0,
            elapsedMs = 0L
        )
        assertThat(lat).isWithin(0.0001).of(50.0)
        assertThat(lon).isWithin(0.0001).of(10.0)
    }

    @Test
    fun `extrapolatePosition heading east moves longitude eastward`() {
        val (lat, lon) = DeadReckoning.extrapolatePosition(
            lat = 0.0, lon = 0.0,
            bearingDeg = 90.0,  // due east
            speedKmh = 900.0,
            elapsedMs = 3_600_000L  // 1 hour
        )
        // At the equator: 900 km east ≈ 900/111.195 ≈ 8.1° east
        assertThat(lat).isWithin(0.1).of(0.0)        // lat unchanged
        assertThat(lon).isGreaterThan(0.0)             // lon increased
        assertThat(lon).isWithin(2.0).of(8.1)
    }

    @Test
    fun `extrapolatePosition 1 minute at 900 kmh heading 270 degrees west`() {
        // 900 km/h × (1/60) h = 15 km west of starting point at 51.5°N, 0.0°E
        val (newLat, newLon) = DeadReckoning.extrapolatePosition(
            lat = 51.5, lon = 0.0,
            bearingDeg = 270.0,  // due west
            speedKmh = 900.0,
            elapsedMs = 60_000L  // 1 minute
        )
        // Distance should be ~15 km
        val distKm = FlightCalculator.haversineDistance(51.5, 0.0, newLat, newLon)
        assertThat(distKm).isWithin(0.5).of(15.0)
        // Latitude should barely change
        assertThat(newLat).isWithin(0.1).of(51.5)
        // Longitude should decrease (west)
        assertThat(newLon).isLessThan(0.0)
    }

    @Test
    fun `extrapolatePosition heading north increases latitude`() {
        val (newLat, newLon) = DeadReckoning.extrapolatePosition(
            lat = 0.0, lon = 0.0,
            bearingDeg = 0.0,  // due north
            speedKmh = 1000.0,
            elapsedMs = 3_600_000L  // 1 hour
        )
        // 1000 km north from equator ≈ 1000/111.195 ≈ 8.99° north
        assertThat(newLat).isGreaterThan(0.0)
        assertThat(newLat).isWithin(1.0).of(9.0)
        assertThat(newLon).isWithin(0.1).of(0.0)
    }

    @Test
    fun `extrapolatePosition heading south decreases latitude`() {
        val (newLat, newLon) = DeadReckoning.extrapolatePosition(
            lat = 10.0, lon = 0.0,
            bearingDeg = 180.0,  // due south
            speedKmh = 600.0,
            elapsedMs = 3_600_000L
        )
        assertThat(newLat).isLessThan(10.0)
        assertThat(newLon).isWithin(0.1).of(0.0)
    }

    @Test
    fun `extrapolatePosition 900 kmh for 30 minutes travels approximately 450 km`() {
        val (newLat, newLon) = DeadReckoning.extrapolatePosition(
            lat = 50.0, lon = 8.5,
            bearingDeg = 270.0,
            speedKmh = 900.0,
            elapsedMs = 1_800_000L  // 30 minutes
        )
        val distKm = FlightCalculator.haversineDistance(50.0, 8.5, newLat, newLon)
        assertThat(distKm).isWithin(5.0).of(450.0)
    }

    @Test
    fun `extrapolatePosition diagonal northeast at 45 degrees`() {
        val (newLat, newLon) = DeadReckoning.extrapolatePosition(
            lat = 0.0, lon = 0.0,
            bearingDeg = 45.0,  // NE
            speedKmh = 800.0,
            elapsedMs = 3_600_000L
        )
        // Should move both north and east
        assertThat(newLat).isGreaterThan(0.0)
        assertThat(newLon).isGreaterThan(0.0)
    }

    // ─── GPS Blending ─────────────────────────────────────────────────────────

    @Test
    fun `blendWithGps weight 1 returns GPS position`() {
        val (lat, lon) = DeadReckoning.blendWithGps(
            drLat = 50.0, drLon = 10.0,
            gpsLat = 51.0, gpsLon = 11.0,
            gpsTrustWeight = 1.0
        )
        assertThat(lat).isWithin(0.0001).of(51.0)
        assertThat(lon).isWithin(0.0001).of(11.0)
    }

    @Test
    fun `blendWithGps weight 0 returns dead reckoning position`() {
        val (lat, lon) = DeadReckoning.blendWithGps(
            drLat = 50.0, drLon = 10.0,
            gpsLat = 51.0, gpsLon = 11.0,
            gpsTrustWeight = 0.0
        )
        assertThat(lat).isWithin(0.0001).of(50.0)
        assertThat(lon).isWithin(0.0001).of(10.0)
    }

    @Test
    fun `blendWithGps weight 0_5 returns midpoint`() {
        val (lat, lon) = DeadReckoning.blendWithGps(
            drLat = 50.0, drLon = 10.0,
            gpsLat = 52.0, gpsLon = 12.0,
            gpsTrustWeight = 0.5
        )
        assertThat(lat).isWithin(0.001).of(51.0)
        assertThat(lon).isWithin(0.001).of(11.0)
    }

    @Test
    fun `blendWithGps clamps weight above 1 to 1`() {
        val (lat, lon) = DeadReckoning.blendWithGps(
            drLat = 50.0, drLon = 10.0,
            gpsLat = 51.0, gpsLon = 11.0,
            gpsTrustWeight = 2.0  // should be clamped to 1.0
        )
        assertThat(lat).isWithin(0.0001).of(51.0)
        assertThat(lon).isWithin(0.0001).of(11.0)
    }

    @Test
    fun `blendWithGps clamps weight below 0 to 0`() {
        val (lat, lon) = DeadReckoning.blendWithGps(
            drLat = 50.0, drLon = 10.0,
            gpsLat = 51.0, gpsLon = 11.0,
            gpsTrustWeight = -0.5  // should be clamped to 0.0
        )
        assertThat(lat).isWithin(0.0001).of(50.0)
        assertThat(lon).isWithin(0.0001).of(10.0)
    }

    // ─── Airborne detection ───────────────────────────────────────────────────

    @Test
    fun `isAirborne returns true for cruise speed`() {
        assertThat(DeadReckoning.isAirborne(speedKmh = 850.0, altitudeM = 11000.0)).isTrue()
    }

    @Test
    fun `isAirborne returns true for high altitude even at zero speed`() {
        assertThat(DeadReckoning.isAirborne(speedKmh = 0.0, altitudeM = 2000.0)).isTrue()
    }

    @Test
    fun `isAirborne returns false for ground-level zero speed`() {
        assertThat(DeadReckoning.isAirborne(speedKmh = 0.0, altitudeM = 50.0)).isFalse()
    }

    @Test
    fun `isAirborne returns true for taxi speed 50 kmh`() {
        assertThat(DeadReckoning.isAirborne(speedKmh = 50.0, altitudeM = 100.0)).isFalse()
    }

    @Test
    fun `isAirborne returns true when speed exceeds 100 kmh`() {
        assertThat(DeadReckoning.isAirborne(speedKmh = 101.0, altitudeM = 0.0)).isTrue()
    }
}
