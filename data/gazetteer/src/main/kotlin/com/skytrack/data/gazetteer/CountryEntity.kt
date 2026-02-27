package com.skytrack.data.gazetteer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey val countryCode: String,
    val countryName: String,
    val regionName: String?,
    val continentName: String,
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double
)
