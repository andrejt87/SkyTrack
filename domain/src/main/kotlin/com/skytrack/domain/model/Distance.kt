package com.skytrack.domain.model

data class Distance(
    val km: Double
) {
    val miles: Double get() = km * 0.621371
    val nauticalMiles: Double get() = km * 0.539957
}
