package com.skytrack.feature.tracking

import com.skytrack.domain.model.*

data class TrackingUiState(
    val trackingState: TrackingState = TrackingState.INITIALIZING,
    val progress: Float = 0f,
    val speed: Speed = Speed(0.0),
    val altitude: Altitude = Altitude(0.0),
    val heading: Float = 0f,
    val eta: String = "--:--",
    val distanceCovered: Distance = Distance(0.0),
    val distanceRemaining: Distance = Distance(0.0),
    val currentPosition: LatLng? = null,
    val departure: Airport? = null,
    val arrival: Airport? = null,
    val isGpsActive: Boolean = false,
    val turbulenceLevel: String = "NONE"
)
