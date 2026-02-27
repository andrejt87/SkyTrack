package com.skytrack.domain.model

import java.time.Instant

data class TelemetryPoint(
    val position: Position,
    val progress: Float,
    val timestamp: Instant = Instant.now()
)
