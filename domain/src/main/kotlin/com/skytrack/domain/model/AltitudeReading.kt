package com.skytrack.domain.model

import java.time.Instant

data class AltitudeReading(
    val gpsAltitudeMeters: Double?,
    val barometerPressureHPa: Double?,
    val fusedAltitudeMeters: Double,
    val timestamp: Instant = Instant.now()
)
