package com.skytrack.app.ui.screens.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
fun HomeScreen(
    onMapClick: (Long) -> Unit,
    onStatsClick: (Long) -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSelectDeparture: () -> Unit,
    onSelectArrival: () -> Unit,
    onAddDepartureToFlight: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCompleteDialog by remember { mutableStateOf(false) }

    // Request location permission
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = DarkSurface) {
                val flightId = uiState.activeFlight?.id
                NavigationBarItem(
                    selected = true, onClick = { },
                    icon = { Icon(Icons.Default.Dashboard, null) },
                    label = { Text("Live") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Amber, selectedTextColor = Amber, indicatorColor = DarkSurface2)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onMapClick(flightId ?: -1L) },
                    icon = { Icon(Icons.Default.Map, null) },
                    label = { Text("Map") },
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary, indicatorColor = DarkSurface2)
                )
                NavigationBarItem(
                    selected = false, onClick = onHistoryClick,
                    icon = { Icon(Icons.Default.History, null) },
                    label = { Text("History") },
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary, indicatorColor = DarkSurface2)
                )
                NavigationBarItem(
                    selected = false, onClick = onSettingsClick,
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Settings") },
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary, indicatorColor = DarkSurface2)
                )
            }
        },
        topBar = {},
        containerColor = DarkBackground
    ) { paddingValues ->
        val flight = uiState.activeFlight
        val progress = uiState.progress
        val hasData = uiState.hasActiveFlight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── Fixed-height top section (header + setup/progress) ─────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Brush.verticalGradient(listOf(DeepBlue, DarkBackground)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // ─── Header row (always same height) ──────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (hasData) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(flight!!.routeLabel, style = MaterialTheme.typography.headlineSmall.copy(color = TextPrimary, fontWeight = FontWeight.Bold, letterSpacing = 3.sp))
                                    GpsSignalIndicator(accuracyM = progress.gpsAccuracyM)
                                }
                                Text(formatElapsed(uiState.elapsedMs), style = MaterialTheme.typography.bodySmall.copy(color = TextTertiary, letterSpacing = 1.sp))
                            }
                        } else {
                            Text("SKYTRACK", style = MaterialTheme.typography.headlineMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold, letterSpacing = 4.sp))
                        }
                        if (hasData) {
                            Button(
                                onClick = { showCompleteDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Error, contentColor = TextPrimary),
                                shape = MaterialTheme.shapes.medium,
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Stop, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("STOP", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
                            }
                        } else {
                            GpsSignalIndicator(accuracyM = progress.gpsAccuracyM)
                        }
                    }

                    // ─── Central slot (setup OR progress) ─────────────────────
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        if (hasData) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (!flight!!.hasDeparture) {
                                    OutlinedButton(
                                        onClick = onAddDepartureToFlight,
                                        border = BorderStroke(1.dp, TextTertiary),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Add, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Departure", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                ProgressHero(
                                    departureIata = flight.departureIata, arrivalIata = flight.arrivalIata,
                                    progressPercent = progress.smoothedProgressPercent,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                AirportSearchField(
                                    selectedAirport = uiState.arrival,
                                    placeholder = "Select destination",
                                    icon = Icons.Default.FlightLand,
                                    onClick = onSelectArrival,
                                    onClear = viewModel::clearArrival,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (uiState.departure != null) {
                                    AirportSearchField(
                                        selectedAirport = uiState.departure,
                                        placeholder = "Departure",
                                        icon = Icons.Default.FlightTakeoff,
                                        onClick = onSelectDeparture,
                                        onClear = viewModel::clearDeparture,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    OutlinedButton(
                                        onClick = onSelectDeparture,
                                        border = BorderStroke(1.dp, TextTertiary),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Add, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Departure", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                Button(
                                    onClick = viewModel::startFlight,
                                    enabled = uiState.canStart && !uiState.isLoading,
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = TextOnAmber, disabledContainerColor = DarkSurface3, disabledContentColor = TextTertiary),
                                    shape = MaterialTheme.shapes.large
                                ) {
                                    if (uiState.isLoading) {
                                        CircularProgressIndicator(color = DarkBackground, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                                    } else {
                                        Icon(Icons.Default.FlightTakeoff, null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("START TRACKING", style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ─── Map preview (always visible) ────────────────────────────────
            Card(
                onClick = { onMapClick(flight?.id ?: -1L) },
                modifier = Modifier.fillMaxWidth().height(180.dp).padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = MaterialTheme.shapes.large
            ) {
                if (hasData) {
                    FlightRouteOverlay(
                        departureLat = flight!!.departureLat, departureLon = flight.departureLon,
                        arrivalLat = flight.arrivalLat, arrivalLon = flight.arrivalLon,
                        currentLat = progress.currentLat, currentLon = progress.currentLon,
                        showCurrentPosition = progress.currentLat != 0.0,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    CurrentPositionMap(
                        lat = progress.currentLat,
                        lon = progress.currentLon,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // ─── Offline map download banner ──────────────────────────────────
            MapDownloadBanner(modifier = Modifier.padding(horizontal = 20.dp))

            // ─── Metrics (always visible) ─────────────────────────────────────
            val hasGps = progress.currentLat != 0.0

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(label = "Speed", value = if (hasGps) progress.speedFormatted else "–", icon = Icons.Default.Speed, iconTint = SpeedGreen, modifier = Modifier.weight(1f))
                MetricCard(label = "Altitude", value = if (hasGps) progress.altitudeFormatted else "–", icon = Icons.Default.Landscape, iconTint = AltitudeCyan, modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(label = "ETA", value = if (hasData) (progress.etaFormatted ?: "–") else "–", icon = Icons.Default.Schedule, iconTint = Amber, modifier = Modifier.weight(1f))
                MetricCard(label = "Remaining", value = if (hasData) "${progress.remainingDistanceKm.roundToInt()}" else "–", unit = if (hasData) "km" else "", icon = Icons.Default.Route, iconTint = RouteBlue, modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(label = "Bearing", value = if (hasData) "${progress.bearingDeg.roundToInt()}°" else "–", icon = Icons.Default.Explore, iconTint = WarningLight, modifier = Modifier.weight(1f))
                MetricCard(
                    label = if (uiState.barometerAvailable) "Pressure" else "GPS Acc.",
                    value = if (hasGps && uiState.barometerAvailable) "${"%.1f".format(progress.pressureHpa)} hPa"
                            else if (hasGps) "${progress.gpsAccuracyM.roundToInt()} m" else "–",
                    icon = if (uiState.barometerAvailable) Icons.Default.Air else Icons.Default.GpsFixed,
                    iconTint = Info, modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // End flight dialog
    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            containerColor = DarkSurface,
            icon = { Icon(Icons.Default.FlightLand, null, tint = Amber) },
            title = { Text("End Flight?", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { showCompleteDialog = false; viewModel.completeFlight() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = TextOnAmber)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Complete Flight")
                    }
                    OutlinedButton(
                        onClick = { showCompleteDialog = false; viewModel.cancelFlight() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningLight),
                        border = BorderStroke(1.dp, WarningLight)
                    ) {
                        Icon(Icons.Default.Cancel, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Abort Flight")
                    }
                    OutlinedButton(
                        onClick = { showCompleteDialog = false; viewModel.deleteFlight() },
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
                TextButton(onClick = { showCompleteDialog = false }) { Text("Back", color = TextSecondary) }
            }
        )
    }
}

private fun formatElapsed(ms: Long): String {
    if (ms <= 0) return "Just departed"
    val hours = TimeUnit.MILLISECONDS.toHours(ms)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
    return if (hours > 0) "Airborne ${hours}h ${minutes}m" else "Airborne ${minutes}m"
}
