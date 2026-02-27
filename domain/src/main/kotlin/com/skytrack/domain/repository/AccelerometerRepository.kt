package com.skytrack.domain.repository

import com.skytrack.domain.model.Acceleration
import kotlinx.coroutines.flow.Flow

interface AccelerometerRepository {
    fun accelerationFlow(): Flow<Acceleration>
}
