package com.skytrack.feature.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skytrack.core.design.components.OfflineBadge
import com.skytrack.core.design.theme.SkyTrackColors
import com.skytrack.core.design.theme.SkyTrackSpacing

@Composable
fun MapScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyTrackColors.Background)
    ) {
        // MapLibre MapView would go here — requires native View integration
        // Placeholder for MapLibre GL integration
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "MapLibre Karte\n${uiState.departureIata} → ${uiState.arrivalIata}",
                style = MaterialTheme.typography.bodyLarge,
                color = SkyTrackColors.TextSecondary
            )
        }

        // Offline badge
        if (uiState.isOfflineBadgeVisible) {
            OfflineBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(SkyTrackSpacing.md)
            )
        }

        // Current position info
        uiState.currentPosition?.let { pos ->
            Text(
                text = "%.4f°, %.4f°".format(pos.latitude, pos.longitude),
                style = MaterialTheme.typography.labelSmall,
                color = SkyTrackColors.TextSecondary,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(SkyTrackSpacing.md)
            )
        }
    }
}
