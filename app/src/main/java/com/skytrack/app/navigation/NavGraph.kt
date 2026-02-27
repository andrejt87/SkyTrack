package com.skytrack.app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.*
import androidx.navigation.compose.*
import com.skytrack.app.ui.screens.dashboard.DashboardScreen
import com.skytrack.app.ui.screens.history.HistoryScreen
import com.skytrack.app.ui.screens.map.MapScreen
import com.skytrack.app.ui.screens.settings.SettingsScreen
import com.skytrack.app.ui.screens.setup.FlightSetupScreen
import com.skytrack.app.ui.screens.splash.SplashScreen
import com.skytrack.app.ui.screens.stats.StatsScreen
import com.skytrack.app.ui.airportpicker.AirportPickerScreen
import com.google.gson.Gson
import com.skytrack.app.data.model.Airport

sealed class Screen(val route: String) {
    object Splash   : Screen("splash")
    object Setup    : Screen("setup")
    object Dashboard: Screen("dashboard/{flightId}") {
        fun createRoute(flightId: Long) = "dashboard/$flightId"
    }
    object Map      : Screen("map/{flightId}") {
        fun createRoute(flightId: Long) = "map/$flightId"
    }
    object Stats    : Screen("stats/{flightId}") {
        fun createRoute(flightId: Long) = "stats/$flightId"
    }
    object History  : Screen("history")
    object Settings : Screen("settings")
    object AirportPicker : Screen("airport_picker/{type}") {
        fun createRoute(type: String) = "airport_picker/$type"
        const val TYPE_DEPARTURE = "departure"
        const val TYPE_ARRIVAL   = "arrival"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 } },
        exitTransition  = { fadeOut(tween(200)) },
        popEnterTransition  = { fadeIn(tween(300)) },
        popExitTransition   = { fadeOut(tween(200)) + slideOutHorizontally(tween(300)) { it / 4 } }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Setup.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onActiveFlight = { flightId ->
                    navController.navigate(Screen.Dashboard.createRoute(flightId)) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Setup.route) { backStackEntry ->
            // Observe airport picker results
            val savedStateHandle = backStackEntry.savedStateHandle
            val setupViewModel: com.skytrack.app.ui.screens.setup.FlightSetupViewModel = androidx.hilt.navigation.compose.hiltViewModel(backStackEntry)

            LaunchedEffect(Unit) {
                savedStateHandle.getStateFlow<String?>("selected_airport_json", null).collect { json ->
                    if (json != null) {
                        val airport = Gson().fromJson(json, Airport::class.java)
                        val type = savedStateHandle.get<String>("selected_airport_type")
                        if (type == Screen.AirportPicker.TYPE_DEPARTURE) {
                            setupViewModel.setDeparture(airport)
                        } else {
                            setupViewModel.setArrival(airport)
                        }
                        savedStateHandle["selected_airport_json"] = null
                    }
                }
            }

            FlightSetupScreen(
                viewModel = setupViewModel,
                onFlightStarted = { flightId ->
                    navController.navigate(Screen.Dashboard.createRoute(flightId)) {
                        popUpTo(Screen.Setup.route) { inclusive = false }
                    }
                },
                onSelectDeparture = {
                    navController.navigate(Screen.AirportPicker.createRoute(Screen.AirportPicker.TYPE_DEPARTURE))
                },
                onSelectArrival = {
                    navController.navigate(Screen.AirportPicker.createRoute(Screen.AirportPicker.TYPE_ARRIVAL))
                },
                onHistoryClick = {
                    navController.navigate(Screen.History.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Dashboard.route,
            arguments = listOf(navArgument("flightId") { type = NavType.LongType })
        ) { backStackEntry ->
            val flightId = backStackEntry.arguments?.getLong("flightId") ?: -1L
            DashboardScreen(
                flightId = flightId,
                onMapClick = { navController.navigate(Screen.Map.createRoute(flightId)) },
                onStatsClick = { navController.navigate(Screen.Stats.createRoute(flightId)) },
                onHistoryClick = { navController.navigate(Screen.History.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onFlightComplete = {
                    navController.navigate(Screen.Setup.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Map.route,
            arguments = listOf(navArgument("flightId") { type = NavType.LongType })
        ) { backStackEntry ->
            val flightId = backStackEntry.arguments?.getLong("flightId") ?: -1L
            MapScreen(
                flightId = flightId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Stats.route,
            arguments = listOf(navArgument("flightId") { type = NavType.LongType })
        ) { backStackEntry ->
            val flightId = backStackEntry.arguments?.getLong("flightId") ?: -1L
            StatsScreen(
                flightId = flightId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onFlightClick = { flightId ->
                    navController.navigate(Screen.Stats.createRoute(flightId))
                },
                onBack = { navController.popBackStack() },
                onNewFlight = {
                    navController.navigate(Screen.Setup.route) {
                        popUpTo(Screen.History.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AirportPicker.route,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: Screen.AirportPicker.TYPE_DEPARTURE
            AirportPickerScreen(
                pickerType = type,
                onAirportSelected = { airport ->
                    val json = Gson().toJson(airport)
                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_airport_json", json)
                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_airport_type", type)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
