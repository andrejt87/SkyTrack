package com.skytrack.domain.model

data class Airport(
    val iata: String,
    val icao: String,
    val name: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Int,
    val timezone: String = ""
)
