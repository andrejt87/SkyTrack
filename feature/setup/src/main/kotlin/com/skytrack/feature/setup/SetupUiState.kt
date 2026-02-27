package com.skytrack.feature.setup

import com.skytrack.domain.model.Airport
import com.skytrack.domain.model.Flight

data class SetupUiState(
    val departureQuery: String = "",
    val arrivalQuery: String = "",
    val departureSuggestions: List<Airport> = emptyList(),
    val arrivalSuggestions: List<Airport> = emptyList(),
    val selectedDeparture: Airport? = null,
    val selectedArrival: Airport? = null,
    val nearestAirport: Airport? = null,
    val recentFlights: List<Flight> = emptyList(),
    val isStartEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
