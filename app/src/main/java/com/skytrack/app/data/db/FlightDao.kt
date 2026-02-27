package com.skytrack.app.data.db

import androidx.room.*
import com.skytrack.app.data.model.Flight
import com.skytrack.app.data.model.FlightStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {

    @Query("SELECT * FROM flights ORDER BY createdAtMs DESC")
    fun getAllFlights(): Flow<List<Flight>>

    @Query("SELECT * FROM flights WHERE id = :id LIMIT 1")
    fun getFlightById(id: Long): Flow<Flight?>

    @Query("SELECT * FROM flights WHERE id = :id LIMIT 1")
    suspend fun getFlightByIdOnce(id: Long): Flight?

    @Query("SELECT * FROM flights WHERE status = :status ORDER BY createdAtMs DESC")
    fun getFlightsByStatus(status: FlightStatus): Flow<List<Flight>>

    @Query("""
        SELECT * FROM flights
        WHERE status IN ('AIRBORNE', 'DESCENDING', 'BOARDING')
        ORDER BY createdAtMs DESC
        LIMIT 1
    """)
    fun getActiveFlight(): Flow<Flight?>

    @Query("""
        SELECT * FROM flights
        WHERE status = 'COMPLETED'
        ORDER BY createdAtMs DESC
    """)
    fun getCompletedFlights(): Flow<List<Flight>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flight: Flight): Long

    @Update
    suspend fun update(flight: Flight)

    @Delete
    suspend fun delete(flight: Flight)

    @Query("DELETE FROM flights WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM flights WHERE status = 'COMPLETED'")
    suspend fun getCompletedFlightCount(): Int

    @Query("""
        SELECT SUM(totalDistanceKm) FROM flights
        WHERE status = 'COMPLETED'
    """)
    suspend fun getTotalDistanceKm(): Double?

    @Query("""
        SELECT SUM(actualArrivalMs - actualDepartureMs) FROM flights
        WHERE status = 'COMPLETED' AND actualArrivalMs > 0
    """)
    suspend fun getTotalFlightTimeMs(): Long?

    @Query("""
        SELECT MAX(maxAltitudeM) FROM flights
        WHERE status = 'COMPLETED'
    """)
    suspend fun getPersonalAltitudeRecord(): Double?

    @Query("""
        SELECT MAX(maxSpeedKmh) FROM flights
        WHERE status = 'COMPLETED'
    """)
    suspend fun getPersonalSpeedRecord(): Double?
}
