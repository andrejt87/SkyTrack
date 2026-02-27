package com.skytrack.domain.usecase

import com.skytrack.domain.model.Position
import com.skytrack.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentPositionUseCase(
    private val locationRepository: LocationRepository
) {
    fun execute(): Flow<Position> = locationRepository.locationFlow()

    suspend fun lastKnown(): Position? = locationRepository.lastKnownPosition()
}
