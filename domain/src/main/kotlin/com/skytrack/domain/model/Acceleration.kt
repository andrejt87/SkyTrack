package com.skytrack.domain.model

data class Acceleration(
    val x: Float,
    val y: Float,
    val z: Float
) {
    val magnitude: Float get() = kotlin.math.sqrt(x * x + y * y + z * z)
}
