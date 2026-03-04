package com.skytrack.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytrack.app.data.model.Airport
import com.skytrack.app.data.model.Flight
import com.skytrack.app.data.model.FlightProgress
import com.skytrack.app.data.model.FlightStatus
import com.skytrack.app.data.model.TrackPoint
import com.skytrack.app.data.repository.FlightRepository
import com.skytrack.app.data.repository.LocationRepository
import com.skytrack.app.data.sensor.BarometerProvider
import com.skytrack.app.data.sensor.LocationProvider
import com.skytrack.app.domain.FlightCalculator
import android.content.Context
import android.content.Intent
import com.skytrack.app.service.FlightTrackingService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import android.util.Log
import com.skytrack.app.ui.screens.debug.DebugLog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

data class HomeUiState(
    // Setup state
    val departure: Airport? = null,
    val arrival: Airport? = null,
    val routeDistanceKm: Double = 0.0,
    val canStart: Boolean = false,
    val isLoading: Boolean = false,

    // Live tracking state
    val activeFlight: Flight? = null,
    val progress: FlightProgress = FlightProgress(),
    val isTracking: Boolean = false,
    val barometerAvailable: Boolean = false,
    val elapsedMs: Long = 0L,
    val errorMessage: String? = null
) {
    val hasActiveFlight: Boolean
        get() = activeFlight != null && (activeFlight.status == FlightStatus.AIRBORNE || activeFlight.status == FlightStatus.DESCENDING || activeFlight.status == FlightStatus.BOARDING)
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val flightRepository: FlightRepository,
    private val locationProvider: LocationProvider,
    private val locationRepository: LocationRepository,
    private val barometerProvider: BarometerProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var trackingJob: Job? = null
    private var watchdogJob: Job? = null
    private var lastGpsUpdateMs = 0L
    private var maxAltitude = 0.0
    private var maxSpeed = 0.0
    private var speedSamples = mutableListOf<Double>()

    companion object {
        private const val TAG = "HomeViewModel"
        private const val WATCHDOG_INTERVAL_MS = 15_000L
        private const val GPS_STALE_THRESHOLD_MS = 30_000L
    }

    init {
        observeActiveFlight()
        startPassiveLocation()
        startGpsWatchdog()
    }

    private fun startPassiveLocation() {
        viewModelScope.launch {
            DebugLog.info(TAG, "Passive location collector started")
            locationRepository.locationUpdates.collect { loc ->
                lastGpsUpdateMs = System.currentTimeMillis()
                DebugLog.gps(TAG, "lat=${String.format("%.5f", loc.lat)} lon=${String.format("%.5f", loc.lon)} spd=${String.format("%.1f", loc.speedKmh)}km/h alt=${String.format("%.0f", loc.altitudeM)}m acc=${String.format("%.1f", loc.accuracyM)}m")
                // Only update if no active flight (flight tracking handles its own updates)
                if (!_uiState.value.hasActiveFlight) {
                    _uiState.update { it.copy(
                        progress = it.progress.copy(
                            currentLat = loc.lat,
                            currentLon = loc.lon,
                            altitudeM = loc.altitudeM,
                            groundSpeedKmh = loc.speedKmh,
                            gpsAccuracyM = loc.accuracyM
                        )
                    )}
                }
            }
        }
    }

    /** Watchdog: restarts GPS if no update received for 30s */
    private fun startGpsWatchdog() {
        watchdogJob = viewModelScope.launch {
            while (true) {
                delay(WATCHDOG_INTERVAL_MS)
                if (lastGpsUpdateMs > 0) {
                    val staleMs = System.currentTimeMillis() - lastGpsUpdateMs
                    if (staleMs > GPS_STALE_THRESHOLD_MS) {
                        Log.w(TAG, "GPS stale for ${staleMs}ms, restarting location provider")
                        DebugLog.watchdog(TAG, "GPS stale ${staleMs}ms > ${GPS_STALE_THRESHOLD_MS}ms — force restarting provider")
                        locationProvider.forceRestart()
                    } else {
                        DebugLog.watchdog(TAG, "GPS OK, last update ${staleMs}ms ago")
                    }
                }
            }
        }
    }

    private fun observeActiveFlight() {
        viewModelScope.launch {
            flightRepository.getActiveFlight().collect { flight ->
                val wasTracking = _uiState.value.hasActiveFlight
                _uiState.update { it.copy(activeFlight = flight) }
                if (flight != null && !wasTracking) {
                    DebugLog.info(TAG, "Active flight detected: id=${flight.id} ${flight.departureIata}->${flight.arrivalIata}")
                    startTracking(flight)
                } else if (flight == null && wasTracking) {
                    DebugLog.info(TAG, "Active flight ended")
                    stopTrackingInternal()
                }
            }
        }
    }

    // ─── Setup methods ────────────────────────────────────────────────────

    fun setDeparture(airport: Airport) {
        _uiState.update {
            val dist = if (it.arrival != null) FlightCalculator.haversineDistance(airport.lat, airport.lon, it.arrival.lat, it.arrival.lon) else 0.0
            it.copy(departure = airport, routeDistanceKm = dist, canStart = it.arrival != null)
        }
    }

    fun setArrival(airport: Airport) {
        _uiState.update {
            val dist = if (it.departure != null) FlightCalculator.haversineDistance(it.departure.lat, it.departure.lon, airport.lat, airport.lon) else 0.0
            it.copy(arrival = airport, routeDistanceKm = dist, canStart = true)
        }
    }

    fun clearDeparture() {
        _uiState.update { it.copy(departure = null, routeDistanceKm = 0.0) }
    }

    fun clearArrival() {
        _uiState.update { it.copy(arrival = null, routeDistanceKm = 0.0, canStart = false) }
    }

    fun swapAirports() {
        _uiState.update {
            it.copy(departure = it.arrival, arrival = it.departure, canStart = it.departure != null)
        }
    }

    fun startFlight() {
        val state = _uiState.value
        val arr = state.arrival ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val dep = state.departure
            var depLat = dep?.lat ?: 0.0
            var depLon = dep?.lon ?: 0.0

            if (dep == null) {
                val loc = withTimeoutOrNull(10000L) {
                    locationProvider.getLastKnownLocation() ?: locationProvider.getCurrentLocation()
                }
                if (loc != null) {
                    depLat = loc.latitude
                    depLon = loc.longitude
                }
            }

            val dist = if (depLat != 0.0 || depLon != 0.0) {
                FlightCalculator.haversineDistance(depLat, depLon, arr.lat, arr.lon)
            } else state.routeDistanceKm

            val flight = Flight(
                departureIata = dep?.iata ?: "",
                departureName = dep?.name ?: "",
                departureLat = depLat,
                departureLon = depLon,
                departureTz = dep?.tz ?: "",
                arrivalIata = arr.iata,
                arrivalName = arr.name,
                arrivalLat = arr.lat,
                arrivalLon = arr.lon,
                arrivalTz = arr.tz,
                status = FlightStatus.AIRBORNE,
                actualDepartureMs = System.currentTimeMillis(),
                totalDistanceKm = dist
            )
            flightRepository.createFlight(flight)
            _uiState.update { it.copy(isLoading = false, departure = null, arrival = null, routeDistanceKm = 0.0, canStart = false) }
        }
    }

    // ─── Live tracking methods ────────────────────────────────────────────

    private fun startTracking(flight: Flight) {
        maxAltitude = 0.0
        maxSpeed = 0.0
        speedSamples.clear()
        _uiState.update { it.copy(isTracking = true, barometerAvailable = barometerProvider.isAvailable) }

        // Start foreground service for background GPS
        try {
            val serviceIntent = Intent(appContext, FlightTrackingService::class.java).apply {
                putExtra(FlightTrackingService.EXTRA_FLIGHT_ID, flight.id)
            }
            appContext.startForegroundService(serviceIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start tracking service", e)
        }

        trackingJob = viewModelScope.launch {
            locationRepository.getFlightProgressFlow(
                departureLat = flight.departureLat,
                departureLon = flight.departureLon,
                arrivalLat = flight.arrivalLat,
                arrivalLon = flight.arrivalLon
            ).combine(
                if (barometerProvider.isAvailable) barometerProvider.altitudeFlow() else flowOf(0.0)
            ) { progress, baroAlt ->
                val altitude = if (barometerProvider.isAvailable) baroAlt else progress.altitudeM
                progress.copy(
                    altitudeM = altitude,
                    barometerAvailable = barometerProvider.isAvailable,
                    pressureHpa = if (barometerProvider.isAvailable) progress.pressureHpa else 1013.25f
                )
            }.collect { progress ->
                lastGpsUpdateMs = System.currentTimeMillis()
                if (progress.altitudeM > maxAltitude) maxAltitude = progress.altitudeM
                if (progress.groundSpeedKmh > maxSpeed) maxSpeed = progress.groundSpeedKmh
                if (progress.groundSpeedKmh > 0) speedSamples.add(progress.groundSpeedKmh)

                DebugLog.track(TAG, "prog=${String.format("%.1f", progress.rawProgressPercent)}% dist=${String.format("%.1f", progress.remainingDistanceKm)}km spd=${String.format("%.1f", progress.groundSpeedKmh)}km/h alt=${String.format("%.0f", progress.altitudeM)}m")

                val currentFlight = _uiState.value.activeFlight ?: return@collect
                flightRepository.insertTrackPoint(
                    TrackPoint(
                        flightId = currentFlight.id,
                        lat = progress.currentLat,
                        lon = progress.currentLon,
                        altitudeM = progress.altitudeM,
                        speedKmh = progress.groundSpeedKmh,
                        heading = progress.bearingDeg.toFloat(),
                        accuracy = progress.gpsAccuracyM,
                        timestamp = System.currentTimeMillis()
                    )
                )

                val elapsed = currentFlight.actualDepartureMs.let {
                    if (it > 0) System.currentTimeMillis() - it else 0L
                }

                _uiState.update { it.copy(progress = progress, elapsedMs = elapsed) }
            }
        }
    }

    private fun stopTrackingInternal() {
        trackingJob?.cancel()
        trackingJob = null
        // Stop foreground service
        try {
            appContext.stopService(Intent(appContext, FlightTrackingService::class.java))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop tracking service", e)
        }
        // Don't reset progress — keep last GPS values so idle UI still shows position/speed/alt
        // Don't call stopTracking — passive location collector continues
        _uiState.update { it.copy(isTracking = false, elapsedMs = 0L) }
    }

    fun completeFlight(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val flight = _uiState.value.activeFlight ?: return@launch
            val avgSpeed = if (speedSamples.isNotEmpty()) speedSamples.average() else 0.0
            flightRepository.completeFlight(
                flightId = flight.id,
                actualArrivalMs = System.currentTimeMillis(),
                maxAltitudeM = maxAltitude,
                maxSpeedKmh = maxSpeed,
                avgSpeedKmh = avgSpeed
            )
            stopTrackingInternal()
            onComplete()
        }
    }

    fun cancelFlight() {
        viewModelScope.launch {
            val flight = _uiState.value.activeFlight ?: return@launch
            flightRepository.updateFlightStatus(flight.id, FlightStatus.SETUP)
            stopTrackingInternal()
        }
    }

    fun deleteFlight() {
        viewModelScope.launch {
            val flight = _uiState.value.activeFlight ?: return@launch
            stopTrackingInternal()
            flightRepository.deleteFlightById(flight.id)
        }
    }

    fun setDepartureOnActiveFlight(airport: Airport) {
        viewModelScope.launch {
            val flight = _uiState.value.activeFlight ?: return@launch
            val updatedFlight = flight.copy(
                departureIata = airport.iata,
                departureName = airport.name,
                departureLat = airport.lat,
                departureLon = airport.lon,
                departureTz = airport.tz,
                totalDistanceKm = FlightCalculator.haversineDistance(
                    airport.lat, airport.lon, flight.arrivalLat, flight.arrivalLon
                )
            )
            flightRepository.updateFlight(updatedFlight)
            // Restart tracking with new departure coordinates
            trackingJob?.cancel()
            trackingJob = null
            startTracking(updatedFlight)
        }
    }

    override fun onCleared() {
        super.onCleared()
        trackingJob?.cancel()
        watchdogJob?.cancel()
    }
}
