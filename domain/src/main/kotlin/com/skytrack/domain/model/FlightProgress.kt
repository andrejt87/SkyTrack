package com.skytrack.domain.model

data class FlightProgress(
    val percentage: Float,
    val distanceCoveredKm: Double,
    val distanceRemainingKm: Double,
    val totalDistanceKm: Double
)
