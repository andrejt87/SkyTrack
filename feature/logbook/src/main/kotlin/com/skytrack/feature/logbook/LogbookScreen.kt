package com.skytrack.feature.logbook

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skytrack.core.design.theme.SkyTrackColors
import com.skytrack.core.design.theme.SkyTrackSpacing
import com.skytrack.domain.model.Flight
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun LogbookScreen(
    onFlightClick: (String) -> Unit = {},
    viewModel: LogbookViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SkyTrackSpacing.md)
    ) {
        Text(
            text = "Flugbuch",
            style = MaterialTheme.typography.headlineLarge,
            color = SkyTrackColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(SkyTrackSpacing.md))

        // Stats summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SkyTrackSpacing.sm)
        ) {
            StatChip("${uiState.stats.totalFlights} Flüge", Modifier.weight(1f))
            StatChip("%.0f km".format(uiState.stats.totalDistanceKm), Modifier.weight(1f))
            StatChip("%.1f h".format(uiState.stats.totalDurationHours), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(SkyTrackSpacing.lg))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.flights.isEmpty()) {
            Text(
                text = "Noch keine Flüge aufgezeichnet",
                style = MaterialTheme.typography.bodyMedium,
                color = SkyTrackColors.TextSecondary
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(SkyTrackSpacing.sm)) {
                items(uiState.flights) { flight ->
                    FlightListItem(
                        flight = flight,
                        onClick = { onFlightClick(flight.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FlightListItem(flight: Flight, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SkyTrackColors.Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkyTrackSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${flight.departure.iata} → ${flight.arrival.iata}",
                    style = MaterialTheme.typography.titleMedium,
                    color = SkyTrackColors.TextPrimary
                )
                Text(
                    text = flight.createdAt.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    style = MaterialTheme.typography.bodySmall,
                    color = SkyTrackColors.TextSecondary
                )
            }
            Text(
                text = "%.0f km".format(flight.config.totalDistanceKm),
                style = MaterialTheme.typography.bodyMedium,
                color = SkyTrackColors.TextSecondary
            )
        }
    }
}

@Composable
private fun StatChip(text: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = SkyTrackColors.SurfaceVariant)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = SkyTrackColors.TextPrimary,
            modifier = Modifier.padding(SkyTrackSpacing.sm)
        )
    }
}
