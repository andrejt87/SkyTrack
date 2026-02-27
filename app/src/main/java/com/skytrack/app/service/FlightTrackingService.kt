package com.skytrack.app.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.skytrack.app.MainActivity
import com.skytrack.app.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.*
import com.skytrack.app.data.repository.FlightRepository
import com.skytrack.app.data.repository.LocationRepository

/**
 * Foreground service that keeps GPS tracking alive even when the app is in background.
 * Started when a flight is active, stopped when flight is completed.
 */
@AndroidEntryPoint
class FlightTrackingService : Service() {

    @Inject lateinit var locationRepository: LocationRepository
    @Inject lateinit var flightRepository: FlightRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        const val CHANNEL_ID     = "flight_tracking_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_STOP    = "ACTION_STOP_TRACKING"
        const val EXTRA_FLIGHT_ID = "flight_id"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        val flightId = intent?.getLongExtra(EXTRA_FLIGHT_ID, -1L) ?: -1L
        val notification = buildNotification("Tracking flight…")
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        locationRepository.stopTracking()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Flight Tracking",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Active flight position tracking"
            setShowBadge(false)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(text: String): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this, 0,
            Intent(this, FlightTrackingService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("SkyTrack")
            .setContentText(text)
            .setContentIntent(openIntent)
            .addAction(android.R.drawable.ic_media_pause, "Stop", stopIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
