package com.skytrack.data.gazetteer

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CountryEntity::class, TimezoneEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GazetteerDatabase : RoomDatabase() {
    abstract fun gazetteerDao(): GazetteerDao
}
