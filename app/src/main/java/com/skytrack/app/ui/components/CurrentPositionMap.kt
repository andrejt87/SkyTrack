package com.skytrack.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.skytrack.app.data.map.OfflineTileProvider
import com.skytrack.app.ui.theme.TextTertiary
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

@Composable
fun CurrentPositionMap(
    lat: Double,
    lon: Double,
    modifier: Modifier = Modifier
) {
    if (lat == 0.0 && lon == 0.0) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Icon(Icons.Default.MyLocation, "Waiting for GPS", tint = TextTertiary)
        }
        return
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            MapView(ctx).apply {
                val offlineAvailable = OfflineTileProvider.configureOfflineMap(this, ctx)
                if (!offlineAvailable) {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setUseDataConnection(true)
                }
                setMultiTouchControls(false)
                isClickable = false
                isFocusable = false
            }
        },
        update = { mapView ->
            mapView.overlays.clear()
            val pos = GeoPoint(lat, lon)
            val marker = Marker(mapView).apply {
                position = pos
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                title = "Current Position"
            }
            mapView.overlays.add(marker)
            mapView.controller.setCenter(pos)
            mapView.controller.setZoom(13.0)
            mapView.invalidate()
        }
    )
}
