package com.skytrack.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skytrack.app.ui.theme.*

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    gpsAccuracyM: Float = 0f,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Units & Display
            SettingsSectionHeader("UNITS & DISPLAY")

            SettingsToggleRow(
                title    = "Metric Units",
                subtitle = if (uiState.useMetricUnits) "Speed in km/h, altitude in metres" else "Speed in knots, altitude in feet",
                icon     = Icons.Default.Straighten,
                checked  = uiState.useMetricUnits,
                onToggle = viewModel::toggleMetricUnits
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tracking
            SettingsSectionHeader("TRACKING")

            // Tracking interval slider
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Timer, null, tint = Amber, modifier = Modifier.size(20.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("GPS Update Interval", style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary))
                            Text("${uiState.trackingIntervalSec} seconds", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        }
                    }
                    Slider(
                        value = uiState.trackingIntervalSec.toFloat(),
                        onValueChange = { viewModel.setTrackingInterval(it.toInt()) },
                        valueRange = 2f..30f,
                        steps = 27,
                        colors = SliderDefaults.colors(
                            thumbColor = Amber,
                            activeTrackColor = Amber,
                            inactiveTrackColor = ProgressTrack
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("2s", style = MaterialTheme.typography.labelSmall.copy(color = TextTertiary))
                        Text("30s", style = MaterialTheme.typography.labelSmall.copy(color = TextTertiary))
                    }
                    Text(
                        text = "Lower values = smoother tracking & more detail, but higher battery usage. Higher values = longer battery life, but less precise route recording.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextTertiary),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App info
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                        Column {
                            Text("SkyTrack", style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary))
                            Text("Offline Flight Tracker", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                        }
                    }
                    Text(
                        "v${uiState.appVersion}",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextTertiary)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            color = TextSecondary,
            letterSpacing = 2.sp
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface2),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, null, tint = if (checked) Amber else TextSecondary, modifier = Modifier.size(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary, fontWeight = FontWeight.Medium))
                Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            }
            Switch(
                checked = checked,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = DarkBackground,
                    checkedTrackColor = Amber,
                    uncheckedThumbColor = TextTertiary,
                    uncheckedTrackColor = DarkSurface3
                )
            )
        }
    }
}
