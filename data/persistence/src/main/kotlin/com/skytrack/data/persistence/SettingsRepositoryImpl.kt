package com.skytrack.data.persistence

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.skytrack.domain.model.UnitSystem
import com.skytrack.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    companion object {
        val UNIT_SYSTEM_KEY = stringPreferencesKey("unit_system")
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    override fun observeUnitSystem(): Flow<UnitSystem> {
        return dataStore.data.map { prefs ->
            val value = prefs[UNIT_SYSTEM_KEY] ?: UnitSystem.METRIC.name
            UnitSystem.valueOf(value)
        }
    }

    override suspend fun setUnitSystem(unitSystem: UnitSystem) {
        dataStore.edit { prefs ->
            prefs[UNIT_SYSTEM_KEY] = unitSystem.name
        }
    }

    override fun observeDarkMode(): Flow<Boolean> {
        return dataStore.data.map { prefs ->
            prefs[DARK_MODE_KEY] ?: true // ADR-006: Dark Mode as default
        }
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }
}
