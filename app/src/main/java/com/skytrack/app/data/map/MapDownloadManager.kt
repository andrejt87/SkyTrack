package com.skytrack.app.data.map

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Manages downloading and storing the offline MBTiles map file.
 * On first launch (or if the file is missing), downloads from GitHub Releases.
 */
object MapDownloadManager {

    private const val TAG = "MapDownloadManager"
    private const val MBTILES_DIR = "mbtiles"
    private const val MBTILES_FILENAME = "skytrack-world.mbtiles"

    // Served from Mac mini via Tailscale (fallback: GitHub Releases when public)
    private const val DOWNLOAD_URL =
        "http://100.126.24.17:8888/skytrack-world.mbtiles"

    data class DownloadState(
        val isDownloading: Boolean = false,
        val progress: Float = 0f, // 0..1
        val downloadedMB: Int = 0,
        val totalMB: Int = 0,
        val error: String? = null,
        val isComplete: Boolean = false
    )

    internal val _state = MutableStateFlow(DownloadState())
    val state: StateFlow<DownloadState> = _state.asStateFlow()

    // Application-scoped — survives activity/composable lifecycle
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Returns the MBTiles file path if it exists and is valid.
     */
    fun getMbtilesFile(context: Context): File? {
        val file = File(File(context.filesDir, MBTILES_DIR), MBTILES_FILENAME)
        return if (file.exists() && file.length() > 1024) file else null
    }

    /**
     * Returns true if offline maps are already downloaded.
     */
    fun isMapAvailable(context: Context): Boolean = getMbtilesFile(context) != null

    /**
     * Starts the download in an app-scoped coroutine that survives backgrounding.
     */
    fun startDownload(context: Context) {
        scope.launch { downloadMap(context.applicationContext) }
    }

    /**
     * Downloads the MBTiles file. Runs in background scope.
     */
    private suspend fun downloadMap(context: Context) {
        if (_state.value.isDownloading) return

        _state.value = DownloadState(isDownloading = true)

        withContext(Dispatchers.IO) {
            try {
                val outDir = File(context.filesDir, MBTILES_DIR)
                outDir.mkdirs()
                val outFile = File(outDir, MBTILES_FILENAME)
                val tmpFile = File(outDir, "$MBTILES_FILENAME.tmp")

                val url = URL(DOWNLOAD_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 15_000
                conn.readTimeout = 30_000
                conn.setRequestProperty("User-Agent", "SkyTrack/1.0")
                conn.instanceFollowRedirects = true

                // Handle GitHub redirects
                val responseCode = conn.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("HTTP $responseCode")
                }

                val totalBytes = conn.contentLengthLong
                val totalMB = (totalBytes / 1024 / 1024).toInt()

                Log.i(TAG, "Downloading $totalMB MB from $DOWNLOAD_URL")

                conn.inputStream.use { input ->
                    FileOutputStream(tmpFile).use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var totalRead = 0L

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalRead += bytesRead

                            val progress = if (totalBytes > 0) totalRead.toFloat() / totalBytes else 0f
                            _state.value = DownloadState(
                                isDownloading = true,
                                progress = progress,
                                downloadedMB = (totalRead / 1024 / 1024).toInt(),
                                totalMB = totalMB
                            )
                        }
                    }
                }
                conn.disconnect()

                // Rename tmp to final
                if (outFile.exists()) outFile.delete()
                tmpFile.renameTo(outFile)

                Log.i(TAG, "Download complete: ${outFile.length() / 1024 / 1024} MB")
                _state.value = DownloadState(isComplete = true)

            } catch (e: Exception) {
                Log.e(TAG, "Download failed", e)
                _state.value = DownloadState(error = e.message ?: "Download failed")
            }
        }
    }

    /**
     * Resets the download state (e.g. when going offline with a stuck download).
     */
    fun resetState() {
        _state.value = DownloadState()
    }

    /**
     * Deletes the downloaded map file.
     */
    fun deleteMap(context: Context) {
        val file = File(File(context.filesDir, MBTILES_DIR), MBTILES_FILENAME)
        if (file.exists()) file.delete()
        val tmp = File(File(context.filesDir, MBTILES_DIR), "$MBTILES_FILENAME.tmp")
        if (tmp.exists()) tmp.delete()
        _state.value = DownloadState()
    }
}
