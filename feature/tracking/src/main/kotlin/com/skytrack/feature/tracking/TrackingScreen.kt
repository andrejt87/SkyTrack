package com.skytrack.feature.tracking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skytrack.core.design.components.FlightDataCard
import com.skytrack.core.design.components.GpsStatusIndicator
import com.skytrack.core.design.components.ProgressArc
import com.skytrack.core.design.theme.SkyTrackColors
import com.skytrack.core.design.theme.SkyTrackSpacing
import com.skytrack.domain.model.TrackingState

@Composable
fun TrackingScreen(
    onNavigateToMap: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SkyTrackSpacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header: Route info + GPS status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${uiState.departure?.iata ?: "---"} → ${uiState.arrival?.iata ?: "---"}",
                style = MaterialTheme.typography.headlineMedium,
                color = SkyTrackColors.TextPrimary
            )
            GpsStatusIndicator(isActive = uiState.isGpsActive)
        }

        Spacer(modifier = Modifier.height(SkyTrackSpacing.xl))

        // Central progress arc
        ProgressArc(
            progress = uiState.progress,
            size = 280.dp
        )

        // Status message
        when (uiState.trackingState) {
            TrackingState.GPS_LOST -> Text(
                text = "GPS Signal verloren",
                color = SkyTrackColors.Error,
                style = MaterialTheme.typography.bodyMedium
            )
            TrackingState.ARRIVED -> Text(
                text = "Willkommen in ${uiState.arrival?.city ?: uiState.arrival?.iata}",
                color = SkyTrackColors.AccentSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
            TrackingState.ACQUIRING -> Text(
                text = "GPS wird initialisiert...",
                color = SkyTrackColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            else -> {}
        }

        Spacer(modifier = Modifier.height(SkyTrackSpacing.lg))

        // Flight data cards grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SkyTrackSpacing.sm)
        ) {
            FlightDataCard(
                label = "Geschwindigkeit",
                value = "%.0f".format(uiState.speed.kmh),
                unit = "km/h",
                modifier = Modifier.weight(1f)
            )
            FlightDataCard(
                label = "Höhe",
                value = "%.0f".format(uiState.altitude.meters),
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
                label = "ETA",
                value = uiState.eta,
                modifier = Modifier.weight(1f)
            )
            FlightDataCard(
                label = "Verbleibend",
                value = "%.0f".format(uiState.distanceRemaining.km),
                unit = "km",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SkyTrackSpacing.sm)
        ) {
            OutlinedButton(
                onClick = onNavigateToMap,
                modifier = Modifier.weight(1f)
            ) {
                Text("Karte")
            }
            OutlinedButton(
                onClick = onNavigateToDashboard,
                modifier = Modifier.weight(1f)
            ) {
                Text("Dashboard")
            }
        }
    }
}
