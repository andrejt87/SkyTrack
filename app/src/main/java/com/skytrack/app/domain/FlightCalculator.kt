package com.skytrack.app.domain

import com.skytrack.app.data.model.FlightProgress
import kotlin.math.*

/**
 * Core flight calculation utilities.
 * All calculations use the WGS-84 model approximated as a sphere of radius 6371 km.
 */
object FlightCalculator {

    private const val EARTH_RADIUS_KM = 6371.0

    // ─── Distance ─────────────────────────────────────────────────────────────

    /**
     * Haversine formula: great-circle distance between two coordinates in km.
     */
    fun haversineDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    // ─── Bearing ──────────────────────────────────────────────────────────────

    /**
     * Initial bearing from point 1 → point 2 in degrees [0, 360).
     */
    fun calculateBearing(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val dLon = Math.toRadians(lon2 - lon1)
        val y = sin(dLon) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(dLon)
        return (Math.toDegrees(atan2(y, x)) + 360) % 360
    }

    // ─── Progress ─────────────────────────────────────────────────────────────

    /**
     * Calculates flight progress using Haversine distances plus EMA smoothing.
     *
     * @param alpha EMA smoothing factor: 0.0 = no update, 1.0 = no smoothing
     */
    fun calculateProgress(
        currentLat: Double, currentLon: Double,
        departureLat: Double, departureLon: Double,
        arrivalLat: Double, arrivalLon: Double,
        previousSmoothedProgress: Double = 0.0,
        alpha: Double = 1.0
    ): FlightProgress {
        val totalDistance = haversineDistance(departureLat, departureLon, arrivalLat, arrivalLon)
        val remainingDistance = haversineDistance(currentLat, currentLon, arrivalLat, arrivalLon)
        val rawProgress = if (totalDistance > 0.0) {
            ((totalDistance - remainingDistance) / totalDistance * 100.0).coerceIn(0.0, 100.0)
        } else 0.0
        val bearing = calculateBearing(currentLat, currentLon, arrivalLat, arrivalLon)
        return FlightProgress(
            rawProgressPercent = rawProgress,
            smoothedProgressPercent = rawProgress,
            remainingDistanceKm = remainingDistance,
            totalDistanceKm = totalDistance,
            bearingDeg = bearing,
            currentLat = currentLat,
            currentLon = currentLon
        )
    }

    // ─── ETA ──────────────────────────────────────────────────────────────────

    /**
     * Returns ETA as milliseconds to add to [System.currentTimeMillis], or null if speed ≤ 0.
     */
    fun calculateETA(remainingDistanceKm: Double, speedKmh: Double): Long? {
        if (speedKmh <= 0) return null
        val remainingMs = (remainingDistanceKm / speedKmh * 3_600_000L).toLong()
        return System.currentTimeMillis() + remainingMs
    }

    /**
     * Speed derived from two GPS fixes.
     */
    fun calculateSpeedKmh(
        lat1: Double, lon1: Double, timeMs1: Long,
        lat2: Double, lon2: Double, timeMs2: Long
    ): Double {
        val distKm = haversineDistance(lat1, lon1, lat2, lon2)
        val durationHours = (timeMs2 - timeMs1) / 3_600_000.0
        return if (durationHours > 0) distKm / durationHours else 0.0
    }

    // ─── Intermediate point (great circle) ───────────────────────────────────

    /**
     * Returns a list of [numPoints] points along the great-circle between start and end.
     * Useful for drawing smooth route polylines on the map.
     */
    fun greatCirclePoints(
        startLat: Double, startLon: Double,
        endLat: Double, endLon: Double,
        numPoints: Int = 100
    ): List<Pair<Double, Double>> {
        val points = mutableListOf<Pair<Double, Double>>()
        for (i in 0..numPoints) {
            val fraction = i.toDouble() / numPoints
            val point = intermediatePoint(startLat, startLon, endLat, endLon, fraction)
            points.add(point)
        }
        return points
    }

    private fun intermediatePoint(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double,
        fraction: Double
    ): Pair<Double, Double> {
        val lat1R = Math.toRadians(lat1)
        val lon1R = Math.toRadians(lon1)
        val lat2R = Math.toRadians(lat2)
        val lon2R = Math.toRadians(lon2)
        val d = 2 * asin(
            sqrt(
                sin((lat2R - lat1R) / 2).pow(2) +
                        cos(lat1R) * cos(lat2R) * sin((lon2R - lon1R) / 2).pow(2)
            )
        )
        if (d == 0.0) return Pair(lat1, lon1)
        val a = sin((1 - fraction) * d) / sin(d)
        val b = sin(fraction * d) / sin(d)
        val x = a * cos(lat1R) * cos(lon1R) + b * cos(lat2R) * cos(lon2R)
        val y = a * cos(lat1R) * sin(lon1R) + b * cos(lat2R) * sin(lon2R)
        val z = a * sin(lat1R) + b * sin(lat2R)
        val latR = atan2(z, sqrt(x.pow(2) + y.pow(2)))
        val lonR = atan2(y, x)
        return Pair(Math.toDegrees(latR), Math.toDegrees(lonR))
    }
}
