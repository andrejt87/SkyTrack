package com.skytrack.core.math

/**
 * Conversion utilities for aviation and distance units.
 */
object UnitConverter {
    // Distance
    fun kmToMiles(km: Double): Double = km * 0.621371
    fun milesToKm(miles: Double): Double = miles / 0.621371
    fun kmToNauticalMiles(km: Double): Double = km * 0.539957
    fun nauticalMilesToKm(nm: Double): Double = nm / 0.539957

    // Speed
    fun kmhToKnots(kmh: Double): Double = kmh * 0.539957
    fun knotsToKmh(knots: Double): Double = knots / 0.539957
    fun kmhToMph(kmh: Double): Double = kmh * 0.621371
    fun mphToKmh(mph: Double): Double = mph / 0.621371

    // Altitude
    fun metersToFeet(m: Double): Double = m * 3.28084
    fun feetToMeters(ft: Double): Double = ft / 3.28084

    // Pressure
    fun hpaToInHg(hpa: Double): Double = hpa * 0.02953
    fun inHgToHpa(inHg: Double): Double = inHg / 0.02953

    // Temperature
    fun celsiusToFahrenheit(c: Double): Double = c * 9.0 / 5.0 + 32.0
    fun fahrenheitToCelsius(f: Double): Double = (f - 32.0) * 5.0 / 9.0
}
