package com.skytrack.domain.usecase

import com.skytrack.domain.model.*
import com.skytrack.domain.repository.AirportRepository
import com.skytrack.domain.repository.FlightRepository
import java.util.UUID

class StartFlightUseCase(
    private val flightRepository: FlightRepository,
    private val airportRepository: AirportRepository
) {
    suspend fun execute(departureIata: String, arrivalIata: String): Result<Flight> {
        val departure = airportRepository.findByIata(departureIata)
            ?: return Result.failure(IllegalArgumentException("Departure airport not found: $departureIata"))
        val arrival = airportRepository.findByIata(arrivalIata)
            ?: return Result.failure(IllegalArgumentException("Arrival airport not found: $arrivalIata"))

        val depLatLng = LatLng(departure.latitude, departure.longitude)
        val arrLatLng = LatLng(arrival.latitude, arrival.longitude)
        val totalDistance = CalculateFlightProgressUseCase.haversineKm(depLatLng, arrLatLng)

        if (totalDistance < 1.0) {
            return Result.failure(IllegalArgumentException("Invalid route: departure equals arrival"))
        }

        val flight = Flight(
            id = UUID.randomUUID().toString(),
            departure = departure,
            arrival = arrival,
            config = FlightConfig(
                departureIata = departureIata,
                arrivalIata = arrivalIata,
                totalDistanceKm = totalDistance
            ),
            state = TrackingState.ACQUIRING
        )

        flightRepository.startFlight(flight)
        return Result.success(flight)
    }
}
