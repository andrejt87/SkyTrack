package com.skytrack.app.data.map

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Foreground service that downloads the offline MBTiles file.
 * Survives app backgrounding / screen off.
 */
class MapDownloadService : Service() {

    companion object {
        private const val TAG = "MapDownloadService"
        private const val CHANNEL_ID = "map_download"
        private const val NOTIFICATION_ID = 42
        private const val MBTILES_DIR = "mbtiles"
        private const val MBTILES_FILENAME = "skytrack-world.mbtiles"
        private const val DOWNLOAD_URL = "http://100.126.24.17:8888/skytrack-world.mbtiles"

        fun start(context: Context) {
            val intent = Intent(context, MapDownloadService::class.java)
            context.startForegroundService(intent)
        }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (MapDownloadManager.state.value.isDownloading) {
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID, buildNotification("Preparing download…", 0))

        scope.launch {
            try {
                MapDownloadManager._state.value = MapDownloadManager.DownloadState(isDownloading = true)
                download()
            } catch (e: Exception) {
                Log.e(TAG, "Download failed", e)
                MapDownloadManager._state.value = MapDownloadManager.DownloadState(error = e.message ?: "Download failed")
            } finally {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun download() {
        val outDir = File(filesDir, MBTILES_DIR)
        outDir.mkdirs()
        val outFile = File(outDir, MBTILES_FILENAME)
        val tmpFile = File(outDir, "$MBTILES_FILENAME.tmp")

        val url = URL(DOWNLOAD_URL)
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 15_000
        conn.readTimeout = 30_000
        conn.setRequestProperty("User-Agent", "SkyTrack/1.0")
        conn.instanceFollowRedirects = true

        val responseCode = conn.responseCode
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("HTTP $responseCode")
        }

        val totalBytes = conn.contentLengthLong
        val totalMB = (totalBytes / 1024 / 1024).toInt()

        conn.inputStream.use { input ->
            FileOutputStream(tmpFile).use { output ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalRead = 0L

                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    totalRead += bytesRead

                    val progress = if (totalBytes > 0) totalRead.toFloat() / totalBytes else 0f
                    val dlMB = (totalRead / 1024 / 1024).toInt()

                    MapDownloadManager._state.value = MapDownloadManager.DownloadState(
                        isDownloading = true,
                        progress = progress,
                        downloadedMB = dlMB,
                        totalMB = totalMB
                    )

                    // Update notification every 5MB
                    if (dlMB % 5 == 0) {
                        updateNotification("Downloading… $dlMB / $totalMB MB", (progress * 100).toInt())
                    }
                }
            }
        }
        conn.disconnect()

        if (outFile.exists()) outFile.delete()
        tmpFile.renameTo(outFile)

        Log.i(TAG, "Download complete: ${outFile.length() / 1024 / 1024} MB")
        MapDownloadManager._state.value = MapDownloadManager.DownloadState(isComplete = true)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, "Map Downloads",
            NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Offline map tile downloads" }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(text: String, progress: Int): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SkyTrack")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setProgress(100, progress, progress == 0)
            .build()
    }

    private fun updateNotification(text: String, progress: Int) {
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, buildNotification(text, progress))
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
