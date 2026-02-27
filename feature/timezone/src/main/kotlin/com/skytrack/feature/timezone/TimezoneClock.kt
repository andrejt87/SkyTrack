package com.skytrack.feature.timezone

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.skytrack.core.design.theme.SkyTrackColors
import com.skytrack.core.design.theme.SkyTrackSpacing
import com.skytrack.domain.model.TimezoneInfo
import java.time.format.DateTimeFormatter

@Composable
fun TimezoneClock(
    info: TimezoneInfo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = info.label,
            style = MaterialTheme.typography.labelMedium,
            color = SkyTrackColors.TextSecondary
        )
        Spacer(modifier = Modifier.height(SkyTrackSpacing.xs))
        Text(
            text = info.currentTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            style = MaterialTheme.typography.headlineMedium,
            color = SkyTrackColors.TextPrimary
        )
        Text(
            text = "UTC%+.1f".format(info.offsetHours),
            style = MaterialTheme.typography.labelSmall,
            color = SkyTrackColors.TextSecondary
        )
    }
}
