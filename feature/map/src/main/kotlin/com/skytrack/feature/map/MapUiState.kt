package com.skytrack.feature.map

import com.skytrack.domain.model.LatLng

data class MapUiState(
    val currentPosition: LatLng? = null,
    val heading: Float = 0f,
    val departurePosition: LatLng? = null,
    val arrivalPosition: LatLng? = null,
    val routePoints: List<LatLng> = emptyList(),
    val zoomLevel: Double = 5.0,
    val isOfflineBadgeVisible: Boolean = true,
    val isMapLoaded: Boolean = false,
    val departureIata: String = "",
    val arrivalIata: String = ""
)
