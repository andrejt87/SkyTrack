package com.skytrack.feature.overfly

import androidx.compose.foundation.layout.*
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

@Composable
fun OverflyInfoCard(
    modifier: Modifier = Modifier,
    viewModel: OverflyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SkyTrackColors.Surface)
    ) {
        Column(modifier = Modifier.padding(SkyTrackSpacing.md)) {
            Text(
                text = "Überflug",
                style = MaterialTheme.typography.labelMedium,
                color = SkyTrackColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(SkyTrackSpacing.xs))
            Text(
                text = if (uiState.isOverOcean) {
                    uiState.oceanName ?: "Ozean"
                } else {
                    uiState.countryName.ifEmpty { "---" }
                },
                style = MaterialTheme.typography.titleMedium,
                color = SkyTrackColors.TextPrimary
            )
            uiState.regionName?.let { region ->
                Text(
                    text = region,
                    style = MaterialTheme.typography.bodySmall,
                    color = SkyTrackColors.TextSecondary
                )
            }
        }
    }
}
