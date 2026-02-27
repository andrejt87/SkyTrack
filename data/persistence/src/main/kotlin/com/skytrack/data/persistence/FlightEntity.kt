package com.skytrack.data.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flights")
data class FlightEntity(
    @PrimaryKey val id: String,
    val departureIata: String,
    val arrivalIata: String,
    val departureName: String,
    val arrivalName: String,
    val departureLat: Double,
    val departureLon: Double,
    val arrivalLat: Double,
    val arrivalLon: Double,
    val totalDistanceKm: Double,
    val state: String,
    val maxAltitudeM: Double = 0.0,
    val maxSpeedKmh: Double = 0.0,
    val totalDurationSeconds: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)
