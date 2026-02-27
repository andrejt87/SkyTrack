package com.skytrack.core.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.skytrack.core.design.theme.SkyTrackColors

@Composable
fun GpsStatusIndicator(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = if (isActive) SkyTrackColors.GpsActive else SkyTrackColors.GpsLost,
        animationSpec = tween(300),
        label = "gps_color"
    )

    val pulseAlpha by rememberInfiniteTransition(label = "gps_pulse").animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gps_alpha"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .alpha(pulseAlpha)
                .background(color, CircleShape)
        )
        Text(
            text = if (isActive) "GPS" else "GPS LOST",
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
