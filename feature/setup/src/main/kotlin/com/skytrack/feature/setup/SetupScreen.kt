package com.skytrack.feature.setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skytrack.core.design.theme.SkyTrackColors
import com.skytrack.core.design.theme.SkyTrackSpacing

@Composable
fun SetupScreen(
    onFlightStarted: () -> Unit = {},
    viewModel: SetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SkyTrackSpacing.md)
    ) {
        Text(
            text = "Flug einrichten",
            style = MaterialTheme.typography.headlineLarge,
            color = SkyTrackColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(SkyTrackSpacing.lg))

        // Departure input
        OutlinedTextField(
            value = uiState.departureQuery,
            onValueChange = viewModel::onDepartureQueryChanged,
            label = { Text("Abflughafen (IATA oder Name)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Departure suggestions
        if (uiState.departureSuggestions.isNotEmpty()) {
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                itemsIndexed(uiState.departureSuggestions) { index, airport ->
                    Text(
                        text = "${airport.iata} - ${airport.name} (${airport.city})",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onDepartureSelected(index) }
                            .padding(SkyTrackSpacing.sm),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Nearest airport hint
        uiState.nearestAirport?.let { nearest ->
            TextButton(onClick = viewModel::useNearestAsDeparture) {
                Text("Nächster Flughafen: ${nearest.iata} - ${nearest.name}")
            }
        }

        Spacer(modifier = Modifier.height(SkyTrackSpacing.md))

        // Arrival input
        OutlinedTextField(
            value = uiState.arrivalQuery,
            onValueChange = viewModel::onArrivalQueryChanged,
            label = { Text("Zielflughafen (IATA oder Name)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Arrival suggestions
        if (uiState.arrivalSuggestions.isNotEmpty()) {
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                itemsIndexed(uiState.arrivalSuggestions) { index, airport ->
                    Text(
                        text = "${airport.iata} - ${airport.name} (${airport.city})",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onArrivalSelected(index) }
                            .padding(SkyTrackSpacing.sm),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(SkyTrackSpacing.lg))

        // Error message
        uiState.error?.let { error ->
            Text(
                text = error,
                color = SkyTrackColors.Error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(SkyTrackSpacing.sm))
        }

        // Start button
        Button(
            onClick = { viewModel.startFlight(onFlightStarted) },
            enabled = uiState.isStartEnabled && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Flug starten")
            }
        }

        Spacer(modifier = Modifier.height(SkyTrackSpacing.lg))

        // Recent flights
        if (uiState.recentFlights.isNotEmpty()) {
            Text(
                text = "Letzte Flüge",
                style = MaterialTheme.typography.titleMedium,
                color = SkyTrackColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(SkyTrackSpacing.sm))
            uiState.recentFlights.forEach { flight ->
                Text(
                    text = "${flight.departure.iata} → ${flight.arrival.iata}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SkyTrackColors.TextPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SkyTrackSpacing.xs)
                )
            }
        }
    }
}
