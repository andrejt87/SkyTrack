package com.skytrack.domain.model

data class PlaceInfo(
    val countryCode: String,
    val countryName: String,
    val regionName: String? = null,
    val oceanName: String? = null,
    val continentName: String = ""
)
