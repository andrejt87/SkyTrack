package com.skytrack.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.skytrack.core.design.components.FlightDataCard
import com.skytrack.core.design.theme.SkyTrackColors
import com.skytrack.core.design.theme.SkyTrackSpacing

@Composable
fun DashboardScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(SkyTrackSpacing.md)
    ) {
        Text(
            text = "Flight Dashboard",
            style = MaterialTheme.typography.headlineLarge,
            color = SkyTrackColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(SkyTrackSpacing.lg))

        // Current stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SkyTrackSpacing.sm)
        ) {
            FlightDataCard(
                label = "Höhe",
                value = "%.0f".format(uiState.currentAltitude.meters),
                unit = "m",
                modifier = Modifier.weight(1f)
            )
            FlightDataCard(
                label = "Max Höhe",
                value = "%.0f".format(uiState.maxAltitude.meters),
                unit = "m",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(SkyTrackSpacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SkyTrackSpacing.sm)
        ) {
            FlightDataCard(
                label = "Geschwindigkeit",
                value = "%.0f".format(uiState.currentSpeed.kmh),
                unit = "km/h",
                modifier = Modifier.weight(1f)
            )
            FlightDataCard(
                label = "Max Speed",
                value = "%.0f".format(uiState.maxSpeed.kmh),
                unit = "km/h",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(SkyTrackSpacing.lg))

        // Altitude profile placeholder
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(SkyTrackSpacing.xxxl * 3),
            colors = CardDefaults.cardColors(containerColor = SkyTrackColors.Surface)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(SkyTrackSpacing.md)) {
                Text(
                    text = "Höhenprofil (${uiState.altitudeHistory.size} Punkte)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SkyTrackColors.TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(SkyTrackSpacing.lg))

        // Flight statistics
        if (uiState.stats.totalFlights > 0) {
            Text(
                text = "Statistiken",
                style = MaterialTheme.typography.titleMedium,
                color = SkyTrackColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(SkyTrackSpacing.sm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SkyTrackSpacing.sm)
            ) {
                FlightDataCard(
                    label = "Flüge",
                    value = "${uiState.stats.totalFlights}",
                    modifier = Modifier.weight(1f)
                )
                FlightDataCard(
                    label = "Gesamtstrecke",
                    value = "%.0f".format(uiState.stats.totalDistanceKm),
                    unit = "km",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
