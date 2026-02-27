package com.skytrack.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytrack.domain.model.*
import com.skytrack.domain.usecase.*
import com.skytrack.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getCurrentPositionUseCase: GetCurrentPositionUseCase,
    private val getFlightStatsUseCase: GetFlightStatsUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val altitudeBuffer = mutableListOf<AltitudePoint>()
    private val speedBuffer = mutableListOf<SpeedPoint>()

    init {
        observePosition()
        loadStats()
        observeSettings()
    }

    private fun observePosition() {
        viewModelScope.launch {
            getCurrentPositionUseCase.execute().collect { position ->
                val now = System.currentTimeMillis()

                altitudeBuffer.add(AltitudePoint(now, position.altitudeMeters))
                speedBuffer.add(SpeedPoint(now, position.speedKmh))

                // Keep last 30 minutes of data
                val cutoff = now - 30 * 60 * 1000
                altitudeBuffer.removeAll { it.timestampMs < cutoff }
                speedBuffer.removeAll { it.timestampMs < cutoff }

                _uiState.update { state ->
                    state.copy(
                        altitudeHistory = altitudeBuffer.toList(),
                        speedHistory = speedBuffer.toList(),
                        currentAltitude = Altitude(position.altitudeMeters),
                        currentSpeed = Speed(position.speedKmh),
                        maxAltitude = Altitude(maxOf(state.maxAltitude.meters, position.altitudeMeters)),
                        maxSpeed = Speed(maxOf(state.maxSpeed.kmh, position.speedKmh))
                    )
                }
            }
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            val stats = getFlightStatsUseCase.execute()
            _uiState.update { it.copy(stats = stats) }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.observeUnitSystem().collect { unit ->
                _uiState.update { it.copy(unitSystem = unit) }
            }
        }
    }
}
