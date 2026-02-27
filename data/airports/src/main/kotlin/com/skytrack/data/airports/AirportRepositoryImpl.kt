package com.skytrack.data.airports

import com.skytrack.domain.model.Airport
import com.skytrack.domain.repository.AirportRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AirportRepositoryImpl @Inject constructor(
    private val airportDao: AirportDao
) : AirportRepository {

    override suspend fun findByIata(code: String): Airport? {
        return airportDao.findByIata(code.uppercase())?.toDomain()
    }

    override suspend fun search(query: String): List<Airport> {
        return airportDao.search(query).map { it.toDomain() }
    }

    override suspend fun findNearest(latitude: Double, longitude: Double, radiusKm: Double): List<Airport> {
        val radiusDeg = radiusKm / 111.0 // rough km to degree conversion
        return airportDao.findNearest(latitude, longitude, radiusDeg).map { it.toDomain() }
    }

    private fun AirportEntity.toDomain(): Airport = Airport(
        iata = iata,
        icao = icao,
        name = name,
        city = city,
        country = country,
        latitude = latitude,
        longitude = longitude,
        elevation = elevation,
        timezone = timezone
    )
}
