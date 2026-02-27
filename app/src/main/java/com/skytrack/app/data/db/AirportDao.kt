package com.skytrack.app.data.db

import androidx.room.*
import com.skytrack.app.data.model.Airport
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {

    @Query("SELECT * FROM airports ORDER BY iata ASC")
    fun getAllAirports(): Flow<List<Airport>>

    @Query("SELECT * FROM airports WHERE iata = :iata LIMIT 1")
    suspend fun getAirportByIata(iata: String): Airport?

    @Query("SELECT * FROM airports WHERE icao = :icao LIMIT 1")
    suspend fun getAirportByIcao(icao: String): Airport?

    @Query("""
        SELECT * FROM airports
        WHERE iata LIKE :query
           OR icao LIKE :query
           OR name LIKE :query
           OR city LIKE :query
        ORDER BY
            CASE WHEN iata = :exactQuery THEN 0
                 WHEN iata LIKE :query THEN 1
                 WHEN city LIKE :query THEN 2
                 ELSE 3 END,
            name ASC
        LIMIT 50
    """)
    suspend fun searchAirports(query: String, exactQuery: String = query): List<Airport>

    @Query("SELECT COUNT(*) FROM airports")
    suspend fun getAirportCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(airports: List<Airport>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(airport: Airport)

    @Delete
    suspend fun delete(airport: Airport)

    @Query("DELETE FROM airports")
    suspend fun deleteAll()

    @Query("""
        SELECT * FROM airports
        WHERE iata IN (:iataList)
        ORDER BY name ASC
    """)
    suspend fun getAirportsByIataList(iataList: List<String>): List<Airport>
}
