package com.skytrack.domain.model

data class FlightStats(
    val totalFlights: Int = 0,
    val totalDistanceKm: Double = 0.0,
    val totalDurationHours: Double = 0.0,
    val topAirports: List<AirportVisitCount> = emptyList()
)

data class AirportVisitCount(
    val airport: Airport,
    val count: Int
)
