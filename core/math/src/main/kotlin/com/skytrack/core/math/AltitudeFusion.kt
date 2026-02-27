package com.skytrack.core.math

/**
 * Fuses GPS altitude with barometer pressure to produce a more accurate altitude reading.
 * Uses a Kalman filter to combine the two sources.
 */
class AltitudeFusion {

    private val kalmanFilter = KalmanFilter(processNoise = 0.1, measurementNoise = 2.0)

    /**
     * Calculate altitude from barometric pressure using the international barometric formula.
     * Assumes standard sea-level pressure of 1013.25 hPa.
     */
    fun pressureToAltitude(pressureHPa: Double, seaLevelPressureHPa: Double = 1013.25): Double {
        return 44330.0 * (1.0 - Math.pow(pressureHPa / seaLevelPressureHPa, 1.0 / 5.255))
    }

    /**
     * Fuse GPS and barometer altitude readings.
     * @param gpsAltitudeM GPS altitude in meters (nullable if no fix)
     * @param pressureHPa barometric pressure in hPa (nullable if no sensor)
     * @return fused altitude in meters
     */
    fun fuse(gpsAltitudeM: Double?, pressureHPa: Double?): Double {
        val baroAltitude = pressureHPa?.let { pressureToAltitude(it) }

        return when {
            gpsAltitudeM != null && baroAltitude != null -> {
                // Both available: use Kalman filter with weighted average
                val weighted = (gpsAltitudeM * 0.3 + baroAltitude * 0.7)
                kalmanFilter.update(weighted)
            }
            gpsAltitudeM != null -> kalmanFilter.update(gpsAltitudeM)
            baroAltitude != null -> kalmanFilter.update(baroAltitude)
            else -> kalmanFilter.currentEstimate
        }
    }

    fun reset() {
        kalmanFilter.reset()
    }
}
