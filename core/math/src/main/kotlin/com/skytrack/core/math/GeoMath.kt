package com.skytrack.core.math

import kotlin.math.*

/**
 * Great-circle (Haversine) calculations for flight tracking.
 * All angles in degrees, distances in kilometers.
 */
object GeoMath {

    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calculate the great-circle distance between two points using Haversine formula.
     * @return distance in kilometers
     */
    fun haversineDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * Calculate the initial bearing from point 1 to point 2.
     * @return bearing in degrees (0-360)
     */
    fun initialBearing(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val dLonRad = Math.toRadians(lon2 - lon1)

        val y = sin(dLonRad) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(dLonRad)
        val bearing = Math.toDegrees(atan2(y, x))
        return (bearing + 360) % 360
    }

    /**
     * Generate intermediate points along a great-circle route.
     * @param numPoints number of points to generate (including start and end)
     * @return list of [lat, lon] pairs
     */
    fun greatCirclePoints(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double,
        numPoints: Int = 100
    ): List<Pair<Double, Double>> {
        val points = mutableListOf<Pair<Double, Double>>()
        val lat1Rad = Math.toRadians(lat1)
        val lon1Rad = Math.toRadians(lon1)
        val lat2Rad = Math.toRadians(lat2)
        val lon2Rad = Math.toRadians(lon2)

        val d = haversineDistance(lat1, lon1, lat2, lon2) / EARTH_RADIUS_KM

        for (i in 0 until numPoints) {
            val f = i.toDouble() / (numPoints - 1)
            val a = sin((1 - f) * d) / sin(d)
            val b = sin(f * d) / sin(d)
            val x = a * cos(lat1Rad) * cos(lon1Rad) + b * cos(lat2Rad) * cos(lon2Rad)
            val y = a * cos(lat1Rad) * sin(lon1Rad) + b * cos(lat2Rad) * sin(lon2Rad)
            val z = a * sin(lat1Rad) + b * sin(lat2Rad)
            val lat = Math.toDegrees(atan2(z, sqrt(x * x + y * y)))
            val lon = Math.toDegrees(atan2(y, x))
            points.add(Pair(lat, lon))
        }
        return points
    }

    /**
     * Calculate cross-track distance (perpendicular distance from a point to the great circle path).
     * @return distance in kilometers (positive = right of path, negative = left)
     */
    fun crossTrackDistance(
        pointLat: Double, pointLon: Double,
        pathStartLat: Double, pathStartLon: Double,
        pathEndLat: Double, pathEndLon: Double
    ): Double {
        val d13 = haversineDistance(pathStartLat, pathStartLon, pointLat, pointLon) / EARTH_RADIUS_KM
        val theta13 = Math.toRadians(initialBearing(pathStartLat, pathStartLon, pointLat, pointLon))
        val theta12 = Math.toRadians(initialBearing(pathStartLat, pathStartLon, pathEndLat, pathEndLon))
        val dxt = asin(sin(d13) * sin(theta13 - theta12))
        return dxt * EARTH_RADIUS_KM
    }
}
