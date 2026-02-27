package com.skytrack.core.navigation

sealed class SkyTrackDestination(val route: String) {
    data object Splash : SkyTrackDestination("splash")
    data object Setup : SkyTrackDestination("setup")
    data object Tracking : SkyTrackDestination("tracking")
    data object Map : SkyTrackDestination("map")
    data object Dashboard : SkyTrackDestination("dashboard")
    data object Logbook : SkyTrackDestination("logbook")
    data object FlightDetail : SkyTrackDestination("flight_detail/{flightId}") {
        fun createRoute(flightId: String) = "flight_detail/$flightId"
    }
    data object Settings : SkyTrackDestination("settings")
}
