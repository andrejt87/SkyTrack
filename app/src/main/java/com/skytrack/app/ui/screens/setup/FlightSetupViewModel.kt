package com.skytrack.app.ui.screens.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytrack.app.data.model.Airport
import com.skytrack.app.data.model.Flight
import com.skytrack.app.data.model.FlightStatus
import com.skytrack.app.data.repository.AirportRepository
import com.skytrack.app.data.repository.FlightRepository
import com.skytrack.app.domain.FlightCalculator
import com.skytrack.app.data.sensor.LocationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

data class SetupUiState(
    val departure: Airport? = null,
    val arrival: Airport? = null,
    val isLoading: Boolean = false,
    val canStart: Boolean = false,
    val routeDistanceKm: Double = 0.0,
    val errorMessage: String? = null,
    val hasLocationPermission: Boolean = false
)

@HiltViewModel
class FlightSetupViewModel @Inject constructor(
    private val flightRepository: FlightRepository,
    private val airportRepository: AirportRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    // Shared state for airport picker result – other ViewModels can read this via SavedStateHandle
    private val _selectedDeparture = MutableStateFlow<Airport?>(null)
    private val _selectedArrival   = MutableStateFlow<Airport?>(null)

    init {
        viewModelScope.launch {
            combine(_selectedDeparture, _selectedArrival) { dep, arr ->
                val distance = if (dep != null && arr != null) {
                    FlightCalculator.haversineDistance(dep.lat, dep.lon, arr.lat, arr.lon)
                } else 0.0
                SetupUiState(
                    departure = dep,
                    arrival = arr,
                    canStart = arr != null && (dep == null || dep.iata != arr.iata),
                    routeDistanceKm = distance
                )
            }.collect { _uiState.value = it }
        }
    }

    fun setDeparture(airport: Airport) {
        _selectedDeparture.value = airport
    }

    fun setArrival(airport: Airport) {
        _selectedArrival.value = airport
    }

    fun clearDeparture() {
        _selectedDeparture.value = null
    }

    fun clearArrival() {
        _selectedArrival.value = null
    }

    fun swapAirports() {
        val dep = _selectedDeparture.value
        val arr = _selectedArrival.value
        _selectedDeparture.value = arr
        _selectedArrival.value = dep
    }

    fun onLocationPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(hasLocationPermission = granted) }
    }

    fun startFlight(onSuccess: (Long) -> Unit) {
        val state = _uiState.value
        val arr = state.arrival ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // If no departure selected, use current GPS position
            val dep = state.departure
            val depIata = dep?.iata ?: "GPS"
            val depName = dep?.name ?: "Current Position"
            var depLat = dep?.lat ?: 0.0
            var depLon = dep?.lon ?: 0.0
            val depTz = dep?.tz ?: ""
            
            if (dep == null) {
                // Try cached location first, then request fresh GPS fix
                val loc = withTimeoutOrNull(10000L) {
                    locationProvider.getLastKnownLocation()
                        ?: locationProvider.getCurrentLocation()
                }
                if (loc != null) {
                    depLat = loc.latitude
                    depLon = loc.longitude
                }
            }
            
            val flight = Flight(
                departureIata = depIata,
                departureName = depName,
                departureLat  = depLat,
                departureLon  = depLon,
                departureTz   = depTz,
                arrivalIata   = arr.iata,
                arrivalName   = arr.name,
                arrivalLat    = arr.lat,
                arrivalLon    = arr.lon,
                arrivalTz     = arr.tz,
                status        = FlightStatus.AIRBORNE,
                actualDepartureMs = System.currentTimeMillis(),
                totalDistanceKm   = state.routeDistanceKm
            )
            val id = flightRepository.createFlight(flight)
            _uiState.update { it.copy(isLoading = false) }
            onSuccess(id)
        }
    }
}
