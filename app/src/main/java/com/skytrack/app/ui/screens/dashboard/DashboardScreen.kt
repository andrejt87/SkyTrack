package com.skytrack.app.ui.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skytrack.app.ui.components.*
import com.skytrack.app.ui.theme.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    flightId: Long,
    onMapClick: () -> Unit,
    onStatsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFlightComplete: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val flight  = uiState.flight
    val progress = uiState.progress
    var showCompleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = DarkSurface) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Dashboard, null) },
                    label = { Text("Live") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Amber,
                        selectedTextColor = Amber,
                        indicatorColor = DarkSurface2
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onMapClick,
                    icon = { Icon(Icons.Default.Map, null) },
                    label = { Text("Map") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = DarkSurface2
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onStatsClick,
                    icon = { Icon(Icons.Default.BarChart, null) },
                    label = { Text("Stats") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = DarkSurface2
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onHistoryClick,
                    icon = { Icon(Icons.Default.History, null) },
                    label = { Text("History") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = DarkSurface2
                    )
                )
            }
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── Header: Route + Elapsed time ─────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(DeepBlue, DarkBackground)
                        )
                    )
                    .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    if (flight != null) {
                        Text(
                            text = flight.routeLabel,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 4.sp
                            )
                        )
                        if (flight.flightNumber.isNotBlank()) {
                            Text(
                                text = flight.flightNumber,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextSecondary,
                                    letterSpacing = 2.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatElapsed(uiState.elapsedMs),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextTertiary,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
                // Cancel / complete button
                IconButton(
                    onClick = { showCompleteDialog = true },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Stop, "End flight", tint = Error)
                }
            }

            // ─── Big Progress Hero ─────────────────────────────────────────────
            if (flight != null) {
                ProgressHero(
                    departureIata    = flight.departureIata,
                    arrivalIata      = flight.arrivalIata,
                    progressPercent  = progress.smoothedProgressPercent,
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
            }

            // ─── Inline map preview ────────────────────────────────────────────
            if (flight != null) {
                Card(
                    onClick = onMapClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = MaterialTheme.shapes.large
                ) {
                    FlightRouteOverlay(
                        departureLat = flight.departureLat,
                        departureLon = flight.departureLon,
                        arrivalLat   = flight.arrivalLat,
                        arrivalLon   = flight.arrivalLon,
                        currentLat   = progress.currentLat,
                        currentLon   = progress.currentLon,
                        showCurrentPosition = progress.currentLat != 0.0,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // ─── Metric Cards 2×2 Grid ────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    label    = "Speed",
                    value    = progress.groundSpeedKmh.roundToInt().toString(),
                    unit     = "km/h",
                    icon     = Icons.Default.Speed,
                    iconTint = SpeedGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label    = "Altitude",
                    value    = progress.altitudeFormatted,
                    icon     = Icons.Default.Landscape,
                    iconTint = AltitudeCyan,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    label    = "ETA",
                    value    = progress.etaFormatted ?: "–",
                    icon     = Icons.Default.Schedule,
                    iconTint = Amber,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label    = "Remaining",
                    value    = "${progress.remainingDistanceKm.roundToInt()}",
                    unit     = "km",
                    icon     = Icons.Default.Route,
                    iconTint = RouteBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            // ─── Additional row: Bearing + GPS accuracy ───────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    label    = "Bearing",
                    value    = "${progress.bearingDeg.roundToInt()}°",
                    icon     = Icons.Default.Explore,
                    iconTint = WarningLight,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label    = if (uiState.barometerAvailable) "Pressure" else "GPS Acc.",
                    value    = if (uiState.barometerAvailable)
                        "${"%.1f".format(progress.pressureHpa)} hPa"
                    else
                        "${progress.gpsAccuracyM.roundToInt()} m",
                    icon     = if (uiState.barometerAvailable) Icons.Default.Air else Icons.Default.GpsFixed,
                    iconTint = Info,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Complete flight dialog
    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            containerColor = DarkSurface,
            icon = { Icon(Icons.Default.FlightLand, null, tint = Amber) },
            title = {
                Text("End Flight?", color = TextPrimary)
            },
            text = {
                Text(
                    "Mark this flight as completed and stop tracking?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCompleteDialog = false
                        viewModel.completeFlight(onFlightComplete)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = TextOnAmber)
                ) {
                    Text("Complete Flight")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

private fun formatElapsed(ms: Long): String {
    if (ms <= 0) return "Just departed"
    val hours   = TimeUnit.MILLISECONDS.toHours(ms)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
    return if (hours > 0) "Airborne ${hours}h ${minutes}m" else "Airborne ${minutes}m"
}
