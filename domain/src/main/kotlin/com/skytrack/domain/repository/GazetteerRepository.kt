package com.skytrack.domain.repository

import com.skytrack.domain.model.PlaceInfo
import java.time.ZoneId

interface GazetteerRepository {
    suspend fun reverseGeocode(latitude: Double, longitude: Double): PlaceInfo
    suspend fun getTimezone(latitude: Double, longitude: Double): ZoneId
}
