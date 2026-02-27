package com.skytrack.data.persistence

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlight(flight: FlightEntity)

    @Query("SELECT * FROM flights WHERE state NOT IN ('ARRIVED', 'PARKED') ORDER BY createdAt DESC LIMIT 1")
    fun observeActiveFlight(): Flow<FlightEntity?>

    @Query("SELECT * FROM flights WHERE state NOT IN ('ARRIVED', 'PARKED') ORDER BY createdAt DESC LIMIT 1")
    suspend fun getActiveFlight(): FlightEntity?

    @Query("UPDATE flights SET state = :state WHERE id = :flightId")
    suspend fun updateState(flightId: String, state: String)

    @Query("SELECT * FROM flights ORDER BY createdAt DESC")
    suspend fun getAllFlights(): List<FlightEntity>

    @Query("SELECT * FROM flights WHERE id = :id")
    suspend fun getFlightById(id: String): FlightEntity?

    @Insert
    suspend fun insertTelemetryPoint(point: TelemetryPointEntity)

    @Query("SELECT * FROM telemetry_points WHERE flightId = :flightId ORDER BY timestamp ASC")
    suspend fun getTelemetryForFlight(flightId: String): List<TelemetryPointEntity>
}
