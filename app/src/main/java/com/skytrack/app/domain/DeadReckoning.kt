package com.skytrack.app.domain

import kotlin.math.*

/**
 * Dead reckoning: estimate current position from last known position,
 * heading, speed, and elapsed time. Used as fallback when GPS signal is lost.
 */
object DeadReckoning {

    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Extrapolates a position given:
     * @param lat           Starting latitude in decimal degrees
     * @param lon           Starting longitude in decimal degrees
     * @param bearingDeg    True heading in degrees [0, 360)
     * @param speedKmh      Ground speed in km/h
     * @param elapsedMs     Time elapsed in milliseconds
     * @return              Estimated (latitude, longitude) as a Pair
     */
    fun extrapolatePosition(
        lat: Double,
        lon: Double,
        bearingDeg: Double,
        speedKmh: Double,
        elapsedMs: Long
    ): Pair<Double, Double> {
        if (speedKmh <= 0 || elapsedMs <= 0) return Pair(lat, lon)

        val distKm = speedKmh * (elapsedMs / 3_600_000.0)
        val angularDist = distKm / EARTH_RADIUS_KM          // radians

        val bearingRad = Math.toRadians(bearingDeg)
        val latRad = Math.toRadians(lat)
        val lonRad = Math.toRadians(lon)

        val newLatRad = asin(
            sin(latRad) * cos(angularDist) +
                    cos(latRad) * sin(angularDist) * cos(bearingRad)
        )
        val newLonRad = lonRad + atan2(
            sin(bearingRad) * sin(angularDist) * cos(latRad),
            cos(angularDist) - sin(latRad) * sin(newLatRad)
        )

        return Pair(Math.toDegrees(newLatRad), Math.toDegrees(newLonRad))
    }

    /**
     * Weighted average of dead-reckoned position and last GPS fix.
     * [gpsTrustWeight] = 1.0 → full trust in GPS, 0.0 → full dead reckoning.
     */
    fun blendWithGps(
        drLat: Double, drLon: Double,
        gpsLat: Double, gpsLon: Double,
        gpsTrustWeight: Double
    ): Pair<Double, Double> {
        val w = gpsTrustWeight.coerceIn(0.0, 1.0)
        return Pair(
            drLat * (1 - w) + gpsLat * w,
            drLon * (1 - w) + gpsLon * w
        )
    }

    /**
     * Detects if aircraft is likely still airborne based on speed and altitude.
     */
    fun isAirborne(speedKmh: Double, altitudeM: Double): Boolean {
        return speedKmh > 100.0 || altitudeM > 1000.0
    }
}
