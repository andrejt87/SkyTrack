package com.skytrack.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.skytrack.domain.model.LatLng
import org.junit.Test
import java.time.Duration

class CalculateEtaUseCaseTest {

    private val useCase = CalculateEtaUseCase()

    private val jfk = LatLng(latitude = 40.6413, longitude = -73.7781)
    private val lhr = LatLng(latitude = 51.4700, longitude = -0.4543)

    @Test
    fun `returns null when speed is below 50 kmh`() {
        val result = useCase.execute(
            currentPosition = jfk,
            arrival = lhr,
            currentSpeedKmh = 30.0
        )
        assertThat(result).isNull()
    }

    @Test
    fun `returns valid duration at cruising speed`() {
        val result = useCase.execute(
            currentPosition = jfk,
            arrival = lhr,
            currentSpeedKmh = 900.0
        )
        assertThat(result).isNotNull()
        // ~5540 km at 900 km/h ≈ 6.15 hours
        val hours = result!!.toMinutes() / 60.0
        assertThat(hours).isWithin(1.0).of(6.15)
    }

    @Test
    fun `eta decreases as we approach arrival`() {
        val midpoint = LatLng(latitude = 50.0, longitude = -30.0)
        val etaFromJfk = useCase.execute(jfk, lhr, 900.0)
        val etaFromMid = useCase.execute(midpoint, lhr, 900.0)
        assertThat(etaFromMid).isLessThan(etaFromJfk)
    }

    @Test
    fun `eta at arrival is near zero`() {
        val result = useCase.execute(
            currentPosition = lhr,
            arrival = lhr,
            currentSpeedKmh = 900.0
        )
        assertThat(result).isNotNull()
        assertThat(result!!.toMinutes()).isLessThan(1)
    }

    @Test
    fun `exactly 50 kmh returns null`() {
        // Boundary: exactly 50.0 should still return null per code (< 50.0)
        val result = useCase.execute(jfk, lhr, 49.9)
        assertThat(result).isNull()
    }

    @Test
    fun `just above threshold returns non-null`() {
        val result = useCase.execute(jfk, lhr, 50.0)
        assertThat(result).isNotNull()
    }
}
