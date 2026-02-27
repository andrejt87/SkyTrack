package com.skytrack.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skytrack.core.design.theme.SkyTrackColors

@Composable
fun OfflineBadge(modifier: Modifier = Modifier) {
    Text(
        text = "OFFLINE",
        style = MaterialTheme.typography.labelSmall,
        color = SkyTrackColors.TextSecondary,
        modifier = modifier
            .background(
                SkyTrackColors.SurfaceVariant,
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}
