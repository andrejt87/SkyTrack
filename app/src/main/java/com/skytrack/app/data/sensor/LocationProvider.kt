package com.skytrack.app.data.sensor

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import com.skytrack.app.ui.screens.debug.DebugLog
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val prefs = context.getSharedPreferences("skytrack_prefs", Context.MODE_PRIVATE)

    private fun getIntervalMs(): Long = (prefs.getInt("tracking_interval", 5) * 1000L)

    private fun buildLocationRequest(): LocationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        getIntervalMs()
    )
        .setMinUpdateIntervalMillis((getIntervalMs() / 2).coerceAtLeast(1000L))
        .setMinUpdateDistanceMeters(MIN_DISTANCE_METERS)
        .setWaitForAccurateLocation(false)
        .build()

    private var currentIntervalMs: Long = getIntervalMs()
    private var activeCallback: LocationCallback? = null

    /** Shared hot Flow of location updates. Restarts when interval setting changes. */
    val locationFlow: Flow<Location> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    DebugLog.gps("LocationProvider", "FusedClient update: lat=${it.latitude} lon=${it.longitude} acc=${it.accuracy}m provider=${it.provider}")
                    trySend(it)
                }

                // Check if interval changed — restart if so
                val newInterval = getIntervalMs()
                if (newInterval != currentIntervalMs) {
                    currentIntervalMs = newInterval
                    fusedClient.removeLocationUpdates(this)
                    startUpdates(this)
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                DebugLog.gps("LocationProvider", "LocationAvailability: ${availability.isLocationAvailable}")
            }
        }
        activeCallback = callback
        DebugLog.info("LocationProvider", "Starting FusedClient updates (interval=${currentIntervalMs}ms, minDist=${MIN_DISTANCE_METERS}m)")
        startUpdates(callback)
        awaitClose {
            fusedClient.removeLocationUpdates(callback)
            activeCallback = null
        }
    }.shareIn(scope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000), replay = 1)

    @SuppressLint("MissingPermission")
    private fun startUpdates(callback: LocationCallback) {
        fusedClient.requestLocationUpdates(
            buildLocationRequest(),
            callback,
            Looper.getMainLooper()
        )
    }

    fun startTracking() {
        // Tracking starts automatically when locationFlow is collected
        // If callback was killed by stopTracking, restart it
        activeCallback?.let {
            DebugLog.info("LocationProvider", "startTracking: callback alive, re-requesting updates")
            startUpdates(it)
        } ?: DebugLog.warn("LocationProvider", "startTracking: no active callback (flow not collected?)")
    }

    fun stopTracking() {
        // DON'T remove the callback — this kills the shared flow permanently
        // Just log for now; the flow lifecycle is managed by shareIn
        DebugLog.info("LocationProvider", "stopTracking called (no-op, flow manages lifecycle)")
    }

    /** Force restart location updates — used by watchdog when GPS is stale */
    @SuppressLint("MissingPermission")
    fun forceRestart() {
        activeCallback?.let { cb ->
            DebugLog.warn("LocationProvider", "forceRestart: removing and re-adding callback")
            fusedClient.removeLocationUpdates(cb)
            startUpdates(cb)
        } ?: DebugLog.error("LocationProvider", "forceRestart: no active callback!")
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? {
        return try {
            fusedClient.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        return try {
            val cts = CancellationTokenSource()
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cts.token
            ).await()
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val MIN_DISTANCE_METERS = 0f           // No minimum distance filter
    }
}
