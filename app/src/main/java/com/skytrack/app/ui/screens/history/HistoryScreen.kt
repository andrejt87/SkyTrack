package com.skytrack.app.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skytrack.app.data.model.Flight
import com.skytrack.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@Composable
fun HistoryScreen(
    onFlightClick: (Long) -> Unit,
    onBack: () -> Unit,
    onNewFlight: () -> Unit = {},
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var flightToDelete by remember { mutableStateOf<Flight?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flight History", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewFlight,
                containerColor = Amber
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Flight", tint = DarkBackground)
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) {
                CircularProgressIndicator(color = Amber)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary banner
            if (uiState.totalFlights > 0) {
                item {
                    SummaryBanner(
                        totalFlights    = uiState.totalFlights,
                        totalDistanceKm = uiState.totalDistanceKm,
                        totalTimeMs     = uiState.totalFlightTimeMs
                    )
                }
            }

            if (uiState.flights.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.AirplanemodeInactive,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                "No completed flights yet",
                                style = MaterialTheme.typography.bodyLarge.copy(color = TextTertiary)
                            )
                        }
                    }
                }
            } else {
                items(uiState.flights, key = { it.id }) { flight ->
                    FlightHistoryCard(
                        flight = flight,
                        onClick = { onFlightClick(flight.id) },
                        onDelete = { flightToDelete = flight }
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    flightToDelete?.let { flight ->
        AlertDialog(
            onDismissRequest = { flightToDelete = null },
            containerColor = DarkSurface,
            icon = { Icon(Icons.Default.Delete, null, tint = Error) },
            title = { Text("Delete Flight?", color = TextPrimary) },
            text = { Text("Remove ${flight.routeLabel} from history?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteFlight(flight)
                        flightToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { flightToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun SummaryBanner(
    totalFlights: Int,
    totalDistanceKm: Double,
    totalTimeMs: Long
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface2),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryStat("FLIGHTS", totalFlights.toString(), Icons.Default.Flight, Amber)
            SummaryStat(
                "TOTAL KM",
                "${(totalDistanceKm / 1000).roundToInt()}k",
                Icons.Default.Route,
                RouteBlue
            )
            SummaryStat(
                "HOURS",
                "${TimeUnit.MILLISECONDS.toHours(totalTimeMs)}h",
                Icons.Default.Timer,
                SpeedGreen
            )
        }
    }
}

@Composable
private fun SummaryStat(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
        Text(value, style = MaterialTheme.typography.titleLarge.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
        Text(label, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, letterSpacing = 1.sp))
    }
}

@Composable
private fun FlightHistoryCard(
    flight: Flight,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Flight, null, tint = Amber, modifier = Modifier.size(24.dp))
                Column {
                    Text(
                        flight.routeLabel,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary, fontWeight = FontWeight.Bold
                        )
                    )
                    if (flight.flightNumber.isNotBlank()) {
                        Text(flight.flightNumber, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    }
                    Text(
                        buildString {
                            if (flight.totalDistanceKm > 0) append("${flight.totalDistanceKm.roundToInt()} km  ·  ")
                            append(formatDate(flight.actualDepartureMs.takeIf { it > 0 } ?: flight.createdAtMs))
                        },
                        style = MaterialTheme.typography.bodySmall.copy(color = TextTertiary)
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete", tint = TextTertiary, modifier = Modifier.size(18.dp))
            }
        }
    }
}

private fun formatDate(ms: Long): String {
    if (ms == 0L) return ""
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(ms))
}
