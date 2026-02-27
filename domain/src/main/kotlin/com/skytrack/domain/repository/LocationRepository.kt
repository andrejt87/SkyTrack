package com.skytrack.domain.repository

import com.skytrack.domain.model.Position
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun locationFlow(): Flow<Position>
    suspend fun lastKnownPosition(): Position?
}
