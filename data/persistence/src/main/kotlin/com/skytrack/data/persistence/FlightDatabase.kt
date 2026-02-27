package com.skytrack.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FlightEntity::class, TelemetryPointEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FlightDatabase : RoomDatabase() {
    abstract fun flightDao(): FlightDao
}
