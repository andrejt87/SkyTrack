package com.skytrack.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.skytrack.app.data.model.Airport
import com.skytrack.app.data.model.Flight

@Database(
    entities = [Airport::class, Flight::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun airportDao(): AirportDao
    abstract fun flightDao(): FlightDao

    companion object {
        const val DATABASE_NAME = "skytrack.db"
    }
}
