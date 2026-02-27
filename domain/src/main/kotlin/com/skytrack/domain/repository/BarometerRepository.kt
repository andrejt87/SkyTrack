package com.skytrack.domain.repository

import com.skytrack.domain.model.Pressure
import kotlinx.coroutines.flow.Flow

interface BarometerRepository {
    fun pressureFlow(): Flow<Pressure>
}
