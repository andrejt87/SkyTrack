package com.skytrack.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skytrack.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkyTrackTopBar(
    gpsAccuracyM: Float,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "SKYTRACK",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    )
                )
                GpsSignalIndicator(accuracyM = gpsAccuracyM)
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
    )
}
