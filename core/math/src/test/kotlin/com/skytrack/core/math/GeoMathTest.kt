package com.skytrack.core.math

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GeoMathTest {

    @Test
    fun `haversine distance between JFK and LHR is approximately 5540 km`() {
        val distance = GeoMath.haversineDistance(
            40.6413, -73.7781,  // JFK
            51.4700, -0.4543    // LHR
        )
        assertThat(distance).isWithin(50.0).of(5540.0)
    }

    @Test
    fun `haversine distance same point is zero`() {
        val distance = GeoMath.haversineDistance(52.52, 13.405, 52.52, 13.405)
        assertThat(distance).isWithin(0.001).of(0.0)
    }

    @Test
    fun `initial bearing from JFK to LHR is roughly northeast`() {
        val bearing = GeoMath.initialBearing(
            40.6413, -73.7781,
            51.4700, -0.4543
        )
        assertThat(bearing).isWithin(10.0).of(51.0)
    }

    @Test
    fun `great circle points generates correct number of points`() {
        val points = GeoMath.greatCirclePoints(
            40.6413, -73.7781,
            51.4700, -0.4543,
            numPoints = 50
        )
        assertThat(points).hasSize(50)
        // First point should be near departure
        assertThat(points.first().first).isWithin(0.01).of(40.6413)
        // Last point should be near arrival
        assertThat(points.last().first).isWithin(0.01).of(51.4700)
    }
}
