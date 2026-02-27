package com.skytrack.domain.usecase

import com.skytrack.domain.model.Flight
import com.skytrack.domain.repository.FlightRepository

class GetRecentFlightsUseCase(
    private val flightRepository: FlightRepository
) {
    suspend fun execute(limit: Int = 10): List<Flight> {
        return flightRepository.getAllFlights()
            .sortedByDescending { it.createdAt }
            .take(limit)
    }
}
