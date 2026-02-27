package com.skytrack.core.design.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skytrack.core.design.theme.SkyTrackColors
import com.skytrack.core.design.theme.SkyTrackSpacing

@Composable
fun FlightDataCard(
    label: String,
    value: String,
    unit: String = "",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SkyTrackColors.Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(SkyTrackSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = SkyTrackColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(SkyTrackSpacing.xs))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    color = SkyTrackColors.TextPrimary
                )
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(SkyTrackSpacing.xs))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.labelMedium,
                        color = SkyTrackColors.TextSecondary
                    )
                }
            }
        }
    }
}
