package com.skytrack.app.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AltitudeCalculatorTest {

    // ─── ISA Pressure → Altitude ──────────────────────────────────────────────

    @Test
    fun `pressureToAltitude at sea level pressure should return zero`() {
        val altitude = AltitudeCalculator.pressureToAltitude(1013.25f)
        assertThat(altitude).isWithin(1.0).of(0.0)
    }

    @Test
    fun `pressureToAltitude at 850 hPa should be approximately 1457 m`() {
        // ISA: 850 hPa ≈ 1457 m
        val altitude = AltitudeCalculator.pressureToAltitude(850.0f)
        assertThat(altitude).isWithin(20.0).of(1457.0)
    }

    @Test
    fun `pressureToAltitude at 700 hPa should be approximately 3012 m`() {
        // ISA: 700 hPa ≈ 3012 m
        val altitude = AltitudeCalculator.pressureToAltitude(700.0f)
        assertThat(altitude).isWithin(30.0).of(3012.0)
    }

    @Test
    fun `pressureToAltitude at 500 hPa should be approximately 5574 m`() {
        // ISA: 500 hPa ≈ 5574 m
        val altitude = AltitudeCalculator.pressureToAltitude(500.0f)
        assertThat(altitude).isWithin(50.0).of(5574.0)
    }

    @Test
    fun `pressureToAltitude at 300 hPa should be approximately 9164 m`() {
        // ISA: 300 hPa ≈ 9164 m (around FL300)
        val altitude = AltitudeCalculator.pressureToAltitude(300.0f)
        assertThat(altitude).isWithin(100.0).of(9164.0)
    }

    @Test
    fun `pressureToAltitude at 250 hPa should be approximately 10363 m`() {
        // ISA: 250 hPa ≈ 10363 m (FL340 typical cruise)
        val altitude = AltitudeCalculator.pressureToAltitude(250.0f)
        assertThat(altitude).isWithin(100.0).of(10363.0)
    }

    @Test
    fun `pressureToAltitude should increase as pressure decreases`() {
        val alt1 = AltitudeCalculator.pressureToAltitude(900.0f)
        val alt2 = AltitudeCalculator.pressureToAltitude(800.0f)
        val alt3 = AltitudeCalculator.pressureToAltitude(700.0f)
        assertThat(alt1).isLessThan(alt2)
        assertThat(alt2).isLessThan(alt3)
    }

    @Test
    fun `pressureToAltitude with custom QNH of 1020 should shift altitude down`() {
        val altISA      = AltitudeCalculator.pressureToAltitude(1000.0f, qnhHpa = 1013.25f)
        val altHighQNH  = AltitudeCalculator.pressureToAltitude(1000.0f, qnhHpa = 1020.0f)
        // Higher QNH means same pressure = lower altitude
        assertThat(altHighQNH).isLessThan(altISA)
    }

    // ─── Altitude → Pressure (inverse) ────────────────────────────────────────

    @Test
    fun `altitudeToPressure at zero altitude should return sea level pressure`() {
        val pressure = AltitudeCalculator.altitudeToPressure(0.0)
        assertThat(pressure.toDouble()).isWithin(0.1).of(1013.25)
    }

    @Test
    fun `altitudeToPressure round-trip should recover original altitude`() {
        val originalAltitude = 5000.0
        val pressure = AltitudeCalculator.altitudeToPressure(originalAltitude)
        val recoveredAltitude = AltitudeCalculator.pressureToAltitude(pressure)
        assertThat(recoveredAltitude).isWithin(1.0).of(originalAltitude)
    }

    @Test
    fun `altitudeToPressure at 10000m should return approximately 264 hPa`() {
        val pressure = AltitudeCalculator.altitudeToPressure(10_000.0)
        assertThat(pressure.toDouble()).isWithin(5.0).of(264.0)
    }

    // ─── Unit Conversions ─────────────────────────────────────────────────────

    @Test
    fun `metresToFeet converts correctly`() {
        // 1 metre = 3.28084 feet
        assertThat(AltitudeCalculator.metresToFeet(1.0)).isWithin(0.001).of(3.28084)
        assertThat(AltitudeCalculator.metresToFeet(0.0)).isWithin(0.001).of(0.0)
        assertThat(AltitudeCalculator.metresToFeet(304.8)).isWithin(0.1).of(1000.0)
    }

    @Test
    fun `feetToMetres converts correctly`() {
        assertThat(AltitudeCalculator.feetToMetres(1.0)).isWithin(0.001).of(0.3048)
        assertThat(AltitudeCalculator.feetToMetres(1000.0)).isWithin(0.1).of(304.8)
    }

    @Test
    fun `metresToFeet and feetToMetres are inverse operations`() {
        val original = 8534.0
        val roundTrip = AltitudeCalculator.feetToMetres(AltitudeCalculator.metresToFeet(original))
        assertThat(roundTrip).isWithin(0.01).of(original)
    }

    // ─── ISA Temperature ──────────────────────────────────────────────────────

    @Test
    fun `isaTemperatureAtAltitude at sea level should be 15 degrees Celsius`() {
        val temp = AltitudeCalculator.isaTemperatureAtAltitude(0.0)
        assertThat(temp).isWithin(0.01).of(15.0)
    }

    @Test
    fun `isaTemperatureAtAltitude at 11000m should be approximately minus 56 degrees Celsius`() {
        val temp = AltitudeCalculator.isaTemperatureAtAltitude(11_000.0)
        // ISA at tropopause: -56.5°C
        assertThat(temp).isWithin(1.0).of(-56.5)
    }

    @Test
    fun `isaTemperatureAtAltitude decreases with altitude`() {
        val tempSea    = AltitudeCalculator.isaTemperatureAtAltitude(0.0)
        val tempMid    = AltitudeCalculator.isaTemperatureAtAltitude(5000.0)
        val tempHigh   = AltitudeCalculator.isaTemperatureAtAltitude(10000.0)
        assertThat(tempSea).isGreaterThan(tempMid)
        assertThat(tempMid).isGreaterThan(tempHigh)
    }

    // ─── Altitude Formatting ──────────────────────────────────────────────────

    @Test
    fun `formatAltitude at FL350 should return FL350 string`() {
        // FL350 = 35000 ft = 10668 m
        val formatted = AltitudeCalculator.formatAltitude(10_668.0)
        assertThat(formatted).contains("FL")
        assertThat(formatted).contains("350")
    }

    @Test
    fun `formatAltitude below 10000ft returns feet string`() {
        // 1524 m ≈ 5000 ft
        val formatted = AltitudeCalculator.formatAltitude(1524.0)
        assertThat(formatted).contains("ft")
        assertThat(formatted).doesNotContain("FL")
    }

    @Test
    fun `formatAltitude at zero should return 0 ft`() {
        val formatted = AltitudeCalculator.formatAltitude(0.0)
        assertThat(formatted).contains("ft")
    }

    // ─── Cruise and Transition Detection ─────────────────────────────────────

    @Test
    fun `isCruising returns true at 9000m`() {
        assertThat(AltitudeCalculator.isCruising(9000.0)).isTrue()
    }

    @Test
    fun `isCruising returns false at 5000m`() {
        assertThat(AltitudeCalculator.isCruising(5000.0)).isFalse()
    }

    @Test
    fun `isBelowTransitionAltitude returns true below 1829m`() {
        assertThat(AltitudeCalculator.isBelowTransitionAltitude(1000.0)).isTrue()
    }

    @Test
    fun `isBelowTransitionAltitude returns false above 1829m`() {
        assertThat(AltitudeCalculator.isBelowTransitionAltitude(2000.0)).isFalse()
    }
}
