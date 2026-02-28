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
import androidx.compose.foundation.BorderStroke
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
    onAddDeparture: () -> Unit = {},
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

            // ─── "Where are you flying from?" banner if no departure ──────────
            if (flight != null && !flight.hasDeparture) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Where are you flying from?",
                            style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onAddDeparture,
                            border = BorderStroke(1.dp, Amber)
                        ) {
                            Icon(Icons.Default.FlightTakeoff, null, tint = Amber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Departure (optional)", color = Amber)
                        }
                    }
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

    // End flight dialog with 3 options
    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            containerColor = DarkSurface,
            icon = { Icon(Icons.Default.FlightLand, null, tint = Amber) },
            title = {
                Text("End Flight?", color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Complete flight
                    Button(
                        onClick = {
                            showCompleteDialog = false
                            viewModel.completeFlight(onFlightComplete)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = TextOnAmber)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Complete Flight")
                    }
                    // Abort flight (keep in history as incomplete)
                    OutlinedButton(
                        onClick = {
                            showCompleteDialog = false
                            viewModel.cancelFlight()
                            onFlightComplete()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningLight),
                        border = BorderStroke(1.dp, WarningLight)
                    ) {
                        Icon(Icons.Default.Cancel, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Abort Flight")
                    }
                    // Delete flight entirely
                    OutlinedButton(
                        onClick = {
                            showCompleteDialog = false
                            viewModel.deleteFlight()
                            onFlightComplete()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                        border = BorderStroke(1.dp, Error)
                    ) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete Flight")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCompleteDialog = false }) {
                    Text("Back", color = TextSecondary)
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
