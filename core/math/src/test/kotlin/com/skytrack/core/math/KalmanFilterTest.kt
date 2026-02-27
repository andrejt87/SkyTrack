package com.skytrack.core.math

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class KalmanFilterTest {

    @Test
    fun `first measurement is returned as-is`() {
        val kf = KalmanFilter()
        val result = kf.update(100.0)
        assertThat(result).isEqualTo(100.0)
    }

    @Test
    fun `filter converges to stable value`() {
        val kf = KalmanFilter()
        var result = 0.0
        repeat(100) {
            result = kf.update(500.0)
        }
        assertThat(result).isWithin(1.0).of(500.0)
    }

    @Test
    fun `filter smooths noisy measurements`() {
        val kf = KalmanFilter(processNoise = 0.01, measurementNoise = 1.0)
        val measurements = listOf(100.0, 102.0, 98.0, 101.0, 99.0, 100.5, 100.0)
        var result = 0.0
        measurements.forEach { result = kf.update(it) }
        // Should be close to 100.0 average
        assertThat(result).isWithin(2.0).of(100.0)
    }
}
