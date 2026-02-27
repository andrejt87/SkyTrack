package com.skytrack.core.navigation

sealed class NavigationEvent {
    data class Navigate(val destination: SkyTrackDestination) : NavigationEvent()
    data object NavigateBack : NavigationEvent()
    data class NavigateToFlightDetail(val flightId: String) : NavigationEvent()
}
