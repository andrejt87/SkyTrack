package com.skytrack.app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.*
import androidx.navigation.compose.*
import com.skytrack.app.ui.screens.home.HomeScreen
import com.skytrack.app.ui.screens.history.HistoryScreen
import com.skytrack.app.ui.screens.map.MapScreen
import com.skytrack.app.ui.screens.settings.SettingsScreen
import com.skytrack.app.ui.screens.splash.SplashScreen
import com.skytrack.app.ui.screens.stats.StatsScreen
import com.skytrack.app.ui.airportpicker.AirportPickerScreen
import com.google.gson.Gson
import com.skytrack.app.data.model.Airport

sealed class Screen(val route: String) {
    object Splash   : Screen("splash")
    object Home     : Screen("home")
    object Setup    : Screen("setup") // kept for compat, redirects to Home
    object Dashboard: Screen("dashboard/{flightId}") {
        fun createRoute(flightId: Long) = "dashboard/$flightId"
    }
    object Map      : Screen("map/{flightId}") {
        fun createRoute(flightId: Long = -1L) = "map/$flightId"
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
        const val TYPE_DEPARTURE_ACTIVE = "departure_active"
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
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onActiveFlight = { _ ->
                    // Always go to Home — it auto-detects active flight
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) { backStackEntry ->
            val homeViewModel: com.skytrack.app.ui.screens.home.HomeViewModel =
                androidx.hilt.navigation.compose.hiltViewModel(backStackEntry)

            // Listen for airport picker results
            LaunchedEffect(Unit) {
                backStackEntry.savedStateHandle.getStateFlow<String?>("selected_airport_json", null).collect { json ->
                    if (json != null) {
                        val airport = Gson().fromJson(json, Airport::class.java)
                        val type = backStackEntry.savedStateHandle.get<String>("selected_airport_type")
                        when (type) {
                            Screen.AirportPicker.TYPE_DEPARTURE -> homeViewModel.setDeparture(airport)
                            Screen.AirportPicker.TYPE_ARRIVAL -> homeViewModel.setArrival(airport)
                            Screen.AirportPicker.TYPE_DEPARTURE_ACTIVE -> homeViewModel.setDepartureOnActiveFlight(airport)
                        }
                        backStackEntry.savedStateHandle["selected_airport_json"] = null
                    }
                }
            }

            HomeScreen(
                viewModel = homeViewModel,
                onMapClick = { flightId -> navController.navigate(Screen.Map.createRoute(flightId)) },
                onStatsClick = { flightId -> navController.navigate(Screen.Stats.createRoute(flightId)) },
                onHistoryClick = { navController.navigate(Screen.History.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onSelectDeparture = {
                    navController.navigate(Screen.AirportPicker.createRoute(Screen.AirportPicker.TYPE_DEPARTURE))
                },
                onSelectArrival = {
                    navController.navigate(Screen.AirportPicker.createRoute(Screen.AirportPicker.TYPE_ARRIVAL))
                },
                onAddDepartureToFlight = {
                    navController.navigate(Screen.AirportPicker.createRoute(Screen.AirportPicker.TYPE_DEPARTURE_ACTIVE))
                }
            )
        }

        composable(
            route = Screen.Map.route,
            arguments = listOf(navArgument("flightId") { type = NavType.LongType })
        ) { backStackEntry ->
            val flightId = backStackEntry.arguments?.getLong("flightId") ?: -1L
            MapScreen(flightId = flightId, onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.Stats.route,
            arguments = listOf(navArgument("flightId") { type = NavType.LongType })
        ) { backStackEntry ->
            val flightId = backStackEntry.arguments?.getLong("flightId") ?: -1L
            StatsScreen(flightId = flightId, onBack = { navController.popBackStack() })
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onFlightClick = { flightId -> navController.navigate(Screen.Stats.createRoute(flightId)) },
                onBack = { navController.popBackStack() },
                onNewFlight = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
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
