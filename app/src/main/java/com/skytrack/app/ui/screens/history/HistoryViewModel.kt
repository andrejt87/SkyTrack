package com.skytrack.app.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytrack.app.data.model.Flight
import com.skytrack.app.data.repository.FlightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val flights: List<Flight> = emptyList(),
    val totalFlights: Int = 0,
    val totalDistanceKm: Double = 0.0,
    val totalFlightTimeMs: Long = 0L,
    val isLoading: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val flightRepository: FlightRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = flightRepository.getCompletedFlights()
        .map { flights ->
            HistoryUiState(
                flights = flights,
                totalFlights = flights.size,
                totalDistanceKm = flights.sumOf { it.totalDistanceKm },
                totalFlightTimeMs = flights.sumOf { it.durationMs },
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryUiState()
        )

    fun deleteFlight(flight: Flight) {
        viewModelScope.launch {
            flightRepository.deleteFlight(flight)
        }
    }
}
