package com.skytrack.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "airports")
data class Airport(
    @PrimaryKey
    val iata: String,
    val icao: String,
    val name: String,
    val city: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz: String
) {
    /**
     * Returns a display string like "FRA - Frankfurt Airport"
     */
    val displayName: String
        get() = "$iata – $name"

    /**
     * Returns short display: "FRA (Frankfurt)"
     */
    val shortDisplay: String
        get() = "$iata ($city)"
}
