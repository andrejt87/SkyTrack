package com.skytrack.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "track_points",
    foreignKeys = [ForeignKey(
        entity = Flight::class,
        parentColumns = ["id"],
        childColumns = ["flightId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("flightId")]
)
data class TrackPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val flightId: Long,
    val lat: Double,
    val lon: Double,
    val altitudeM: Double,
    val speedKmh: Double,
    val heading: Float,
    val accuracy: Float,
    val timestamp: Long
)
