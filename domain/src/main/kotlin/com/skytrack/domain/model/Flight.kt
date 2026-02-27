package com.skytrack.domain.model

import java.time.Instant

data class Flight(
    val id: String,
    val departure: Airport,
    val arrival: Airport,
    val config: FlightConfig,
    val log: FlightLog = FlightLog(),
    val state: TrackingState = TrackingState.INITIALIZING,
    val createdAt: Instant = Instant.now()
)

data class FlightConfig(
    val departureIata: String,
    val arrivalIata: String,
    val totalDistanceKm: Double,
    val greatCirclePoints: List<LatLng> = emptyList()
)

data class FlightLog(
    val telemetry: List<TelemetryPoint> = emptyList(),
    val maxAltitudeM: Double = 0.0,
    val maxSpeedKmh: Double = 0.0,
    val totalDurationSeconds: Long = 0
)
