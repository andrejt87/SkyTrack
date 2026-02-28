package com.skytrack.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.skytrack.app.data.model.Airport
import com.skytrack.app.data.model.Flight
import com.skytrack.app.data.model.TrackPoint

@Database(
    entities = [Airport::class, Flight::class, TrackPoint::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun airportDao(): AirportDao
    abstract fun flightDao(): FlightDao
    abstract fun trackPointDao(): TrackPointDao

    companion object {
        const val DATABASE_NAME = "skytrack.db"
    }
}
