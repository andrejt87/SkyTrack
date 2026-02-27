package com.skytrack.data.airports

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AirportDao {

    @Query("SELECT * FROM airports WHERE iata = :code LIMIT 1")
    suspend fun findByIata(code: String): AirportEntity?

    @Query("""
        SELECT * FROM airports 
        WHERE iata LIKE '%' || :query || '%' 
        OR name LIKE '%' || :query || '%' 
        OR city LIKE '%' || :query || '%'
        ORDER BY 
            CASE WHEN iata = :query THEN 0
                 WHEN iata LIKE :query || '%' THEN 1
                 ELSE 2 END
        LIMIT 20
    """)
    suspend fun search(query: String): List<AirportEntity>

    @Query("""
        SELECT *, 
        ((:lat - latitude) * (:lat - latitude) + (:lon - longitude) * (:lon - longitude)) as dist
        FROM airports
        WHERE latitude BETWEEN :lat - :radiusDeg AND :lat + :radiusDeg
        AND longitude BETWEEN :lon - :radiusDeg AND :lon + :radiusDeg
        ORDER BY dist
        LIMIT 10
    """)
    suspend fun findNearest(lat: Double, lon: Double, radiusDeg: Double): List<AirportEntity>

    @Query("SELECT COUNT(*) FROM airports")
    suspend fun count(): Int
}
