package com.skytrack.app.ui.screens.map

import android.graphics.Paint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skytrack.app.ui.components.SkyTrackTopBar
import com.skytrack.app.domain.FlightCalculator
import com.skytrack.app.ui.theme.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import com.skytrack.app.data.map.NetworkMonitor
import com.skytrack.app.data.map.OfflineTileProvider

@Composable
fun MapScreen(
    flightId: Long,
    gpsAccuracyM: Float = 0f,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val flight   = uiState.flight
    val progress = uiState.progress
    val trackPoints = uiState.trackPoints
    val isOnline by NetworkMonitor.isOnline.collectAsState()

    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var isFollowingPlane by remember { mutableStateOf(true) }
    var initialCentered by remember { mutableStateOf(false) }
    var lastCurrentLat by remember { mutableStateOf(Double.NaN) }
    var lastCurrentLon by remember { mutableStateOf(Double.NaN) }
    var lastTrackSize by remember { mutableIntStateOf(0) }
    var lastFlightId by remember { mutableStateOf(-2L) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Map", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Full-screen OSMDroid map
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).also { mv ->
                        mapViewRef = mv
                        OfflineTileProvider.configureMap(mv, ctx)
                        mv.setMultiTouchControls(true)
                        val rotGesture = RotationGestureOverlay(mv)
                        rotGesture.isEnabled = true
                        mv.overlays.add(rotGesture)
                        // Start centered on current position (no animation)
                        if (progress.currentLat != 0.0) {
                            mv.controller.setCenter(GeoPoint(progress.currentLat, progress.currentLon))
                            mv.controller.setZoom(12.0)
                            initialCentered = true
                        } else {
                            mv.controller.setZoom(5.0)
                        }
                    }
                },
                update = { mapView ->
                    OfflineTileProvider.configureMap(mapView, mapView.context)

                    // Only update when position, track data, or flight changed
                    val currentFlightId = flight?.id ?: -1L
                    if (progress.currentLat == lastCurrentLat &&
                        progress.currentLon == lastCurrentLon &&
                        trackPoints.size == lastTrackSize &&
                        currentFlightId == lastFlightId) return@AndroidView
                    lastCurrentLat = progress.currentLat
                    lastCurrentLon = progress.currentLon
                    lastTrackSize = trackPoints.size
                    lastFlightId = currentFlightId

                    mapView.overlays.clear()

                    // No flight — just show current position
                    if (flight == null) {
                        if (progress.currentLat != 0.0) {
                            val curPos = GeoPoint(progress.currentLat, progress.currentLon)
                            val curMarker = Marker(mapView).apply {
                                position = curPos
                                title = "Current Position"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            mapView.overlays.add(curMarker)
                            if (!initialCentered) {
                                mapView.controller.setCenter(curPos)
                                mapView.controller.setZoom(12.0)
                                initialCentered = true
                            }
                        }
                        mapView.invalidate()
                        return@AndroidView
                    }

                    // Route polyline (great circle)
                    val routePoints = FlightCalculator.greatCirclePoints(
                        flight.departureLat, flight.departureLon,
                        flight.arrivalLat,   flight.arrivalLon,
                        numPoints = 100
                    ).map { (lat, lon) -> GeoPoint(lat, lon) }

                    val routePolyline = Polyline(mapView).apply {
                        setPoints(routePoints)
                        outlinePaint.apply {
                            color = android.graphics.Color.parseColor("#58A6FF")
                            strokeWidth = 5f
                            isAntiAlias = true
                            pathEffect = android.graphics.DashPathEffect(floatArrayOf(30f, 15f), 0f)
                            alpha = 180
                        }
                    }
                    mapView.overlays.add(routePolyline)

                    // Actual track points path (red, solid)
                    if (trackPoints.size >= 2) {
                        val trackGeoPoints = trackPoints.map { GeoPoint(it.lat, it.lon) }
                        val trackPolyline = Polyline(mapView).apply {
                            setPoints(trackGeoPoints)
                            outlinePaint.apply {
                                color = android.graphics.Color.parseColor("#FF3B30")
                                strokeWidth = 4f
                                isAntiAlias = true
                                alpha = 230
                            }
                        }
                        mapView.overlays.add(trackPolyline)

                        // Waypoint markers (1km minimum distance between each)
                        var lastWpLat = trackPoints.first().lat
                        var lastWpLon = trackPoints.first().lon
                        for (i in trackPoints.indices) {
                            val tp = trackPoints[i]
                            val dist = if (i == 0) Double.MAX_VALUE
                                else FlightCalculator.haversineDistance(lastWpLat, lastWpLon, tp.lat, tp.lon)
                            if (dist >= 1.0) {
                                val wpMarker = Marker(mapView).apply {
                                    position = GeoPoint(tp.lat, tp.lon)
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                    title = "WP${i + 1} · ${tp.altitudeM.toInt()}m · ${tp.speedKmh.toInt()} km/h"
                                    icon = mapView.context.getDrawable(android.R.drawable.presence_online)
                                }
                                mapView.overlays.add(wpMarker)
                                lastWpLat = tp.lat
                                lastWpLon = tp.lon
                            }
                        }
                    }

                    // Departure marker (hide when current position is nearby to avoid double pin)
                    val depNearCurrent = progress.currentLat != 0.0 &&
                        FlightCalculator.haversineDistance(
                            flight.departureLat, flight.departureLon,
                            progress.currentLat, progress.currentLon
                        ) < 5.0 // less than 5 km apart
                    if (!depNearCurrent && flight.hasDeparture) {
                        val depMarker = Marker(mapView).apply {
                            position = GeoPoint(flight.departureLat, flight.departureLon)
                            title    = "🛫 ${flight.departureIata}"
                            snippet  = flight.departureName
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        mapView.overlays.add(depMarker)
                    }

                    // Arrival marker (destination airport) — always visible
                    val arrMarker = Marker(mapView).apply {
                        position = GeoPoint(flight.arrivalLat, flight.arrivalLon)
                        title    = "🛬 ${flight.arrivalIata}"
                        snippet  = flight.arrivalName
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    mapView.overlays.add(arrMarker)

                    // Current position marker
                    if (progress.currentLat != 0.0) {
                        val curMarker = Marker(mapView).apply {
                            position = GeoPoint(progress.currentLat, progress.currentLon)
                            title    = "✈ ${progress.speedFormatted} · ${progress.altitudeFormatted}"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        mapView.overlays.add(curMarker)

                        if (isFollowingPlane) {
                            mapView.controller.animateTo(GeoPoint(progress.currentLat, progress.currentLon))
                        }
                    } else {
                        // No GPS yet: center on midpoint
                        val mid = routePoints.getOrNull(routePoints.size / 2)
                        if (mid != null) mapView.controller.setCenter(mid)
                        val distKm = FlightCalculator.haversineDistance(
                            flight.departureLat, flight.departureLon,
                            flight.arrivalLat,   flight.arrivalLon
                        )
                        mapView.controller.setZoom(when {
                            distKm > 8000 -> 3.0
                            distKm > 4000 -> 4.0
                            distKm > 2000 -> 5.0
                            else -> 6.0
                        })
                    }
                    mapView.invalidate()
                }
            )

            // My Location button
            FloatingActionButton(
                onClick = {
                    isFollowingPlane = true
                    if (progress.currentLat != 0.0) {
                        mapViewRef?.controller?.animateTo(
                            GeoPoint(progress.currentLat, progress.currentLon)
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = DarkSurface,
                contentColor = if (isFollowingPlane) Amber else TextSecondary
            ) {
                Icon(Icons.Default.MyLocation, "Center")
            }

            // Speed/altitude overlay card
            if (progress.currentLat != 0.0) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.9f)),
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Column {
                            Text("SPEED", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            Text(progress.speedFormatted, style = MaterialTheme.typography.titleMedium.copy(color = SpeedGreen))
                        }
                        Column {
                            Text("ALTITUDE", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            Text(progress.altitudeFormatted, style = MaterialTheme.typography.titleMedium.copy(color = AltitudeCyan))
                        }
                        Column {
                            Text("ETA", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            Text(progress.etaFormatted ?: "–", style = MaterialTheme.typography.titleMedium.copy(color = Amber))
                        }
                    }
                }
            }
        }
    }
}
