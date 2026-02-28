package com.skytrack.app

import android.app.Application
import android.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import java.io.File
import javax.inject.Inject
import com.skytrack.app.data.repository.AirportRepository

@HiltAndroidApp
class SkyTrackApplication : Application() {

    @Inject
    lateinit var airportRepository: AirportRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        // Initialize network monitor
        com.skytrack.app.data.map.NetworkMonitor.init(applicationContext)

        // Configure OSMDroid
        Configuration.getInstance().apply {
            load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
            userAgentValue = packageName
            // Set tile cache size: 256 MB
            tileFileSystemCacheMaxBytes = 256L * 1024L * 1024L
            tileFileSystemCacheTrimBytes = 128L * 1024L * 1024L
            // Set osmdroid base path to app-internal storage (no SD card permission needed)
            val basePath = File(applicationContext.filesDir, "osmdroid")
            basePath.mkdirs()
            osmdroidBasePath = basePath
            osmdroidTileCache = File(basePath, "tiles")
            osmdroidTileCache.mkdirs()
        }

        // Seed airport database from bundled JSON
        applicationScope.launch {
            airportRepository.seedDatabaseIfEmpty()
        }

        // Set global metric preference
        val prefs = getSharedPreferences("skytrack_prefs", MODE_PRIVATE)
        com.skytrack.app.data.model.FlightProgress.useMetric = prefs.getBoolean("use_metric", true)
    }
}
