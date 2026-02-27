package com.skytrack.app.ui.screens.setup

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skytrack.app.ui.components.AirportSearchField
import com.skytrack.app.ui.theme.*

@Composable
fun FlightSetupScreen(
    onFlightStarted: (Long) -> Unit,
    onSelectDeparture: () -> Unit,
    onSelectArrival: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: FlightSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Request location permission on first composition
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        viewModel.onLocationPermissionResult(granted)
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Flight",
                        style = MaterialTheme.typography.titleLarge.copy(color = TextPrimary)
                    )
                },
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(Icons.Default.History, "History", tint = TextSecondary)
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, "Settings", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Text(
                text = "Where are you flying?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )

            // Departure
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "DEPARTURE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        letterSpacing = 2.sp
                    )
                )
                AirportSearchField(
                    selectedAirport = uiState.departure,
                    placeholder = "Search departure airport",
                    icon = Icons.Default.FlightTakeoff,
                    onClick = onSelectDeparture,
                    onClear = viewModel::clearDeparture,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Swap button row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = viewModel::swapAirports,
                    enabled = uiState.departure != null || uiState.arrival != null
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap airports",
                        tint = if (uiState.departure != null || uiState.arrival != null) Amber else TextTertiary
                    )
                }
            }

            // Arrival
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ARRIVAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        letterSpacing = 2.sp
                    )
                )
                AirportSearchField(
                    selectedAirport = uiState.arrival,
                    placeholder = "Search arrival airport",
                    icon = Icons.Default.FlightLand,
                    onClick = onSelectArrival,
                    onClear = viewModel::clearArrival,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Route distance indicator
            if (uiState.routeDistanceKm > 0) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ROUTE DISTANCE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    letterSpacing = 1.5.sp
                                )
                            )
                            Text(
                                text = "${uiState.routeDistanceKm.toInt()} km",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = RouteBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Icon(
                            Icons.Default.Route,
                            contentDescription = null,
                            tint = RouteBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Flight number (optional)
            OutlinedTextField(
                value = uiState.flightNumber,
                onValueChange = viewModel::setFlightNumber,
                label = {
                    Text("Flight Number (optional)", color = TextTertiary)
                },
                placeholder = {
                    Text("e.g. LH401", color = TextTertiary)
                },
                leadingIcon = {
                    Icon(Icons.Default.ConfirmationNumber, null, tint = TextSecondary)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Amber,
                    unfocusedBorderColor = DarkDivider,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = Amber,
                    focusedContainerColor = DarkSurface2,
                    unfocusedContainerColor = DarkSurface2
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Start button
            Button(
                onClick = { viewModel.startFlight(onFlightStarted) },
                enabled = uiState.canStart && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Amber,
                    contentColor = TextOnAmber,
                    disabledContainerColor = DarkSurface3,
                    disabledContentColor = TextTertiary
                ),
                shape = MaterialTheme.shapes.large
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = DarkBackground,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(Icons.Default.FlightTakeoff, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "START TRACKING",
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
