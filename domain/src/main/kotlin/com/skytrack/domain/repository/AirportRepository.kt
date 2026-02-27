package com.skytrack.domain.repository

import com.skytrack.domain.model.Airport

interface AirportRepository {
    suspend fun findByIata(code: String): Airport?
    suspend fun search(query: String): List<Airport>
    suspend fun findNearest(latitude: Double, longitude: Double, radiusKm: Double = 50.0): List<Airport>
}
