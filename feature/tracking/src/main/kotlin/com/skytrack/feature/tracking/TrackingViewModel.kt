package com.skytrack.feature.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytrack.domain.model.*
import com.skytrack.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val observeActiveFlightUseCase: ObserveActiveFlightUseCase,
    private val getCurrentPositionUseCase: GetCurrentPositionUseCase,
    private val calculateFlightProgressUseCase: CalculateFlightProgressUseCase,
    private val calculateEtaUseCase: CalculateEtaUseCase,
    private val detectTurbulenceUseCase: DetectTurbulenceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState.asStateFlow()

    private var currentFlight: Flight? = null

    init {
        observeFlight()
        observePosition()
        observeTurbulence()
    }

    private fun observeFlight() {
        viewModelScope.launch {
            observeActiveFlightUseCase.execute().collect { flight ->
                currentFlight = flight
                flight?.let {
                    _uiState.update { state ->
                        state.copy(
                            departure = flight.departure,
                            arrival = flight.arrival,
                            trackingState = flight.state
                        )
                    }
                }
            }
        }
    }

    private fun observePosition() {
        viewModelScope.launch {
            getCurrentPositionUseCase.execute().collect { position ->
                val flight = currentFlight ?: return@collect

                val currentLatLng = LatLng(position.latitude, position.longitude)
                val depLatLng = LatLng(flight.departure.latitude, flight.departure.longitude)
                val arrLatLng = LatLng(flight.arrival.latitude, flight.arrival.longitude)

                val progress = calculateFlightProgressUseCase.execute(
                    currentPosition = currentLatLng,
                    departure = depLatLng,
                    arrival = arrLatLng,
                    previousEma = _uiState.value.progress
                )

                val eta = calculateEtaUseCase.execute(
                    currentPosition = currentLatLng,
                    arrival = arrLatLng,
                    currentSpeedKmh = position.speedKmh
                )

                val etaString = eta?.let {
                    val hours = it.toHours()
                    val minutes = it.toMinutes() % 60
                    "%02d:%02d".format(hours, minutes)
                } ?: "--:--"

                val newTrackingState = when {
                    progress.percentage >= 99.5f -> TrackingState.ARRIVED
                    position.accuracy <= 0f -> TrackingState.GPS_LOST
                    position.speedKmh < 50 && position.altitudeMeters > 8000 -> TrackingState.PARKED
                    else -> TrackingState.TRACKING
                }

                _uiState.update { state ->
                    state.copy(
                        progress = progress.percentage,
                        speed = Speed(position.speedKmh),
                        altitude = Altitude(position.altitudeMeters),
                        heading = position.headingDegrees,
                        eta = etaString,
                        distanceCovered = Distance(progress.distanceCoveredKm),
                        distanceRemaining = Distance(progress.distanceRemainingKm),
                        currentPosition = currentLatLng,
                        isGpsActive = position.accuracy > 0f,
                        trackingState = newTrackingState
                    )
                }
            }
        }
    }

    private fun observeTurbulence() {
        viewModelScope.launch {
            detectTurbulenceUseCase.execute().collect { level ->
                _uiState.update { it.copy(turbulenceLevel = level.name) }
            }
        }
    }
}
