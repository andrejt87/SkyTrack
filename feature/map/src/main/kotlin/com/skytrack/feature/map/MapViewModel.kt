package com.skytrack.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytrack.core.math.GeoMath
import com.skytrack.domain.model.LatLng
import com.skytrack.domain.usecase.GetCurrentPositionUseCase
import com.skytrack.domain.usecase.ObserveActiveFlightUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val observeActiveFlightUseCase: ObserveActiveFlightUseCase,
    private val getCurrentPositionUseCase: GetCurrentPositionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        observeFlight()
        observePosition()
    }

    private fun observeFlight() {
        viewModelScope.launch {
            observeActiveFlightUseCase.execute().collect { flight ->
                flight?.let {
                    val depLatLng = LatLng(it.departure.latitude, it.departure.longitude)
                    val arrLatLng = LatLng(it.arrival.latitude, it.arrival.longitude)

                    val routePoints = GeoMath.greatCirclePoints(
                        it.departure.latitude, it.departure.longitude,
                        it.arrival.latitude, it.arrival.longitude,
                        numPoints = 100
                    ).map { (lat, lon) -> LatLng(lat, lon) }

                    _uiState.update { state ->
                        state.copy(
                            departurePosition = depLatLng,
                            arrivalPosition = arrLatLng,
                            routePoints = routePoints,
                            departureIata = flight.departure.iata,
                            arrivalIata = flight.arrival.iata
                        )
                    }
                }
            }
        }
    }

    private fun observePosition() {
        viewModelScope.launch {
            getCurrentPositionUseCase.execute().collect { position ->
                _uiState.update { state ->
                    state.copy(
                        currentPosition = LatLng(position.latitude, position.longitude),
                        heading = position.headingDegrees
                    )
                }
            }
        }
    }

    fun onMapLoaded() {
        _uiState.update { it.copy(isMapLoaded = true) }
    }
}
