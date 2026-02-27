package com.skytrack.core.math

/**
 * Simple 1D Kalman filter for altitude sensor fusion (GPS + Barometer).
 * ADR-009: Kalman-Filter für Sensor-Fusion.
 */
class KalmanFilter(
    private var processNoise: Double = 0.01,
    private var measurementNoise: Double = 1.0
) {
    private var estimate: Double = 0.0
    private var errorCovariance: Double = 1.0
    private var isInitialized: Boolean = false

    fun update(measurement: Double): Double {
        if (!isInitialized) {
            estimate = measurement
            isInitialized = true
            return estimate
        }

        // Prediction step
        val predictedEstimate = estimate
        val predictedErrorCovariance = errorCovariance + processNoise

        // Update step
        val kalmanGain = predictedErrorCovariance / (predictedErrorCovariance + measurementNoise)
        estimate = predictedEstimate + kalmanGain * (measurement - predictedEstimate)
        errorCovariance = (1 - kalmanGain) * predictedErrorCovariance

        return estimate
    }

    fun reset() {
        estimate = 0.0
        errorCovariance = 1.0
        isInitialized = false
    }

    val currentEstimate: Double
        get() = estimate
}
