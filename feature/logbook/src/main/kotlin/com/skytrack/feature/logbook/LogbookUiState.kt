package com.skytrack.feature.logbook

import com.skytrack.domain.model.Flight
import com.skytrack.domain.model.FlightStats

data class LogbookUiState(
    val flights: List<Flight> = emptyList(),
    val stats: FlightStats = FlightStats(),
    val isLoading: Boolean = true
)
