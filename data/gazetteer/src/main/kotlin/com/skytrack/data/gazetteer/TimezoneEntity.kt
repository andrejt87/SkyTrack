package com.skytrack.data.gazetteer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timezones")
data class TimezoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timezoneName: String,
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double
)
