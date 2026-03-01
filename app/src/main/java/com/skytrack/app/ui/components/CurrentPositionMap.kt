package com.skytrack.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.skytrack.app.data.map.NetworkMonitor
import com.skytrack.app.data.map.OfflineTileProvider
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun CurrentPositionMap(
    lat: Double,
    lon: Double,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val isOnline by NetworkMonitor.isOnline.collectAsState()
    val hasPosition = lat != 0.0 || lon != 0.0
    var initialCentered by remember { mutableStateOf(false) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                OfflineTileProvider.configureMap(this, context)
                setMultiTouchControls(false)
                isClickable = false
                isFocusable = false
                if (hasPosition) {
                    controller.setCenter(GeoPoint(lat, lon))
                    controller.setZoom(13.0)
                    initialCentered = true
                } else {
                    controller.setCenter(GeoPoint(50.0, 10.0))
                    controller.setZoom(3.0)
                }
            }
        },
        update = { mapView ->
            OfflineTileProvider.configureMap(mapView, ctx)
            mapView.overlays.clear()

            if (hasPosition) {
                val pos = GeoPoint(lat, lon)
                val marker = Marker(mapView).apply {
                    position = pos
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    title = "Current Position"
                }
                mapView.overlays.add(marker)
                // Only center once (first GPS fix), then let user pan freely
                if (!initialCentered) {
                    mapView.controller.setCenter(pos)
                    mapView.controller.setZoom(13.0)
                    initialCentered = true
                }
            }
            mapView.invalidate()
        }
    )
}
