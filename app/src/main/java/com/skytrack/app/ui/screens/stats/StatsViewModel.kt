package com.skytrack.app.ui.screens.stats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytrack.app.data.model.Flight
import com.skytrack.app.data.repository.FlightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class StatsUiState(
    val flight: Flight? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    flightRepository: FlightRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val flightId: Long = checkNotNull(savedStateHandle["flightId"])

    val uiState: StateFlow<StatsUiState> = flightRepository
        .getFlightById(flightId)
        .map { StatsUiState(flight = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatsUiState()
        )
}
