package com.skytrack.app.ui.screens.settings

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val useMetricUnits: Boolean  = true,
    val trackingIntervalSec: Int = 5,
    val appVersion: String = "1.0.0"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(loadFromPrefs())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private fun loadFromPrefs(): SettingsUiState = SettingsUiState(
        useMetricUnits      = preferences.getBoolean(KEY_METRIC, true),
        trackingIntervalSec = preferences.getInt(KEY_INTERVAL, 5)
    )

    fun toggleMetricUnits() {
        val new = !_uiState.value.useMetricUnits
        _uiState.update { it.copy(useMetricUnits = new) }
        preferences.edit().putBoolean(KEY_METRIC, new).apply()
        com.skytrack.app.data.model.FlightProgress.useMetric = new
    }

    fun setTrackingInterval(sec: Int) {
        val clamped = sec.coerceIn(2, 30)
        _uiState.update { it.copy(trackingIntervalSec = clamped) }
        preferences.edit().putInt(KEY_INTERVAL, clamped).apply()
    }

    companion object {
        const val KEY_METRIC   = "use_metric"
        const val KEY_INTERVAL = "tracking_interval"
    }
}
