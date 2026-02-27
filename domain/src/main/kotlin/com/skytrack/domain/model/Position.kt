package com.skytrack.domain.model

import java.time.Instant

data class Position(
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double = 0.0,
    val speedKmh: Double = 0.0,
    val headingDegrees: Float = 0f,
    val accuracy: Float = 0f,
    val timestamp: Instant = Instant.now()
)
