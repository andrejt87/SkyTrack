package com.skytrack.data.gazetteer

import androidx.room.Dao
import androidx.room.Query

@Dao
interface GazetteerDao {

    @Query("""
        SELECT * FROM countries 
        WHERE :lat BETWEEN minLat AND maxLat 
        AND :lon BETWEEN minLon AND maxLon
        LIMIT 1
    """)
    suspend fun findCountryAt(lat: Double, lon: Double): CountryEntity?

    @Query("""
        SELECT timezoneName FROM timezones 
        WHERE :lat BETWEEN minLat AND maxLat 
        AND :lon BETWEEN minLon AND maxLon
        LIMIT 1
    """)
    suspend fun findTimezoneAt(lat: Double, lon: Double): String?
}
