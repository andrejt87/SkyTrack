package com.skytrack.data.airports

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "airports")
data class AirportEntity(
    @PrimaryKey val iata: String,
    val icao: String,
    val name: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Int,
    val timezone: String
)
