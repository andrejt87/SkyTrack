package com.skytrack.app.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytrack.app.data.repository.FlightRepository
import com.skytrack.app.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    flightRepository: FlightRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    val activeFlightId = flightRepository.getActiveFlight()
        .map { flight -> flight?.id }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    /** True once the first GPS fix has been received. */
    private val _gpsReady = MutableStateFlow(false)
    val gpsReady: StateFlow<Boolean> = _gpsReady.asStateFlow()

    init {
        viewModelScope.launch {
            // Take just the first location update to signal readiness
            locationRepository.locationUpdates.first()
            _gpsReady.value = true
        }
    }
}
