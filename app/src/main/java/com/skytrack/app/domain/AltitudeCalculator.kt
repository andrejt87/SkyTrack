package com.skytrack.app.domain

import kotlin.math.pow

/**
 * ISA (International Standard Atmosphere) altitude calculations.
 *
 * Standard sea-level conditions:
 *   - Pressure:     1013.25 hPa
 *   - Temperature:  288.15 K  (15 °C)
 *   - Lapse rate:   −6.5 K/km (troposphere, below ~11 km)
 */
object AltitudeCalculator {

    const val SEA_LEVEL_PRESSURE_HPA = 1013.25f
    private const val TEMPERATURE_LAPSE_RATE = 0.0065  // K/m
    private const val SEA_LEVEL_TEMP_K = 288.15        // Kelvin
    private const val EXPONENT = 1.0 / 5.255           // 1/(g / (R * L))

    /**
     * Converts barometric pressure to altitude using the hypsometric formula.
     * Valid up to ~11 000 m (troposphere).
     *
     * h = 44330 × (1 − (p / p₀)^(1/5.255))
     *
     * @param pressureHpa   Measured pressure in hPa
     * @param qnhHpa        Reference pressure at sea level (default: ISA 1013.25 hPa)
     * @return              Altitude in meters
     */
    fun pressureToAltitude(
        pressureHpa: Float,
        qnhHpa: Float = SEA_LEVEL_PRESSURE_HPA
    ): Double {
        return 44_330.0 * (1.0 - (pressureHpa / qnhHpa).toDouble().pow(EXPONENT))
    }

    /**
     * Converts altitude (metres) back to expected pressure (hPa).
     */
    fun altitudeToPressure(altitudeM: Double, qnhHpa: Float = SEA_LEVEL_PRESSURE_HPA): Float {
        return (qnhHpa * (1.0 - altitudeM / 44_330.0).pow(5.255)).toFloat()
    }

    /**
     * Converts altitude in metres to feet.
     */
    fun metresToFeet(metres: Double): Double = metres * 3.28084

    /**
     * Converts altitude in feet to metres.
     */
    fun feetToMetres(feet: Double): Double = feet / 3.28084

    /**
     * Returns the ISA standard temperature at a given altitude (troposphere only).
     * @return temperature in Celsius
     */
    fun isaTemperatureAtAltitude(altitudeM: Double): Double {
        val tempK = SEA_LEVEL_TEMP_K - TEMPERATURE_LAPSE_RATE * altitudeM
        return tempK - 273.15
    }

    /**
     * Returns a human-readable flight level string (e.g. "FL350") for altitudes ≥ 10 000 ft,
     * or feet string (e.g. "9 500 ft") for lower altitudes.
     */
    fun formatAltitude(altitudeM: Double): String {
        val feet = metresToFeet(altitudeM).toInt()
        return if (feet >= 10_000) {
            "FL${feet / 100}"
        } else {
            "${"%,d".format(feet)} ft"
        }
    }

    /**
     * Checks if the aircraft is at cruising altitude (above FL280 = ~8534 m).
     */
    fun isCruising(altitudeM: Double): Boolean = altitudeM >= 8534.0

    /**
     * Checks if the aircraft is below transition altitude (~6000 ft = ~1829 m).
     */
    fun isBelowTransitionAltitude(altitudeM: Double): Boolean = altitudeM < 1829.0
}
