package com.skytrack.app.ui.components

import android.view.MotionEvent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.Marker
import com.skytrack.app.domain.FlightCalculator
import com.skytrack.app.data.map.NetworkMonitor
import com.skytrack.app.data.map.OfflineTileProvider
import com.skytrack.app.ui.theme.*

@Composable
fun FlightRouteOverlay(
    departureLat: Double,
    departureLon: Double,
    arrivalLat: Double,
    arrivalLon: Double,
    currentLat: Double = 0.0,
    currentLon: Double = 0.0,
    showCurrentPosition: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isOnline by NetworkMonitor.isOnline.collectAsState()
    var lastCurrentLat by remember { mutableStateOf(Double.NaN) }
    var lastCurrentLon by remember { mutableStateOf(Double.NaN) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var initialCentered by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    mapViewRef = this
                    OfflineTileProvider.configureMap(this, ctx)
                    setMultiTouchControls(true)
                    setScrollableAreaLimitLatitude(
                        MapView.getTileSystem().maxLatitude,
                        MapView.getTileSystem().minLatitude,
                        0
                    )
                    setOnTouchListener { v, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> v.parent?.requestDisallowInterceptTouchEvent(true)
                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> v.parent?.requestDisallowInterceptTouchEvent(false)
                        }
                        false
                    }
                }
            },
            update = { mapView ->
                OfflineTileProvider.configureMap(mapView, context)

                if (currentLat == lastCurrentLat && currentLon == lastCurrentLon) return@AndroidView
                lastCurrentLat = currentLat
                lastCurrentLon = currentLon

                mapView.overlays.removeAll { it is Polyline || it is Marker }

                val departure = GeoPoint(departureLat, departureLon)
                val arrival = GeoPoint(arrivalLat, arrivalLon)
                val hasDeparture = departureLat != 0.0 || departureLon != 0.0

                val routePoints = FlightCalculator.greatCirclePoints(
                    departureLat, departureLon,
                    arrivalLat, arrivalLon,
                    numPoints = 80
                ).map { (lat, lon) -> GeoPoint(lat, lon) }

                if (hasDeparture) {
                    val polyline = Polyline(mapView).apply {
                        setPoints(routePoints)
                        outlinePaint.apply {
                            color = android.graphics.Color.parseColor("#58A6FF")
                            strokeWidth = 4f
                            isAntiAlias = true
                            pathEffect = android.graphics.DashPathEffect(floatArrayOf(20f, 10f), 0f)
                        }
                    }
                    mapView.overlays.add(polyline)

                    val departureMarker = Marker(mapView).apply {
                        position = departure
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Departure"
                    }
                    mapView.overlays.add(departureMarker)
                }

                val arrivalMarker = Marker(mapView).apply {
                    position = arrival
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Arrival"
                }
                mapView.overlays.add(arrivalMarker)

                val depIsNearCurrent = hasDeparture && Math.abs(departureLat - currentLat) < 0.01 && Math.abs(departureLon - currentLon) < 0.01
                if (showCurrentPosition && currentLat != 0.0 && currentLon != 0.0 && !depIsNearCurrent) {
                    val currentPos = GeoPoint(currentLat, currentLon)
                    val currentMarker = Marker(mapView).apply {
                        position = currentPos
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        title = "Current Position"
                    }
                    mapView.overlays.add(currentMarker)
                }

                // Only center on first load
                if (!initialCentered && routePoints.isNotEmpty()) {
                    centerOnRoute(mapView, routePoints, departureLat, departureLon, arrivalLat, arrivalLon)
                    initialCentered = true
                }

                mapView.invalidate()
            }
        )

        // Re-center button
        SmallFloatingActionButton(
            onClick = {
                mapViewRef?.let { mapView ->
                    val routePoints = FlightCalculator.greatCirclePoints(
                        departureLat, departureLon,
                        arrivalLat, arrivalLon,
                        numPoints = 80
                    ).map { (lat, lon) -> GeoPoint(lat, lon) }
                    centerOnRoute(mapView, routePoints, departureLat, departureLon, arrivalLat, arrivalLon)
                    mapView.invalidate()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .size(32.dp),
            containerColor = DarkSurface.copy(alpha = 0.85f),
            contentColor = DarkOnSurface,
            shape = CircleShape
        ) {
            Icon(Icons.Default.CenterFocusStrong, contentDescription = "Center route", modifier = Modifier.size(16.dp))
        }
    }
}

private fun centerOnRoute(
    mapView: MapView,
    routePoints: List<GeoPoint>,
    departureLat: Double, departureLon: Double,
    arrivalLat: Double, arrivalLon: Double
) {
    if (routePoints.isNotEmpty()) {
        mapView.controller.setCenter(routePoints[routePoints.size / 2])
    }
    val distKm = FlightCalculator.haversineDistance(departureLat, departureLon, arrivalLat, arrivalLon)
    val zoom = when {
        distKm > 8000 -> 3
        distKm > 4000 -> 4
        distKm > 2000 -> 5
        distKm > 1000 -> 6
        else -> 7
    }
    mapView.controller.setZoom(zoom.toDouble())
}
