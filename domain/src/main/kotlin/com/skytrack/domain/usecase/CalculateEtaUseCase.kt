package com.skytrack.domain.usecase

import com.skytrack.domain.model.LatLng
import java.time.Duration

class CalculateEtaUseCase {

    fun execute(
        currentPosition: LatLng,
        arrival: LatLng,
        currentSpeedKmh: Double
    ): Duration? {
        if (currentSpeedKmh < 50.0) return null // Too slow to calculate meaningful ETA

        val remainingKm = CalculateFlightProgressUseCase.haversineKm(currentPosition, arrival)
        val hoursRemaining = remainingKm / currentSpeedKmh
        val secondsRemaining = (hoursRemaining * 3600).toLong()

        return Duration.ofSeconds(secondsRemaining)
    }
}
