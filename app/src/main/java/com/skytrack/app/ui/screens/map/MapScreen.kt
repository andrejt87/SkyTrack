package com.skytrack.app.ui.screens.map

import android.graphics.Paint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skytrack.app.domain.FlightCalculator
import com.skytrack.app.ui.theme.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import com.skytrack.app.data.map.OfflineTileProvider

@Composable
fun MapScreen(
    flightId: Long,
    onBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val flight   = uiState.flight
    val progress = uiState.progress

    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var isFollowingPlane by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = flight?.routeLabel ?: "Map",
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = TextSecondary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isFollowingPlane = true
                        if (progress.currentLat != 0.0) {
                            mapViewRef?.controller?.animateTo(
                                GeoPoint(progress.currentLat, progress.currentLon)
                            )
                        }
                    }) {
                        Icon(Icons.Default.MyLocation, "Center", tint = if (isFollowingPlane) Amber else TextSecondary)
                    }
                },
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
                        // Configure offline MBTiles provider; falls back gracefully
                        val offlineAvailable = OfflineTileProvider.configureOfflineMap(mv, ctx)
                        if (!offlineAvailable) {
                            // Fallback: online tiles (cached when available)
                            mv.setTileSource(TileSourceFactory.MAPNIK)
                            mv.setUseDataConnection(true)
                        }
                        mv.setMultiTouchControls(true)
                        val rotGesture = RotationGestureOverlay(mv)
                        rotGesture.isEnabled = true
                        mv.overlays.add(rotGesture)
                        mv.controller.setZoom(5.0)
                    }
                },
                update = { mapView ->
                    if (flight == null) return@AndroidView
                    mapView.overlays.clear()

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

                    // Covered path (solid)
                    if (progress.currentLat != 0.0) {
                        val coveredPoints = FlightCalculator.greatCirclePoints(
                            flight.departureLat, flight.departureLon,
                            progress.currentLat, progress.currentLon,
                            numPoints = 50
                        ).map { (lat, lon) -> GeoPoint(lat, lon) }

                        val coveredPolyline = Polyline(mapView).apply {
                            setPoints(coveredPoints)
                            outlinePaint.apply {
                                color = android.graphics.Color.parseColor("#FFA000")
                                strokeWidth = 4f
                                isAntiAlias = true
                                alpha = 220
                            }
                        }
                        mapView.overlays.add(coveredPolyline)
                    }

                    // Departure marker
                    val depMarker = Marker(mapView).apply {
                        position = GeoPoint(flight.departureLat, flight.departureLon)
                        title    = "${flight.departureIata} – ${flight.departureName}"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    mapView.overlays.add(depMarker)

                    // Arrival marker
                    val arrMarker = Marker(mapView).apply {
                        position = GeoPoint(flight.arrivalLat, flight.arrivalLon)
                        title    = "${flight.arrivalIata} – ${flight.arrivalName}"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    mapView.overlays.add(arrMarker)

                    // Current position marker
                    if (progress.currentLat != 0.0) {
                        val curMarker = Marker(mapView).apply {
                            position = GeoPoint(progress.currentLat, progress.currentLon)
                            title    = "✈ ${progress.speedFormatted} · ${progress.altitudeFormatted}"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
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
