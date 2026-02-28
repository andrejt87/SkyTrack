package com.skytrack.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skytrack.app.data.map.MapDownloadManager
import com.skytrack.app.data.map.NetworkMonitor
import com.skytrack.app.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Shows a download banner when:
 * - Internet is available AND detail offline maps are not yet downloaded
 * - Or a download is in progress / errored
 *
 * Hidden when:
 * - Detail maps already exist on disk
 * - No internet (user sees base contours, can't download anyway)
 */
@Composable
fun MapDownloadBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val downloadState by MapDownloadManager.state.collectAsState()

    val detailAvailable = MapDownloadManager.isMapAvailable(context)
    val isOnline by NetworkMonitor.isOnline.collectAsState()

    // Debug: show state as Toast on first compose and changes
    androidx.compose.runtime.LaunchedEffect(isOnline, detailAvailable, downloadState.isDownloading) {
        android.widget.Toast.makeText(
            context,
            "BANNER: online=$isOnline detail=$detailAvailable dl=${downloadState.isDownloading}",
            android.widget.Toast.LENGTH_LONG
        ).show()
    }

    // Hide if detail maps exist or if offline (can't download anyway)
    if (detailAvailable) return
    if (!isOnline) {
        // Reset stuck download state when going offline
        if (downloadState.isDownloading) {
            MapDownloadManager.resetState()
        }
        return
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface2),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    imageVector = when {
                        downloadState.isDownloading -> Icons.Default.CloudDownload
                        downloadState.error != null -> Icons.Default.ErrorOutline
                        else -> Icons.Default.Map
                    },
                    contentDescription = null,
                    tint = if (downloadState.error != null) Error else Amber,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = when {
                        downloadState.isDownloading -> "Downloading offline maps…"
                        downloadState.error != null -> "Download failed"
                        else -> "Download detailed offline maps"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary, fontWeight = FontWeight.Medium
                    )
                )
            }

            when {
                downloadState.isDownloading -> {
                    LinearProgressIndicator(
                        progress = { downloadState.progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = Amber, trackColor = DarkSurface3
                    )
                    Text(
                        text = "${downloadState.downloadedMB} / ${downloadState.totalMB} MB",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextTertiary)
                    )
                }
                downloadState.error != null -> {
                    Text(downloadState.error ?: "", style = MaterialTheme.typography.labelSmall.copy(color = Error))
                    Button(
                        onClick = { scope.launch { MapDownloadManager.downloadMap(context) } },
                        colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = TextOnAmber),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) { Text("Retry", style = MaterialTheme.typography.labelMedium) }
                }
                else -> {
                    Text(
                        "~320 MB · Enables offline maps up to zoom level 8",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                    Button(
                        onClick = { scope.launch { MapDownloadManager.downloadMap(context) } },
                        colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = TextOnAmber),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.CloudDownload, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Download", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}
