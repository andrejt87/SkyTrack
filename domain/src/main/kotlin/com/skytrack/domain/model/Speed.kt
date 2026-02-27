package com.skytrack.domain.model

data class Speed(
    val kmh: Double
) {
    val knots: Double get() = kmh * 0.539957
    val mph: Double get() = kmh * 0.621371
}
