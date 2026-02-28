package com.skytrack.app.data.db

import androidx.room.*
import com.skytrack.app.data.model.TrackPoint
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackPointDao {

    @Insert
    suspend fun insert(trackPoint: TrackPoint)

    @Insert
    suspend fun insertAll(trackPoints: List<TrackPoint>)

    @Query("SELECT * FROM track_points WHERE flightId = :flightId ORDER BY timestamp ASC")
    fun getTrackPointsForFlight(flightId: Long): Flow<List<TrackPoint>>

    @Query("SELECT * FROM track_points WHERE flightId = :flightId ORDER BY timestamp ASC")
    suspend fun getTrackPointsForFlightOnce(flightId: Long): List<TrackPoint>

    @Query("SELECT COUNT(*) FROM track_points WHERE flightId = :flightId")
    suspend fun getTrackPointCount(flightId: Long): Int

    @Query("DELETE FROM track_points WHERE flightId = :flightId")
    suspend fun deleteByFlightId(flightId: Long)
}
