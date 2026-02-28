package com.skytrack.app.ui.screens.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytrack.app.data.model.Flight
import com.skytrack.app.data.model.FlightProgress
import com.skytrack.app.data.model.FlightStatus
import com.skytrack.app.data.model.TrackPoint
import com.skytrack.app.data.repository.FlightRepository
import com.skytrack.app.data.repository.LocationRepository
import com.skytrack.app.data.sensor.BarometerProvider
import com.skytrack.app.domain.FlightCalculator
import com.skytrack.app.data.sensor.LocationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val flight: Flight? = null,
    val progress: FlightProgress = FlightProgress(),
    val isTracking: Boolean = false,
    val barometerAvailable: Boolean = false,
    val elapsedMs: Long = 0L,
    val errorMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val flightRepository: FlightRepository,
    private val locationProvider: LocationProvider,
    private val locationRepository: LocationRepository,
    private val barometerProvider: BarometerProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val flightId: Long = checkNotNull(savedStateHandle["flightId"])

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // Track metrics for update-on-completion
    private var maxAltitude = 0.0
    private var maxSpeed    = 0.0
    private var speedSamples = mutableListOf<Double>()

    init {
        loadFlight()
        startTracking()
    }

    private fun loadFlight() {
        viewModelScope.launch {
            flightRepository.getFlightById(flightId).collect { flight ->
                _uiState.update { it.copy(flight = flight) }
            }
        }
    }

    private fun startTracking() {
        val flight = _uiState.value.flight
        _uiState.update { it.copy(isTracking = true, barometerAvailable = barometerProvider.isAvailable) }

        viewModelScope.launch {
            // Wait until we have flight data
            val flightData = flightRepository.getFlightById(flightId).first { it != null } ?: return@launch

            locationRepository.getFlightProgressFlow(
                departureLat = flightData.departureLat,
                departureLon = flightData.departureLon,
                arrivalLat   = flightData.arrivalLat,
                arrivalLon   = flightData.arrivalLon
            ).combine(
                if (barometerProvider.isAvailable)
                    barometerProvider.altitudeFlow()
                else
                    flowOf(0.0)
            ) { progress, baroAlt ->
                val altitude = if (barometerProvider.isAvailable) baroAlt else progress.altitudeM
                progress.copy(
                    altitudeM = altitude,
                    barometerAvailable = barometerProvider.isAvailable,
                    pressureHpa = if (barometerProvider.isAvailable) progress.pressureHpa else 1013.25f
                )
            }.collect { progress ->
                // Update records
                if (progress.altitudeM > maxAltitude) maxAltitude = progress.altitudeM
                if (progress.groundSpeedKmh > maxSpeed) maxSpeed = progress.groundSpeedKmh
                if (progress.groundSpeedKmh > 0) speedSamples.add(progress.groundSpeedKmh)

                // Persist track point immediately to DB
                flightRepository.insertTrackPoint(
                    TrackPoint(
                        flightId = flightId,
                        lat = progress.currentLat,
                        lon = progress.currentLon,
                        altitudeM = progress.altitudeM,
                        speedKmh = progress.groundSpeedKmh,
                        heading = progress.bearingDeg.toFloat(),
                        accuracy = progress.gpsAccuracyM,
                        timestamp = System.currentTimeMillis()
                    )
                )

                val elapsed = flightRepository.getFlightById(flightId).first()
                    ?.actualDepartureMs?.let { System.currentTimeMillis() - it } ?: 0L

                _uiState.update { it.copy(progress = progress, elapsedMs = elapsed) }
            }
        }
    }

    fun completeFlight(onComplete: () -> Unit) {
        viewModelScope.launch {
            val avgSpeed = if (speedSamples.isNotEmpty()) speedSamples.average() else 0.0
            flightRepository.completeFlight(
                flightId      = flightId,
                actualArrivalMs = System.currentTimeMillis(),
                maxAltitudeM  = maxAltitude,
                maxSpeedKmh   = maxSpeed,
                avgSpeedKmh   = avgSpeed
            )
            locationRepository.stopTracking()
            _uiState.update { it.copy(isTracking = false) }
            onComplete()
        }
    }

    fun cancelFlight() {
        viewModelScope.launch {
            flightRepository.updateFlightStatus(flightId, FlightStatus.SETUP)
            locationRepository.stopTracking()
            _uiState.update { it.copy(isTracking = false) }
        }
    }

    fun setDeparture(iata: String, name: String, lat: Double, lon: Double, tz: String) {
        viewModelScope.launch {
            val flight = flightRepository.getFlightById(flightId).first() ?: return@launch
            flightRepository.updateFlight(
                flight.copy(
                    departureIata = iata,
                    departureName = name,
                    departureLat = lat,
                    departureLon = lon,
                    departureTz = tz
                )
            )
        }
    }

    fun deleteFlight() {
        viewModelScope.launch {
            locationRepository.stopTracking()
            flightRepository.deleteFlightById(flightId)
            _uiState.update { it.copy(isTracking = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationRepository.stopTracking()
    }
}
