package com.skytrack.domain.usecase

import com.skytrack.domain.repository.GazetteerRepository
import java.time.ZoneId

class GetTimezoneUseCase(
    private val gazetteerRepository: GazetteerRepository
) {
    suspend fun execute(latitude: Double, longitude: Double): ZoneId {
        return gazetteerRepository.getTimezone(latitude, longitude)
    }
}
