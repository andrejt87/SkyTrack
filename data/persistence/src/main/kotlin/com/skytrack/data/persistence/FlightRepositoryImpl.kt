package com.skytrack.data.persistence

import com.skytrack.domain.model.*
import com.skytrack.domain.repository.AirportRepository
import com.skytrack.domain.repository.FlightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlightRepositoryImpl @Inject constructor(
    private val flightDao: FlightDao,
    private val airportRepository: AirportRepository
) : FlightRepository {

    override fun observeActiveFlight(): Flow<Flight?> {
        return flightDao.observeActiveFlight().map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun getActiveFlight(): Flight? {
        return flightDao.getActiveFlight()?.toDomain()
    }

    override suspend fun startFlight(flight: Flight): String {
        val entity = FlightEntity(
            id = flight.id,
            departureIata = flight.departure.iata,
            arrivalIata = flight.arrival.iata,
            departureName = flight.departure.name,
            arrivalName = flight.arrival.name,
            departureLat = flight.departure.latitude,
            departureLon = flight.departure.longitude,
            arrivalLat = flight.arrival.latitude,
            arrivalLon = flight.arrival.longitude,
            totalDistanceKm = flight.config.totalDistanceKm,
            state = flight.state.name
        )
        flightDao.insertFlight(entity)
        return flight.id
    }

    override suspend fun updateTrackingState(flightId: String, state: TrackingState) {
        flightDao.updateState(flightId, state.name)
    }

    override suspend fun addTelemetryPoint(flightId: String, point: TelemetryPoint) {
        val entity = TelemetryPointEntity(
            flightId = flightId,
            latitude = point.position.latitude,
            longitude = point.position.longitude,
            altitudeMeters = point.position.altitudeMeters,
            speedKmh = point.position.speedKmh,
            headingDegrees = point.position.headingDegrees,
            progress = point.progress,
            timestamp = point.timestamp.toEpochMilli()
        )
        flightDao.insertTelemetryPoint(entity)
    }

    override suspend fun completeFlight(flightId: String) {
        flightDao.updateState(flightId, TrackingState.ARRIVED.name)
    }

    override suspend fun getAllFlights(): List<Flight> {
        return flightDao.getAllFlights().map { it.toDomain() }
    }

    override suspend fun getFlightById(id: String): Flight? {
        return flightDao.getFlightById(id)?.toDomain()
    }

    private suspend fun FlightEntity.toDomain(): Flight {
        val depAirport = airportRepository.findByIata(departureIata) ?: Airport(
            iata = departureIata, icao = "", name = departureName, city = "",
            country = "", latitude = departureLat, longitude = departureLon, elevation = 0
        )
        val arrAirport = airportRepository.findByIata(arrivalIata) ?: Airport(
            iata = arrivalIata, icao = "", name = arrivalName, city = "",
            country = "", latitude = arrivalLat, longitude = arrivalLon, elevation = 0
        )

        val telemetry = flightDao.getTelemetryForFlight(id).map { tp ->
            TelemetryPoint(
                position = Position(
                    latitude = tp.latitude,
                    longitude = tp.longitude,
                    altitudeMeters = tp.altitudeMeters,
                    speedKmh = tp.speedKmh,
                    headingDegrees = tp.headingDegrees,
                    timestamp = Instant.ofEpochMilli(tp.timestamp)
                ),
                progress = tp.progress,
                timestamp = Instant.ofEpochMilli(tp.timestamp)
            )
        }

        return Flight(
            id = id,
            departure = depAirport,
            arrival = arrAirport,
            config = FlightConfig(
                departureIata = departureIata,
                arrivalIata = arrivalIata,
                totalDistanceKm = totalDistanceKm
            ),
            log = FlightLog(
                telemetry = telemetry,
                maxAltitudeM = maxAltitudeM,
                maxSpeedKmh = maxSpeedKmh,
                totalDurationSeconds = totalDurationSeconds
            ),
            state = TrackingState.valueOf(state),
            createdAt = Instant.ofEpochMilli(createdAt)
        )
    }
}
