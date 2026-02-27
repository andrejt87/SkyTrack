package com.skytrack.data.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "telemetry_points")
data class TelemetryPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val flightId: String,
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double,
    val speedKmh: Double,
    val headingDegrees: Float,
    val progress: Float,
    val timestamp: Long = System.currentTimeMillis()
)
