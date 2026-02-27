package com.skytrack.data.gazetteer

import com.skytrack.domain.model.PlaceInfo
import com.skytrack.domain.repository.GazetteerRepository
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline reverse geocoding using pre-computed Natural Earth polygons.
 * Provides country/ocean/continent lookup from coordinates.
 * Timezone lookup from IANA timezone boundaries.
 */
@Singleton
class GazetteerRepositoryImpl @Inject constructor(
    private val gazetteerDao: GazetteerDao
) : GazetteerRepository {

    override suspend fun reverseGeocode(latitude: Double, longitude: Double): PlaceInfo {
        val result = gazetteerDao.findCountryAt(latitude, longitude)
        return if (result != null) {
            PlaceInfo(
                countryCode = result.countryCode,
                countryName = result.countryName,
                regionName = result.regionName,
                continentName = result.continentName
            )
        } else {
            // Over ocean
            val ocean = determineOcean(latitude, longitude)
            PlaceInfo(
                countryCode = "",
                countryName = "",
                oceanName = ocean,
                continentName = ""
            )
        }
    }

    override suspend fun getTimezone(latitude: Double, longitude: Double): ZoneId {
        val tzName = gazetteerDao.findTimezoneAt(latitude, longitude)
        return try {
            ZoneId.of(tzName ?: "UTC")
        } catch (e: Exception) {
            ZoneId.of("UTC")
        }
    }

    private fun determineOcean(lat: Double, lon: Double): String {
        return when {
            lat > 60 -> "Arctic Ocean"
            lat < -60 -> "Southern Ocean"
            lon in -80.0..0.0 && lat > 0 -> "North Atlantic Ocean"
            lon in -80.0..20.0 && lat < 0 -> "South Atlantic Ocean"
            lon in 20.0..150.0 && lat > 0 -> "Indian Ocean"
            else -> "Pacific Ocean"
        }
    }
}
