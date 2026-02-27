package com.skytrack.data.maps

import android.content.Context
import com.skytrack.domain.repository.MapTilesRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MBTiles-based offline map tile provider.
 * Reads vector tiles from SQLite-based MBTiles file.
 */
@Singleton
class MapTilesRepositoryImpl @Inject constructor(
    private val context: Context
) : MapTilesRepository {

    override suspend fun getTile(z: Int, x: Int, y: Int): ByteArray? {
        // MBTiles uses TMS y-coordinate (flipped)
        val tmsY = (1 shl z) - 1 - y
        return try {
            val db = android.database.sqlite.SQLiteDatabase.openDatabase(
                getMBTilesPath(), null, android.database.sqlite.SQLiteDatabase.OPEN_READONLY
            )
            val cursor = db.rawQuery(
                "SELECT tile_data FROM tiles WHERE zoom_level = ? AND tile_column = ? AND tile_row = ?",
                arrayOf(z.toString(), x.toString(), tmsY.toString())
            )
            val data = if (cursor.moveToFirst()) cursor.getBlob(0) else null
            cursor.close()
            db.close()
            data
        } catch (e: Exception) {
            null
        }
    }

    override fun getStyleJson(): String {
        return context.assets.open("map/style-dark.json")
            .bufferedReader().use { it.readText() }
    }

    override suspend fun isIntegrityValid(): Boolean {
        return try {
            val file = java.io.File(getMBTilesPath())
            file.exists() && file.length() > 0
        } catch (e: Exception) {
            false
        }
    }

    private fun getMBTilesPath(): String {
        return "${context.filesDir}/maps/world.mbtiles"
    }
}
