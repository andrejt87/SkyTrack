package com.skytrack.app.data.map

import android.content.Context
import android.util.Log
import org.osmdroid.tileprovider.IRegisterReceiver
import org.osmdroid.tileprovider.modules.ArchiveFileFactory
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.tileprovider.MapTileProviderArray
import org.osmdroid.tileprovider.modules.IArchiveFile
import org.osmdroid.tileprovider.modules.MBTilesFileArchive
import org.osmdroid.views.MapView
import java.io.File
import java.io.FileOutputStream

/**
 * Manages offline MBTiles tile provisioning for OSMDroid MapViews.
 *
 * Strategy:
 * 1. First look for MBTiles in the app's internal files directory
 *    (copied from assets on first launch, or sideloaded by the user).
 * 2. Then check the osmdroid base path (/sdcard/osmdroid/) for MBTiles files.
 * 3. If no MBTiles found, fall back to the online MAPNIK tile source but
 *    disable data connection (shows blank tiles when offline — acceptable
 *    degradation with a clear UI message).
 *
 * The bundled MBTiles file (if present in assets/) should be named:
 *   skytrack-world.mbtiles
 *
 * Zoom levels:
 *   - World overview:  Zoom 0–6  (Natural Earth / OpenStreetMap low-detail)
 *   - Regional detail: Zoom 6–8  (coastlines, major cities, country borders)
 *   - Airport areas:   Zoom 8–12 (optional, for detailed approach views)
 *
 * Max file size: 500 MB
 */
object OfflineTileProvider {

    private const val TAG = "OfflineTileProvider"
    private const val MBTILES_ASSET_NAME = "skytrack-world.mbtiles"
    private const val MBTILES_DIR = "mbtiles"

    /**
     * The offline tile source definition.
     * Name must NOT match any existing online source to avoid conflicts.
     * Min/Max zoom and tile size must match the MBTiles content.
     */
    val SKYTRACK_TILE_SOURCE = XYTileSource(
        "SkyTrackOffline",   // unique name (not matching any online source)
        0,                    // min zoom
        12,                   // max zoom (adjust based on actual MBTiles content)
        256,                  // tile size in pixels
        ".png",               // tile image format
        arrayOf<String>()     // no online URLs — fully offline
    )

    /**
     * Configures the given MapView for offline tile rendering.
     *
     * @param mapView  The OSMDroid MapView to configure
     * @param context  Application context
     * @return true if offline tiles were found and configured, false if fallback
     */
    fun configureOfflineMap(mapView: MapView, context: Context): Boolean {
        val mbtilesFile = findMbtilesFile(context)

        if (mbtilesFile != null && mbtilesFile.exists() && mbtilesFile.length() > 0) {
            Log.i(TAG, "Found offline MBTiles: ${mbtilesFile.absolutePath} (${mbtilesFile.length() / 1024 / 1024} MB)")

            try {
                val receiver: IRegisterReceiver = SimpleRegisterReceiver(context)
                val archive: IArchiveFile = MBTilesFileArchive.getDatabaseFileArchive(mbtilesFile)
                val archiveProvider = MapTileFileArchiveProvider(
                    receiver,
                    SKYTRACK_TILE_SOURCE,
                    arrayOf(archive)
                )

                val provider = MapTileProviderArray(
                    SKYTRACK_TILE_SOURCE,
                    null,
                    arrayOf<MapTileModuleProviderBase>(archiveProvider)
                )

                mapView.tileProvider = provider
                mapView.setUseDataConnection(false)
                mapView.setTileSource(SKYTRACK_TILE_SOURCE)
                mapView.minZoomLevel = 0.0
                mapView.maxZoomLevel = getMaxZoomFromFile(mbtilesFile)

                Log.i(TAG, "Offline MBTiles provider configured successfully. Max zoom: ${mapView.maxZoomLevel}")
                return true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to configure MBTiles provider", e)
            }
        }

        // Fallback: use default tile source but disable data connection
        Log.w(TAG, "No offline MBTiles found. Map will be blank in offline mode.")
        mapView.setUseDataConnection(false)
        return false
    }

    /**
     * Searches for MBTiles files in multiple locations.
     * Priority:
     *   1. App internal files dir (e.g. /data/data/com.skytrack.app/files/mbtiles/)
     *   2. OSMDroid base path (e.g. /sdcard/osmdroid/)
     *   3. App assets (will be copied to internal on first use)
     */
    private fun findMbtilesFile(context: Context): File? {
        // 1. Check internal storage
        val internalDir = File(context.filesDir, MBTILES_DIR)
        val internalFile = File(internalDir, MBTILES_ASSET_NAME)
        if (internalFile.exists() && internalFile.length() > 0) {
            return internalFile
        }

        // 2. Check osmdroid base path
        val osmdroidBase = org.osmdroid.config.Configuration.getInstance().osmdroidBasePath
        if (osmdroidBase != null && osmdroidBase.exists()) {
            val files = osmdroidBase.listFiles { _, name ->
                name.endsWith(".mbtiles", ignoreCase = true)
            }
            if (!files.isNullOrEmpty()) {
                // Return the largest MBTiles file (most likely to be the world map)
                return files.maxByOrNull { it.length() }
            }
        }

        // 3. Try to copy from APK assets (only if bundled)
        return try {
            copyMbtilesFromAssets(context)
        } catch (e: Exception) {
            Log.d(TAG, "No MBTiles in assets: ${e.message}")
            null
        }
    }

    /**
     * Copies the MBTiles file from the APK assets to internal storage.
     * This is a one-time operation on first launch.
     */
    private fun copyMbtilesFromAssets(context: Context): File? {
        val assetManager = context.assets
        // Check if the asset exists
        val assetList = assetManager.list("") ?: return null
        if (MBTILES_ASSET_NAME !in assetList) return null

        val outDir = File(context.filesDir, MBTILES_DIR)
        outDir.mkdirs()
        val outFile = File(outDir, MBTILES_ASSET_NAME)

        if (outFile.exists() && outFile.length() > 0) {
            return outFile
        }

        Log.i(TAG, "Copying MBTiles from assets to ${outFile.absolutePath}...")
        assetManager.open(MBTILES_ASSET_NAME).use { input ->
            FileOutputStream(outFile).use { output ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                }
            }
        }
        Log.i(TAG, "MBTiles copy complete: ${outFile.length() / 1024 / 1024} MB")
        return outFile
    }

    /**
     * Reads the max zoom level from the MBTiles metadata table.
     * Falls back to 8.0 if metadata is not available.
     */
    private fun getMaxZoomFromFile(file: File): Double {
        return try {
            val db = android.database.sqlite.SQLiteDatabase.openDatabase(
                file.absolutePath, null, android.database.sqlite.SQLiteDatabase.OPEN_READONLY
            )
            val cursor = db.rawQuery(
                "SELECT value FROM metadata WHERE name = 'maxzoom'", null
            )
            val maxZoom = if (cursor.moveToFirst()) {
                cursor.getString(0).toDoubleOrNull() ?: 8.0
            } else {
                8.0
            }
            cursor.close()
            db.close()
            maxZoom
        } catch (e: Exception) {
            Log.w(TAG, "Could not read maxzoom from MBTiles metadata", e)
            8.0
        }
    }

    /**
     * Checks if an offline MBTiles file is available.
     */
    fun isOfflineMapAvailable(context: Context): Boolean {
        return findMbtilesFile(context) != null
    }

    /**
     * Returns human-readable info about the offline map file.
     */
    fun getMapInfo(context: Context): MapInfo {
        val file = findMbtilesFile(context) ?: return MapInfo(
            available = false,
            fileSizeMB = 0,
            maxZoom = 0,
            source = "None"
        )

        return MapInfo(
            available = true,
            fileSizeMB = (file.length() / 1024 / 1024).toInt(),
            maxZoom = getMaxZoomFromFile(file).toInt(),
            source = file.absolutePath
        )
    }

    data class MapInfo(
        val available: Boolean,
        val fileSizeMB: Int,
        val maxZoom: Int,
        val source: String
    )
}
