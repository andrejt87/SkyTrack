package com.skytrack.domain.usecase

import com.skytrack.domain.model.PlaceInfo
import com.skytrack.domain.repository.GazetteerRepository

class GetOverflyInfoUseCase(
    private val gazetteerRepository: GazetteerRepository
) {
    suspend fun execute(latitude: Double, longitude: Double): PlaceInfo {
        return gazetteerRepository.reverseGeocode(latitude, longitude)
    }
}
