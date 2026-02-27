package com.skytrack.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skytrack.core.navigation.SkyTrackDestination
import com.skytrack.feature.dashboard.DashboardScreen
import com.skytrack.feature.logbook.LogbookScreen
import com.skytrack.feature.map.MapScreen
import com.skytrack.feature.setup.SetupScreen
import com.skytrack.feature.tracking.TrackingScreen

@Composable
fun SkyTrackNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = SkyTrackDestination.Setup.route
    ) {
        composable(SkyTrackDestination.Setup.route) {
            SetupScreen(
                onFlightStarted = {
                    navController.navigate(SkyTrackDestination.Tracking.route) {
                        popUpTo(SkyTrackDestination.Setup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(SkyTrackDestination.Tracking.route) {
            TrackingScreen(
                onNavigateToMap = {
                    navController.navigate(SkyTrackDestination.Map.route)
                },
                onNavigateToDashboard = {
                    navController.navigate(SkyTrackDestination.Dashboard.route)
                }
            )
        }

        composable(SkyTrackDestination.Map.route) {
            MapScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(SkyTrackDestination.Dashboard.route) {
            DashboardScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(SkyTrackDestination.Logbook.route) {
            LogbookScreen(
                onFlightClick = { flightId ->
                    navController.navigate(SkyTrackDestination.FlightDetail.createRoute(flightId))
                }
            )
        }
    }
}
