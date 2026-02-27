package com.skytrack.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FlightStatus {
    SETUP,       // Not yet started
    BOARDING,    // At departure airport
    AIRBORNE,    // In flight
    DESCENDING,  // Below 10,000 ft and decreasing
    LANDED,      // At destination
    COMPLETED    // Archived
}

@Entity(tableName = "flights")
data class Flight(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Route
    val departureIata: String,
    val departureName: String,
    val departureLat: Double,
    val departureLon: Double,
    val departureTz: String,

    val arrivalIata: String,
    val arrivalName: String,
    val arrivalLat: Double,
    val arrivalLon: Double,
    val arrivalTz: String,

    // Flight details
    val flightNumber: String = "",
    val airline: String = "",

    // Timing
    val scheduledDepartureMs: Long = 0L,
    val scheduledArrivalMs: Long = 0L,
    val actualDepartureMs: Long = 0L,
    val actualArrivalMs: Long = 0L,

    // Status
    val status: FlightStatus = FlightStatus.SETUP,

    // Metrics (updated throughout flight)
    val totalDistanceKm: Double = 0.0,
    val maxAltitudeM: Double = 0.0,
    val maxSpeedKmh: Double = 0.0,
    val avgSpeedKmh: Double = 0.0,

    // Tracking path (JSON-encoded list of lat/lon pairs)
    val trackPointsJson: String = "[]",

    // Created
    val createdAtMs: Long = System.currentTimeMillis()
) {
    val isActive: Boolean
        get() = status == FlightStatus.AIRBORNE || status == FlightStatus.DESCENDING

    val routeLabel: String
        get() = "$departureIata → $arrivalIata"

    val durationMs: Long
        get() = if (actualArrivalMs > 0 && actualDepartureMs > 0)
            actualArrivalMs - actualDepartureMs else 0L
}
