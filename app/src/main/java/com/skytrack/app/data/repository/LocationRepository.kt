package com.skytrack.app.data.repository

import com.skytrack.app.data.model.FlightProgress
import com.skytrack.app.data.sensor.LocationProvider
import com.skytrack.app.domain.FlightCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val locationProvider: LocationProvider
) {
    /**
     * Emits raw location updates as (lat, lon, speedKmh, accuracyM) tuples.
     */
    val locationUpdates: Flow<LocationUpdate>
        get() = locationProvider.locationFlow.map { loc ->
            LocationUpdate(
                lat = loc.latitude,
                lon = loc.longitude,
                speedKmh = (loc.speed * 3.6f).toDouble(), // m/s → km/h
                accuracyM = loc.accuracy,
                altitudeM = loc.altitude,
                bearingDeg = loc.bearing.toDouble(),
                timestampMs = loc.time
            )
        }

    fun startTracking() = locationProvider.startTracking()

    fun stopTracking() = locationProvider.stopTracking()

    /**
     * Calculates real-time flight progress by combining location updates with route data.
     */
    fun getFlightProgressFlow(
        departureLat: Double,
        departureLon: Double,
        arrivalLat: Double,
        arrivalLon: Double
    ): Flow<FlightProgress> {
        return locationProvider.locationFlow.map { loc ->
            val rawSpeedKmh = (loc.speed * 3.6f).toDouble()
            // Suppress GPS drift noise: speeds below 10 km/h are treated as stationary
            val speedKmh = if (rawSpeedKmh < 10.0) 0.0 else rawSpeedKmh

            val progress = FlightCalculator.calculateProgress(
                currentLat = loc.latitude,
                currentLon = loc.longitude,
                departureLat = departureLat,
                departureLon = departureLon,
                arrivalLat = arrivalLat,
                arrivalLon = arrivalLon
            )
            val eta = FlightCalculator.calculateETA(
                remainingDistanceKm = progress.remainingDistanceKm,
                speedKmh = speedKmh
            )
            progress.copy(
                groundSpeedKmh = speedKmh,
                altitudeM = loc.altitude,
                gpsAccuracyM = loc.accuracy,
                currentLat = loc.latitude,
                currentLon = loc.longitude,
                etaMs = eta
            )
        }
    }

    data class LocationUpdate(
        val lat: Double,
        val lon: Double,
        val speedKmh: Double,
        val accuracyM: Float,
        val altitudeM: Double,
        val bearingDeg: Double,
        val timestampMs: Long
    )
}
