package com.skytrack.app.ui.screens.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytrack.app.data.model.Flight
import com.skytrack.app.data.model.FlightProgress
import com.skytrack.app.data.repository.FlightRepository
import com.skytrack.app.data.repository.LocationRepository
import com.skytrack.app.data.sensor.BarometerProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val flight: Flight? = null,
    val progress: FlightProgress = FlightProgress(),
    val isTracking: Boolean = false
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val flightRepository: FlightRepository,
    private val locationRepository: LocationRepository,
    private val barometerProvider: BarometerProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val flightId: Long = savedStateHandle["flightId"] ?: -1L
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        if (flightId > 0) {
            viewModelScope.launch {
                flightRepository.getFlightById(flightId).collect { flight ->
                    _uiState.update { it.copy(flight = flight) }
                    if (flight != null && !_uiState.value.isTracking) {
                        startTracking(flight)
                    }
                }
            }
        } else {
            // No flight — just show current position
            startLocationOnly()
        }
    }

    private fun startTracking(flight: Flight) {
        _uiState.update { it.copy(isTracking = true) }
        viewModelScope.launch {
            locationRepository.getFlightProgressFlow(
                departureLat = flight.departureLat,
                departureLon = flight.departureLon,
                arrivalLat   = flight.arrivalLat,
                arrivalLon   = flight.arrivalLon
            ).collect { progress ->
                _uiState.update { it.copy(progress = progress) }
            }
        }
    }

    private fun startLocationOnly() {
        _uiState.update { it.copy(isTracking = true) }
        viewModelScope.launch {
            locationRepository.locationUpdates.collect { loc ->
                _uiState.update { it.copy(
                    progress = it.progress.copy(
                        currentLat = loc.lat,
                        currentLon = loc.lon,
                        altitudeM = loc.altitudeM,
                        groundSpeedKmh = loc.speedKmh,
                        gpsAccuracyM = loc.accuracyM
                    )
                )}
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationRepository.stopTracking()
    }
}
