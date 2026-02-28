package com.skytrack.app.data.repository

import com.skytrack.app.data.db.FlightDao
import com.skytrack.app.data.db.TrackPointDao
import com.skytrack.app.data.model.Flight
import com.skytrack.app.data.model.FlightStatus
import com.skytrack.app.data.model.TrackPoint
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlightRepository @Inject constructor(
    private val flightDao: FlightDao,
    private val trackPointDao: TrackPointDao
) {
    fun getAllFlights(): Flow<List<Flight>> = flightDao.getAllFlights()

    fun getFlightById(id: Long): Flow<Flight?> = flightDao.getFlightById(id)

    fun getActiveFlight(): Flow<Flight?> = flightDao.getActiveFlight()

    fun getCompletedFlights(): Flow<List<Flight>> = flightDao.getCompletedFlights()

    suspend fun createFlight(flight: Flight): Long = flightDao.insert(flight)

    suspend fun updateFlight(flight: Flight) = flightDao.update(flight)

    suspend fun deleteFlight(flight: Flight) = flightDao.delete(flight)

    suspend fun deleteFlightById(id: Long) = flightDao.deleteById(id)

    suspend fun getCompletedFlightCount(): Int = flightDao.getCompletedFlightCount()

    suspend fun getTotalDistanceKm(): Double = flightDao.getTotalDistanceKm() ?: 0.0

    suspend fun getTotalFlightTimeMs(): Long = flightDao.getTotalFlightTimeMs() ?: 0L

    suspend fun getPersonalAltitudeRecord(): Double = flightDao.getPersonalAltitudeRecord() ?: 0.0

    suspend fun getPersonalSpeedRecord(): Double = flightDao.getPersonalSpeedRecord() ?: 0.0

    suspend fun updateFlightStatus(flightId: Long, newStatus: FlightStatus) {
        val flight = flightDao.getFlightByIdOnce(flightId) ?: return
        flightDao.update(flight.copy(status = newStatus))
    }

    // ─── TrackPoint operations ────────────────────────────────────────────

    suspend fun insertTrackPoint(trackPoint: TrackPoint) =
        trackPointDao.insert(trackPoint)

    fun getTrackPointsForFlight(flightId: Long): Flow<List<TrackPoint>> =
        trackPointDao.getTrackPointsForFlight(flightId)

    suspend fun getTrackPointsForFlightOnce(flightId: Long): List<TrackPoint> =
        trackPointDao.getTrackPointsForFlightOnce(flightId)

    /**
     * Marks the flight as completed, records actual arrival time, and computes stats.
     */
    suspend fun completeFlight(
        flightId: Long,
        actualArrivalMs: Long,
        maxAltitudeM: Double,
        maxSpeedKmh: Double,
        avgSpeedKmh: Double
    ) {
        val flight = flightDao.getFlightByIdOnce(flightId) ?: return
        flightDao.update(
            flight.copy(
                status = FlightStatus.COMPLETED,
                actualArrivalMs = actualArrivalMs,
                maxAltitudeM = maxAltitudeM,
                maxSpeedKmh = maxSpeedKmh,
                avgSpeedKmh = avgSpeedKmh
            )
        )
    }
}
