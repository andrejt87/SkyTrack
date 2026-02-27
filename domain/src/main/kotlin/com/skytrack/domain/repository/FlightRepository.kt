package com.skytrack.domain.repository

import com.skytrack.domain.model.Flight
import com.skytrack.domain.model.TelemetryPoint
import com.skytrack.domain.model.TrackingState
import kotlinx.coroutines.flow.Flow

interface FlightRepository {
    fun observeActiveFlight(): Flow<Flight?>
    suspend fun getActiveFlight(): Flight?
    suspend fun startFlight(flight: Flight): String
    suspend fun updateTrackingState(flightId: String, state: TrackingState)
    suspend fun addTelemetryPoint(flightId: String, point: TelemetryPoint)
    suspend fun completeFlight(flightId: String)
    suspend fun getAllFlights(): List<Flight>
    suspend fun getFlightById(id: String): Flight?
}
