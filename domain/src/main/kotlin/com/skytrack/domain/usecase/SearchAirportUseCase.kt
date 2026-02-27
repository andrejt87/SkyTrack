package com.skytrack.domain.usecase

import com.skytrack.domain.model.Airport
import com.skytrack.domain.repository.AirportRepository

class SearchAirportUseCase(
    private val airportRepository: AirportRepository
) {
    suspend fun execute(query: String): List<Airport> {
        if (query.length < 2) return emptyList()
        return airportRepository.search(query)
    }

    suspend fun findByIata(iata: String): Airport? {
        return airportRepository.findByIata(iata.uppercase())
    }
}
