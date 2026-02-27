package com.skytrack.core.math

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UnitConverterTest {

    @Test
    fun `metersToFeet converts correctly`() {
        // 1 meter = 3.28084 feet
        val feet = UnitConverter.metersToFeet(1.0)
        assertThat(feet).isWithin(0.01).of(3.28084)
    }

    @Test
    fun `feetToMeters converts correctly`() {
        val meters = UnitConverter.feetToMeters(3.28084)
        assertThat(meters).isWithin(0.01).of(1.0)
    }

    @Test
    fun `kmhToKnots converts correctly`() {
        // 1 km/h = 0.539957 knots
        val knots = UnitConverter.kmhToKnots(1.0)
        assertThat(knots).isWithin(0.001).of(0.539957)
    }

    @Test
    fun `knotsToKmh converts correctly`() {
        val kmh = UnitConverter.knotsToKmh(1.0)
        assertThat(kmh).isWithin(0.001).of(1.852)
    }

    @Test
    fun `kmhToMps converts correctly`() {
        // 3.6 km/h = 1 m/s
        val mps = UnitConverter.kmhToMps(3.6)
        assertThat(mps).isWithin(0.001).of(1.0)
    }

    @Test
    fun `mpsToKmh converts correctly`() {
        val kmh = UnitConverter.mpsToKmh(1.0)
        assertThat(kmh).isWithin(0.001).of(3.6)
    }

    @Test
    fun `kilometersToMiles converts correctly`() {
        // 1 km = 0.621371 miles
        val miles = UnitConverter.kilometersToMiles(1.0)
        assertThat(miles).isWithin(0.001).of(0.621371)
    }

    @Test
    fun `milesToKilometers converts correctly`() {
        val km = UnitConverter.milesToKilometers(1.0)
        assertThat(km).isWithin(0.001).of(1.60934)
    }

    @Test
    fun `celsiusToFahrenheit converts correctly`() {
        assertThat(UnitConverter.celsiusToFahrenheit(0.0)).isWithin(0.01).of(32.0)
        assertThat(UnitConverter.celsiusToFahrenheit(100.0)).isWithin(0.01).of(212.0)
    }

    @Test
    fun `fahrenheitToCelsius converts correctly`() {
        assertThat(UnitConverter.fahrenheitToCelsius(32.0)).isWithin(0.01).of(0.0)
        assertThat(UnitConverter.fahrenheitToCelsius(212.0)).isWithin(0.01).of(100.0)
    }

    @Test
    fun `roundtrip metersToFeet is identity`() {
        val original = 10000.0
        val result = UnitConverter.feetToMeters(UnitConverter.metersToFeet(original))
        assertThat(result).isWithin(0.01).of(original)
    }

    @Test
    fun `roundtrip kmhToKnots is identity`() {
        val original = 900.0
        val result = UnitConverter.knotsToKmh(UnitConverter.kmhToKnots(original))
        assertThat(result).isWithin(0.01).of(original)
    }

    @Test
    fun `zero values convert to zero`() {
        assertThat(UnitConverter.metersToFeet(0.0)).isEqualTo(0.0)
        assertThat(UnitConverter.kmhToKnots(0.0)).isEqualTo(0.0)
        assertThat(UnitConverter.kilometersToMiles(0.0)).isEqualTo(0.0)
    }

    @Test
    fun `negative values handled correctly`() {
        val feet = UnitConverter.metersToFeet(-100.0)
        assertThat(feet).isWithin(0.1).of(-328.084)
    }

    @Test
    fun `hPaToMmHg converts correctly`() {
        // 1 hPa = 0.750062 mmHg
        val mmhg = UnitConverter.hPaToMmHg(1.0)
        assertThat(mmhg).isWithin(0.001).of(0.750062)
    }
}
