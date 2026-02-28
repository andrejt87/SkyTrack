package com.skytrack.app.data.model

/**
 * Represents the real-time flight progress state.
 * Updated frequently by the domain layer using GPS + Haversine calculations.
 */
data class FlightProgress(
    /** Raw progress percentage (0-100), unsmoothed */
    val rawProgressPercent: Double = 0.0,

    /** EMA-smoothed progress percentage (0-100) */
    val smoothedProgressPercent: Double = 0.0,

    /** Distance remaining to destination in kilometers */
    val remainingDistanceKm: Double = 0.0,

    /** Total great-circle distance in kilometers */
    val totalDistanceKm: Double = 0.0,

    /** Current ground speed in km/h (from GPS) */
    val groundSpeedKmh: Double = 0.0,

    /** Current bearing in degrees (0-360) */
    val bearingDeg: Double = 0.0,

    /** Current altitude in meters (from barometer or GPS) */
    val altitudeM: Double = 0.0,

    /** Estimated time of arrival in milliseconds since epoch, null if speed unknown */
    val etaMs: Long? = null,

    /** Current GPS latitude */
    val currentLat: Double = 0.0,

    /** Current GPS longitude */
    val currentLon: Double = 0.0,

    /** GPS accuracy in meters */
    val gpsAccuracyM: Float = 0f,

    /** Whether barometer data is available */
    val barometerAvailable: Boolean = false,

    /** Raw barometric pressure in hPa */
    val pressureHpa: Float = 1013.25f
) {
    /** Distance already covered in km */
    val coveredDistanceKm: Double
        get() = totalDistanceKm - remainingDistanceKm

    /** ETA formatted as duration string (e.g. "2h 45m") */
    val etaFormatted: String?
        get() {
            val eta = etaMs ?: return null
            val remainingMs = eta - System.currentTimeMillis()
            if (remainingMs <= 0) return "Arriving"
            val totalMin = remainingMs / 60_000
            val hours = totalMin / 60
            val minutes = totalMin % 60
            return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }

    /** Altitude formatted — respects metric preference */
    fun altitudeFormatted(metric: Boolean = useMetric): String {
        return if (metric) {
            "${"%,d".format(altitudeM.toInt())} m"
        } else {
            val ft = (altitudeM * 3.28084).toInt()
            if (ft >= 10000) "FL${ft / 100}" else "${"%,d".format(ft)} ft"
        }
    }

    /** For backwards compat — uses global setting */
    val altitudeFormatted: String
        get() = altitudeFormatted(useMetric)

    /** Speed formatted — respects metric preference */
    fun speedFormatted(metric: Boolean = useMetric): String {
        return if (metric) "${groundSpeedKmh.toInt()} km/h"
        else "${(groundSpeedKmh * 0.539957).toInt()} kts"
    }

    /** For backwards compat */
    val speedFormatted: String
        get() = speedFormatted(useMetric)

    companion object {
        /** Global metric preference — set from Application/Activity */
        var useMetric: Boolean = true
    }
}
