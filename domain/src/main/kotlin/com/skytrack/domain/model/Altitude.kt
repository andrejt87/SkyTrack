package com.skytrack.domain.model

data class Altitude(
    val meters: Double
) {
    val feet: Double get() = meters * 3.28084
}
