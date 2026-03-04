package com.skytrack.app.data.map

import android.content.Context
import android.util.Log
import org.osmdroid.tileprovider.IRegisterReceiver
import org.osmdroid.tileprovider.MapTileProviderArray
import org.osmdroid.tileprovider.modules.IArchiveFile
import org.osmdroid.tileprovider.modules.MBTilesFileArchive
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.views.MapView
import java.io.File
import java.io.FileOutputStream
import java.util.WeakHashMap

/**
 * Map tile strategy:
 * 1. Online → OSM tiles from internet (all zoom levels)
 * 2. Offline + detail MBTiles (z0-8) → use downloaded detail tiles
 * 3. Offline + no detail MBTiles → use bundled base tiles (z0-3, continent contours)
 *
 * The download banner is shown when online but detail MBTiles are missing.
 */
object OfflineTileProvider {

    private const val TAG = "OfflineTileProvider"
    private const val MBTILES_DIR = "mbtiles"
    private const val DETAIL_FILENAME = "skytrack-world.mbtiles"
    private const val BASE_ASSET = "skytrack-base.mbtiles"
    private const val BASE_FILENAME = "skytrack-base.mbtiles"

    private val OFFLINE_TILE_SOURCE = XYTileSource(
        "SkyTrackOffline", 0, 12, 256, ".png", arrayOf<String>()
    )

    private val configuredState = WeakHashMap<MapView, Boolean>()

    /**
     * Configures the MapView with the best available tile source.
     * Only reconfigures when online/offline state actually changes per MapView.
     */
    fun configureMap(mapView: MapView, context: Context): Boolean {
        val online = NetworkMonitor.isOnline.value
        if (configuredState[mapView] == online) return online
        configuredState[mapView] = online
        return if (online) {
            Log.d(TAG, "Online — using MAPNIK tiles")
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setUseDataConnection(true)
            mapView.invalidate()
            true
        } else {
            // Offline: try detail MBTiles, then base MBTiles
            val detailFile = getDetailMbtilesFile(context)
            if (detailFile != null) {
                Log.i(TAG, "Offline — using detail MBTiles (${detailFile.length() / 1024 / 1024} MB)")
                configureMbtiles(mapView, context, detailFile)
            } else {
                val baseFile = getBaseMbtilesFile(context)
                if (baseFile != null) {
                    Log.i(TAG, "Offline — using base MBTiles (continent contours)")
                    configureMbtiles(mapView, context, baseFile)
                } else {
                    Log.w(TAG, "Offline — no tiles available")
                    mapView.setUseDataConnection(false)
                    false
                }
            }
        }
    }

    private fun configureMbtiles(mapView: MapView, context: Context, file: File): Boolean {
        return try {
            val receiver: IRegisterReceiver = SimpleRegisterReceiver(context)
            val archive: IArchiveFile = MBTilesFileArchive.getDatabaseFileArchive(file)
            val provider = MapTileProviderArray(
                OFFLINE_TILE_SOURCE, null,
                arrayOf<MapTileModuleProviderBase>(
                    MapTileFileArchiveProvider(receiver, OFFLINE_TILE_SOURCE, arrayOf(archive))
                )
            )
            mapView.tileProvider = provider
            mapView.setUseDataConnection(false)
            mapView.setTileSource(OFFLINE_TILE_SOURCE)
            mapView.minZoomLevel = 0.0
            mapView.maxZoomLevel = getMaxZoomFromFile(file)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to configure MBTiles", e)
            false
        }
    }

    // ─── File lookups ────────────────────────────────────────────────────

    /** Detail MBTiles (downloaded by user, z0-8) */
    private fun getDetailMbtilesFile(context: Context): File? {
        // MapDownloadManager location
        val downloaded = MapDownloadManager.getMbtilesFile(context)
        if (downloaded != null) return downloaded

        // Legacy internal storage
        val legacy = File(File(context.filesDir, MBTILES_DIR), DETAIL_FILENAME)
        if (legacy.exists() && legacy.length() > 1024) return legacy

        return null
    }

    /** Base MBTiles (bundled in APK assets, z0-3) */
    private fun getBaseMbtilesFile(context: Context): File? {
        val outDir = File(context.filesDir, MBTILES_DIR)
        val outFile = File(outDir, BASE_FILENAME)

        // Already copied
        if (outFile.exists() && outFile.length() > 0) return outFile

        // Copy from assets
        return try {
            val assets = context.assets.list("") ?: return null
            if (BASE_ASSET !in assets) return null

            outDir.mkdirs()
            context.assets.open(BASE_ASSET).use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
            Log.i(TAG, "Copied base MBTiles from assets: ${outFile.length() / 1024} KB")
            outFile
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy base MBTiles from assets", e)
            null
        }
    }

    // ─── Utils ───────────────────────────────────────────────────────────

    private fun getMaxZoomFromFile(file: File): Double {
        return try {
            val db = android.database.sqlite.SQLiteDatabase.openDatabase(
                file.absolutePath, null, android.database.sqlite.SQLiteDatabase.OPEN_READONLY
            )
            val cursor = db.rawQuery("SELECT value FROM metadata WHERE name = 'maxzoom'", null)
            val maxZoom = if (cursor.moveToFirst()) cursor.getString(0).toDoubleOrNull() ?: 8.0 else 8.0
            cursor.close(); db.close()
            maxZoom
        } catch (e: Exception) { 8.0 }
    }

    fun isDetailMapAvailable(context: Context): Boolean = getDetailMbtilesFile(context) != null
}
