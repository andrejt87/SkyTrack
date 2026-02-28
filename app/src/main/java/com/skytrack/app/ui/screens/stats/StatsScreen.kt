package com.skytrack.app.ui.screens.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.skytrack.app.domain.AltitudeCalculator
import com.skytrack.app.ui.components.MetricCard
import com.skytrack.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@Composable
fun StatsScreen(
    flightId: Long,
    onBack: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val flight  = uiState.flight

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = flight?.routeLabel ?: "Flight Stats",
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        if (uiState.isLoading || flight == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Amber)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Route header
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = flight.routeLabel,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp
                        )
                    )
                    Text(
                        text = formatDate(flight.actualDepartureMs.takeIf { it > 0 } ?: flight.createdAtMs),
                        style = MaterialTheme.typography.bodySmall.copy(color = TextTertiary)
                    )
                }
            }

            // Section: Route
            SectionHeader("ROUTE")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    label    = "Total Distance",
                    value    = "${flight.totalDistanceKm.roundToInt()}",
                    unit     = "km",
                    icon     = Icons.Default.Route,
                    iconTint = RouteBlue,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label    = "Duration",
                    value    = formatDuration(flight.durationMs),
                    icon     = Icons.Default.Timer,
                    iconTint = Amber,
                    modifier = Modifier.weight(1f)
                )
            }

            // Section: Performance
            SectionHeader("PERFORMANCE")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    label    = "Max Speed",
                    value    = "${flight.maxSpeedKmh.roundToInt()}",
                    unit     = "km/h",
                    icon     = Icons.Default.Speed,
                    iconTint = SpeedGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label    = "Avg Speed",
                    value    = "${flight.avgSpeedKmh.roundToInt()}",
                    unit     = "km/h",
                    icon     = Icons.Default.TrendingUp,
                    iconTint = SuccessLight,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    label    = "Max Altitude",
                    value    = AltitudeCalculator.formatAltitude(flight.maxAltitudeM),
                    icon     = Icons.Default.Landscape,
                    iconTint = AltitudeCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label    = "Max Altitude (m)",
                    value    = "${flight.maxAltitudeM.roundToInt()}",
                    unit     = "m",
                    icon     = Icons.Default.Height,
                    iconTint = Info,
                    modifier = Modifier.weight(1f)
                )
            }

            // Section: Airports
            SectionHeader("AIRPORTS")
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AirportRow(
                        iata = flight.departureIata,
                        name = flight.departureName,
                        role = "Departure",
                        icon = Icons.Default.FlightTakeoff,
                        tint = SpeedGreen
                    )
                    Divider(color = DarkDivider)
                    AirportRow(
                        iata = flight.arrivalIata,
                        name = flight.arrivalName,
                        role = "Arrival",
                        icon = Icons.Default.FlightLand,
                        tint = RouteBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            color = TextSecondary,
            letterSpacing = 2.sp
        )
    )
}

@Composable
private fun AirportRow(
    iata: String,
    name: String,
    role: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: androidx.compose.ui.graphics.Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Column {
            Text(iata, style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
            Text(name, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            Text(role, style = MaterialTheme.typography.labelSmall.copy(color = TextTertiary))
        }
    }
}

private fun formatDate(ms: Long): String {
    if (ms == 0L) return "Unknown date"
    return SimpleDateFormat("EEE dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(ms))
}

private fun formatDuration(ms: Long): String {
    if (ms <= 0) return "–"
    val h = TimeUnit.MILLISECONDS.toHours(ms)
    val m = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}
