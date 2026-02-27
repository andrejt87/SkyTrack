package com.skytrack.domain.usecase

import com.skytrack.domain.model.Acceleration
import com.skytrack.domain.repository.AccelerometerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DetectTurbulenceUseCase(
    private val accelerometerRepository: AccelerometerRepository
) {
    fun execute(): Flow<TurbulenceLevel> {
        return accelerometerRepository.accelerationFlow().map { accel ->
            val deviation = kotlin.math.abs(accel.magnitude - GRAVITY)
            when {
                deviation < 0.5f -> TurbulenceLevel.NONE
                deviation < 1.5f -> TurbulenceLevel.LIGHT
                deviation < 3.0f -> TurbulenceLevel.MODERATE
                else -> TurbulenceLevel.SEVERE
            }
        }
    }

    companion object {
        private const val GRAVITY = 9.81f
    }
}

enum class TurbulenceLevel {
    NONE, LIGHT, MODERATE, SEVERE
}
