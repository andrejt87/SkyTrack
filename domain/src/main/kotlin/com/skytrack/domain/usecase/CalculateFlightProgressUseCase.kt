package com.skytrack.domain.usecase

import com.skytrack.domain.model.FlightProgress
import com.skytrack.domain.model.LatLng
import kotlin.math.*

class CalculateFlightProgressUseCase {

    private var maxProgress: Float = 0f

    fun execute(
        currentPosition: LatLng,
        departure: LatLng,
        arrival: LatLng,
        emaAlpha: Float = 0.15f,
        previousEma: Float = 0f
    ): FlightProgress {
        val totalDistanceKm = haversineKm(departure, arrival)
        val coveredDistanceKm = haversineKm(departure, currentPosition)

        val rawProgress = if (totalDistanceKm > 0) {
            (coveredDistanceKm / totalDistanceKm).coerceIn(0.0, 1.0).toFloat() * 100f
        } else 0f

        // EMA smoothing
        val smoothedProgress = emaAlpha * rawProgress + (1 - emaAlpha) * previousEma

        // Monotonic: never decrease
        maxProgress = maxOf(maxProgress, smoothedProgress)

        return FlightProgress(
            percentage = maxProgress,
            distanceCoveredKm = coveredDistanceKm,
            distanceRemainingKm = (totalDistanceKm - coveredDistanceKm).coerceAtLeast(0.0),
            totalDistanceKm = totalDistanceKm
        )
    }

    fun reset() {
        maxProgress = 0f
    }

    companion object {
        fun haversineKm(from: LatLng, to: LatLng): Double {
            val R = 6371.0 // Earth radius in km
            val dLat = Math.toRadians(to.latitude - from.latitude)
            val dLon = Math.toRadians(to.longitude - from.longitude)
            val a = sin(dLat / 2).pow(2) +
                    cos(Math.toRadians(from.latitude)) * cos(Math.toRadians(to.latitude)) *
                    sin(dLon / 2).pow(2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return R * c
        }
    }
}
