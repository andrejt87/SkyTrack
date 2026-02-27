package com.skytrack.feature.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytrack.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val searchAirportUseCase: SearchAirportUseCase,
    private val getNearestAirportUseCase: GetNearestAirportUseCase,
    private val startFlightUseCase: StartFlightUseCase,
    private val getRecentFlightsUseCase: GetRecentFlightsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    private var departureSearchJob: Job? = null
    private var arrivalSearchJob: Job? = null

    init {
        loadRecentFlights()
        detectNearestAirport()
    }

    fun onDepartureQueryChanged(query: String) {
        _uiState.update { it.copy(departureQuery = query, selectedDeparture = null) }
        departureSearchJob?.cancel()
        departureSearchJob = viewModelScope.launch {
            delay(300) // debounce
            val results = searchAirportUseCase.execute(query)
            _uiState.update { it.copy(departureSuggestions = results) }
            updateStartEnabled()
        }
    }

    fun onArrivalQueryChanged(query: String) {
        _uiState.update { it.copy(arrivalQuery = query, selectedArrival = null) }
        arrivalSearchJob?.cancel()
        arrivalSearchJob = viewModelScope.launch {
            delay(300)
            val results = searchAirportUseCase.execute(query)
            _uiState.update { it.copy(arrivalSuggestions = results) }
            updateStartEnabled()
        }
    }

    fun onDepartureSelected(index: Int) {
        val airport = _uiState.value.departureSuggestions.getOrNull(index) ?: return
        _uiState.update {
            it.copy(
                selectedDeparture = airport,
                departureQuery = "${airport.iata} - ${airport.name}",
                departureSuggestions = emptyList()
            )
        }
        updateStartEnabled()
    }

    fun onArrivalSelected(index: Int) {
        val airport = _uiState.value.arrivalSuggestions.getOrNull(index) ?: return
        _uiState.update {
            it.copy(
                selectedArrival = airport,
                arrivalQuery = "${airport.iata} - ${airport.name}",
                arrivalSuggestions = emptyList()
            )
        }
        updateStartEnabled()
    }

    fun useNearestAsDeparture() {
        val nearest = _uiState.value.nearestAirport ?: return
        _uiState.update {
            it.copy(
                selectedDeparture = nearest,
                departureQuery = "${nearest.iata} - ${nearest.name}",
                departureSuggestions = emptyList()
            )
        }
        updateStartEnabled()
    }

    fun startFlight(onSuccess: () -> Unit) {
        val dep = _uiState.value.selectedDeparture ?: return
        val arr = _uiState.value.selectedArrival ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = startFlightUseCase.execute(dep.iata, arr.iata)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    private fun loadRecentFlights() {
        viewModelScope.launch {
            val flights = getRecentFlightsUseCase.execute(5)
            _uiState.update { it.copy(recentFlights = flights) }
        }
    }

    private fun detectNearestAirport() {
        viewModelScope.launch {
            val nearest = getNearestAirportUseCase.execute()
            _uiState.update { it.copy(nearestAirport = nearest) }
        }
    }

    private fun updateStartEnabled() {
        _uiState.update {
            it.copy(isStartEnabled = it.selectedDeparture != null && it.selectedArrival != null)
        }
    }
}
