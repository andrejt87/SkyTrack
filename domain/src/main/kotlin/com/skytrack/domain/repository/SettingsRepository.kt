package com.skytrack.domain.repository

import com.skytrack.domain.model.UnitSystem
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeUnitSystem(): Flow<UnitSystem>
    suspend fun setUnitSystem(unitSystem: UnitSystem)
    fun observeDarkMode(): Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)
}
