package com.skytrack.core.math

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class AltitudeFusionTest {

    private lateinit var fusion: AltitudeFusion

    @Before
    fun setUp() {
        fusion = AltitudeFusion()
    }

    @Test
    fun `standard pressure at sea level returns approximately zero altitude`() {
        val altitude = fusion.pressureToAltitude(1013.25)
        assertThat(altitude).isWithin(1.0).of(0.0)
    }

    @Test
    fun `lower pressure gives higher altitude`() {
        val altLow = fusion.pressureToAltitude(900.0)
        val altHigh = fusion.pressureToAltitude(800.0)
        assertThat(altHigh).isGreaterThan(altLow)
    }

    @Test
    fun `cruise altitude pressure yields approximately 10000m`() {
        // At ~264 hPa, altitude should be roughly 10000m (FL330)
        val altitude = fusion.pressureToAltitude(264.0)
        assertThat(altitude).isWithin(500.0).of(10000.0)
    }

    @Test
    fun `fuse with only GPS altitude returns kalman-filtered value`() {
        val result = fusion.fuse(gpsAltitudeM = 10000.0, pressureHPa = null)
        // First measurement passes through
        assertThat(result).isWithin(100.0).of(10000.0)
    }

    @Test
    fun `fuse with only barometer returns kalman-filtered altitude`() {
        // Sea level pressure → ~0m altitude
        val result = fusion.fuse(gpsAltitudeM = null, pressureHPa = 1013.25)
        assertThat(result).isWithin(10.0).of(0.0)
    }

    @Test
    fun `fuse with both sources combines weighted`() {
        val result = fusion.fuse(gpsAltitudeM = 10000.0, pressureHPa = 264.0)
        // weighted = 0.3 * GPS + 0.7 * Baro → close to Baro-derived altitude
        assertThat(result).isWithin(1000.0).of(10000.0)
    }

    @Test
    fun `fuse with neither source returns current estimate`() {
        // First prime with a value
        fusion.fuse(gpsAltitudeM = 5000.0, pressureHPa = null)
        // Then call with neither source
        val result = fusion.fuse(gpsAltitudeM = null, pressureHPa = null)
        assertThat(result).isWithin(500.0).of(5000.0)
    }

    @Test
    fun `reset clears filter state`() {
        fusion.fuse(gpsAltitudeM = 10000.0, pressureHPa = null)
        fusion.reset()
        // After reset, next measurement should be returned as-is
        val result = fusion.fuse(gpsAltitudeM = 5000.0, pressureHPa = null)
        assertThat(result).isEqualTo(5000.0) // First measurement after reset
    }

    @Test
    fun `custom sea level pressure affects altitude calculation`() {
        // If sea level pressure is lower, same cabin pressure = lower altitude
        val altStandard = fusion.pressureToAltitude(500.0, seaLevelPressure = 1013.25)
        val altLow = fusion.pressureToAltitude(500.0, seaLevelPressure = 900.0)
        assertThat(altLow).isLessThan(altStandard)
    }
}
