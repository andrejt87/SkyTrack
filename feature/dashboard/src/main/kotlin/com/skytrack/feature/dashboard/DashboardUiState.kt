package com.skytrack.feature.dashboard

import com.skytrack.domain.model.*

data class DashboardUiState(
    val altitudeHistory: List<AltitudePoint> = emptyList(),
    val speedHistory: List<SpeedPoint> = emptyList(),
    val timezones: List<TimezoneInfo> = emptyList(),
    val stats: FlightStats = FlightStats(),
    val currentAltitude: Altitude = Altitude(0.0),
    val currentSpeed: Speed = Speed(0.0),
    val maxAltitude: Altitude = Altitude(0.0),
    val maxSpeed: Speed = Speed(0.0),
    val unitSystem: UnitSystem = UnitSystem.METRIC
)

data class AltitudePoint(val timestampMs: Long, val meters: Double)
data class SpeedPoint(val timestampMs: Long, val kmh: Double)
