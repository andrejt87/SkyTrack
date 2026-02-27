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
    val showBarometer: Boolean   = true,
    val trackingIntervalSec: Int = 5,
    val offlineMapsEnabled: Boolean = true,
    val qnhHpa: Float = 1013.25f,
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
        showBarometer       = preferences.getBoolean(KEY_BAROMETER, true),
        trackingIntervalSec = preferences.getInt(KEY_INTERVAL, 5),
        offlineMapsEnabled  = preferences.getBoolean(KEY_OFFLINE_MAPS, true),
        qnhHpa              = preferences.getFloat(KEY_QNH, 1013.25f)
    )

    fun toggleMetricUnits() {
        val new = !_uiState.value.useMetricUnits
        _uiState.update { it.copy(useMetricUnits = new) }
        preferences.edit().putBoolean(KEY_METRIC, new).apply()
    }

    fun toggleBarometer() {
        val new = !_uiState.value.showBarometer
        _uiState.update { it.copy(showBarometer = new) }
        preferences.edit().putBoolean(KEY_BAROMETER, new).apply()
    }

    fun setTrackingInterval(sec: Int) {
        val clamped = sec.coerceIn(2, 30)
        _uiState.update { it.copy(trackingIntervalSec = clamped) }
        preferences.edit().putInt(KEY_INTERVAL, clamped).apply()
    }

    fun toggleOfflineMaps() {
        val new = !_uiState.value.offlineMapsEnabled
        _uiState.update { it.copy(offlineMapsEnabled = new) }
        preferences.edit().putBoolean(KEY_OFFLINE_MAPS, new).apply()
    }

    fun setQnh(hpa: Float) {
        val clamped = hpa.coerceIn(950f, 1050f)
        _uiState.update { it.copy(qnhHpa = clamped) }
        preferences.edit().putFloat(KEY_QNH, clamped).apply()
    }

    companion object {
        const val KEY_METRIC       = "use_metric"
        const val KEY_BAROMETER    = "show_barometer"
        const val KEY_INTERVAL     = "tracking_interval"
        const val KEY_OFFLINE_MAPS = "offline_maps"
        const val KEY_QNH          = "qnh_hpa"
    }
}
