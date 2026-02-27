package com.skytrack.app.ui.airportpicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skytrack.app.data.model.Airport
import com.skytrack.app.navigation.Screen
import com.skytrack.app.ui.components.AirportSearchTextField
import com.skytrack.app.ui.theme.*

@Composable
fun AirportPickerScreen(
    pickerType: String,
    onAirportSelected: (Airport) -> Unit,
    onBack: () -> Unit,
    viewModel: AirportPickerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDeparture = pickerType == Screen.AirportPicker.TYPE_DEPARTURE
    val title = if (isDeparture) "Departure Airport" else "Arrival Airport"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search field
            AirportSearchTextField(
                query         = uiState.query,
                onQueryChange = viewModel::onQueryChange,
                onSearch      = { },
                modifier      = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Loading
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Amber, modifier = Modifier.size(24.dp))
                }
            }

            // Empty state hint
            if (!uiState.isLoading && uiState.query.length < 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Type at least 2 characters to search\n(IATA code, city, or airport name)",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextTertiary),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Results
            LazyColumn(contentPadding = PaddingValues(vertical = 4.dp)) {
                items(uiState.results, key = { it.iata }) { airport ->
                    AirportResultRow(
                        airport = airport,
                        onClick = {
                            viewModel.selectAirport(airport)
                            onAirportSelected(airport)
                        }
                    )
                }
                if (!uiState.isLoading && uiState.query.length >= 2 && uiState.results.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.SearchOff, null, tint = TextTertiary, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "No airports found for \"${uiState.query}\"",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextTertiary)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AirportResultRow(
    airport: Airport,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // IATA badge
        Surface(
            color = DeepBlue,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.size(width = 52.dp, height = 36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = airport.iata,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Amber,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = airport.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1
            )
            Text(
                text = buildString {
                    append(airport.city)
                    if (airport.country.isNotBlank()) append(", ${airport.country}")
                    if (airport.icao.isNotBlank()) append("  ·  ICAO: ${airport.icao}")
                },
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                maxLines = 1
            )
        }

        Icon(Icons.Default.ChevronRight, null, tint = TextTertiary, modifier = Modifier.size(16.dp))
    }
    Divider(color = DarkDivider.copy(alpha = 0.5f), modifier = Modifier.padding(start = 84.dp))
}
