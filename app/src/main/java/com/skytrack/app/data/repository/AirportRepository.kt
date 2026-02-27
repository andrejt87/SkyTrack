package com.skytrack.app.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.skytrack.app.data.db.AirportDao
import com.skytrack.app.data.model.Airport
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AirportRepository @Inject constructor(
    private val airportDao: AirportDao,
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    fun getAllAirports(): Flow<List<Airport>> = airportDao.getAllAirports()

    suspend fun getAirportByIata(iata: String): Airport? =
        airportDao.getAirportByIata(iata.uppercase())

    suspend fun getAirportByIcao(icao: String): Airport? =
        airportDao.getAirportByIcao(icao.uppercase())

    suspend fun searchAirports(query: String): List<Airport> {
        if (query.isBlank()) return emptyList()
        val trimmed = query.trim()
        val likeQuery = "%${trimmed}%"
        return airportDao.searchAirports(likeQuery, trimmed.uppercase())
    }

    suspend fun getAirportCount(): Int = airportDao.getAirportCount()

    /**
     * Seeds the database from assets/airports.json if the database is empty.
     */
    suspend fun seedDatabaseIfEmpty() {
        val count = airportDao.getAirportCount()
        if (count == 0) {
            val airports = loadAirportsFromAssets()
            if (airports.isNotEmpty()) {
                airportDao.insertAll(airports)
            }
        }
    }

    private fun loadAirportsFromAssets(): List<Airport> {
        return try {
            val json = context.assets.open("airports.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<AirportJson>>() {}.type
            val raw: List<AirportJson> = gson.fromJson(json, type)
            raw.mapNotNull { it.toAirport() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** JSON DTO – mirrors the airports.json structure */
    private data class AirportJson(
        val iata: String?,
        val icao: String?,
        val name: String?,
        val city: String?,
        val country: String?,
        val lat: Double?,
        val lon: Double?,
        val tz: String?
    ) {
        fun toAirport(): Airport? {
            if (iata.isNullOrBlank() || lat == null || lon == null) return null
            return Airport(
                iata = iata.uppercase(),
                icao = icao?.uppercase() ?: "",
                name = name ?: iata,
                city = city ?: "",
                country = country ?: "",
                lat = lat,
                lon = lon,
                tz = tz ?: "UTC"
            )
        }
    }
}
