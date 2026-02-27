package com.skytrack.domain.usecase

import com.skytrack.domain.model.Airport
import com.skytrack.domain.repository.AirportRepository
import com.skytrack.domain.repository.LocationRepository

class GetNearestAirportUseCase(
    private val airportRepository: AirportRepository,
    private val locationRepository: LocationRepository
) {
    suspend fun execute(): Airport? {
        val position = locationRepository.lastKnownPosition() ?: return null
        val nearby = airportRepository.findNearest(position.latitude, position.longitude)
        return nearby.firstOrNull()
    }
}
