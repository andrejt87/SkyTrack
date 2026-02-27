package com.skytrack.feature.overfly

data class OverflyUiState(
    val countryName: String = "",
    val regionName: String? = null,
    val oceanName: String? = null,
    val isOverOcean: Boolean = false
)
