package com.skytrack.app.ui.components

import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import com.skytrack.app.domain.FlightCalculator
import com.skytrack.app.data.map.NetworkMonitor
import com.skytrack.app.data.map.OfflineTileProvider

/**
 * An inline read-only map overlay showing the flight route.
 * Used as an embedded preview inside DashboardScreen.
 */
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

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            MapView(ctx).apply {
                OfflineTileProvider.configureMap(this, ctx)
                setMultiTouchControls(false)
                isClickable = false
                isFocusable = false
                setScrollableAreaLimitLatitude(
                    MapView.getTileSystem().maxLatitude,
                    MapView.getTileSystem().minLatitude,
                    0
                )
                // Disable all gestures for inline preview
                val mRotateGestureOverlay = org.osmdroid.views.overlay.gestures.RotationGestureOverlay(this)
                mRotateGestureOverlay.isEnabled = false
            }
        },
        update = { mapView ->
            // Reconfigure tile source on connectivity change
            OfflineTileProvider.configureMap(mapView, context)
            mapView.overlays.clear()

            val departure = GeoPoint(departureLat, departureLon)
            val arrival = GeoPoint(arrivalLat, arrivalLon)

            // Check if departure coordinates are valid
            val hasDeparture = departureLat != 0.0 || departureLon != 0.0

            // Great-circle route polyline
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
            }

            // Departure marker (skip if at 0,0 = no GPS fix)
            if (hasDeparture) {
                val departureMarker = Marker(mapView).apply {
                    position = departure
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Departure"
                }
                mapView.overlays.add(departureMarker)
            }

            // Arrival marker
            val arrivalMarker = Marker(mapView).apply {
                position = arrival
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "Arrival"
            }
            mapView.overlays.add(arrivalMarker)

            // Current position marker
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

            // Zoom to show the route
            val mapController = mapView.controller
            if (routePoints.isNotEmpty()) {
                val midIndex = routePoints.size / 2
                mapController.setCenter(routePoints[midIndex])
            }
            // Compute zoom level based on distance
            val distKm = FlightCalculator.haversineDistance(
                departureLat, departureLon, arrivalLat, arrivalLon
            )
            val zoom = when {
                distKm > 8000 -> 3
                distKm > 4000 -> 4
                distKm > 2000 -> 5
                distKm > 1000 -> 6
                else -> 7
            }
            mapController.setZoom(zoom.toDouble())
            mapView.invalidate()
        }
    )
}
