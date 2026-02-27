package com.skytrack.domain.usecase

import com.skytrack.domain.model.Flight
import com.skytrack.domain.repository.FlightRepository
import kotlinx.coroutines.flow.Flow

class ObserveActiveFlightUseCase(
    private val flightRepository: FlightRepository
) {
    fun execute(): Flow<Flight?> = flightRepository.observeActiveFlight()
}
