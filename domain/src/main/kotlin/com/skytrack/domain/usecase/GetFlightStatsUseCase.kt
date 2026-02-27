package com.skytrack.domain.usecase

import com.skytrack.domain.model.AirportVisitCount
import com.skytrack.domain.model.FlightStats
import com.skytrack.domain.repository.FlightRepository

class GetFlightStatsUseCase(
    private val flightRepository: FlightRepository
) {
    suspend fun execute(): FlightStats {
        val flights = flightRepository.getAllFlights()
        if (flights.isEmpty()) return FlightStats()

        val totalDistance = flights.sumOf { it.config.totalDistanceKm }
        val totalDuration = flights.sumOf { it.log.totalDurationSeconds }

        val airportCounts = mutableMapOf<String, Int>()
        flights.forEach { flight ->
            airportCounts[flight.departure.iata] = (airportCounts[flight.departure.iata] ?: 0) + 1
            airportCounts[flight.arrival.iata] = (airportCounts[flight.arrival.iata] ?: 0) + 1
        }

        val topAirports = airportCounts.entries
            .sortedByDescending { it.value }
            .take(5)
            .mapNotNull { entry ->
                val airport = flights.flatMap { listOf(it.departure, it.arrival) }
                    .firstOrNull { it.iata == entry.key }
                airport?.let { AirportVisitCount(it, entry.value) }
            }

        return FlightStats(
            totalFlights = flights.size,
            totalDistanceKm = totalDistance,
            totalDurationHours = totalDuration / 3600.0,
            topAirports = topAirports
        )
    }
}
