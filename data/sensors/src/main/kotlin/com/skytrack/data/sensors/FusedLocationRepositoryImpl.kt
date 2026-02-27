package com.skytrack.data.sensors

import android.annotation.SuppressLint
import android.os.Looper
import com.google.android.gms.location.*
import com.skytrack.domain.model.Position
import com.skytrack.domain.repository.LocationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FusedLocationRepositoryImpl @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationRepository {

    private var lastPosition: Position? = null

    @SuppressLint("MissingPermission")
    override fun locationFlow(): Flow<Position> = callbackFlow {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L // 1 Hz as specified in FA-001
        ).setMinUpdateIntervalMillis(500L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val position = Position(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        altitudeMeters = location.altitude,
                        speedKmh = location.speed.toDouble() * 3.6, // m/s to km/h
                        headingDegrees = location.bearing,
                        accuracy = location.accuracy,
                        timestamp = Instant.ofEpochMilli(location.time)
                    )
                    lastPosition = position
                    trySend(position)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun lastKnownPosition(): Position? {
        return lastPosition
    }
}
