package com.skytrack.domain.model

import java.time.ZoneId
import java.time.LocalDateTime

data class TimezoneInfo(
    val label: String,
    val zoneId: ZoneId,
    val currentTime: LocalDateTime,
    val offsetHours: Float
)
