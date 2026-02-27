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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        UPDATE_INTERVAL_MS
    )
        .setMinUpdateIntervalMillis(MIN_UPDATE_INTERVAL_MS)
        .setMinUpdateDistanceMeters(MIN_DISTANCE_METERS)
        .setWaitForAccurateLocation(false)
        .build()

    /** Shared hot Flow of location updates. Starts on first collector, stops when none remain. */
    val locationFlow: Flow<Location> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it) }
            }
        }
        startUpdates(callback)
        awaitClose { fusedClient.removeLocationUpdates(callback) }
    }.shareIn(scope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000), replay = 1)

    private var activeCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    private fun startUpdates(callback: LocationCallback) {
        fusedClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )
    }

    fun startTracking() {
        // Tracking starts automatically when locationFlow is collected
    }

    fun stopTracking() {
        activeCallback?.let { fusedClient.removeLocationUpdates(it) }
        activeCallback = null
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
        private const val UPDATE_INTERVAL_MS = 5_000L      // 5 seconds
        private const val MIN_UPDATE_INTERVAL_MS = 2_000L  // 2 seconds minimum
        private const val MIN_DISTANCE_METERS = 50f         // 50 metres minimum
    }
}
