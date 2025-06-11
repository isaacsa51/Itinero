package com.serranoie.app.itinero.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.chat.ChatScreen
import com.serranoie.app.feature.expenses.navigation.expensesGraph
import com.serranoie.app.feature.home.HomeScreen
import com.serranoie.app.feature.home.HomeViewModel
import com.serranoie.app.feature.home.HomeUiState
import com.serranoie.app.feature.home.navigation.bottombar.BottomBarNav
import com.serranoie.app.feature.itinerary.navigation.itineraryGraph
import com.serranoie.app.feature.settings.trip.TripInfoSettingsScreen
import com.serranoie.app.feature.settings.trip.TripSettingsScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun HomeRootScreen(
    tripId: String,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val homeViewModel = koinViewModel<HomeViewModel>(parameters = { parametersOf(tripId) })
    val tripInfo by homeViewModel.trip.collectAsState()
    val uiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        homeViewModel.getCurrentTravel()
    }

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf(
                    Route.Home.route,
                    Route.Itinerary.route,
                    Route.Expenses.route,
                )
            ) {
                BottomBarNav(navController, tripId)
            }
        }) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Route.Home.route) {
                val viewmodel = koinViewModel<HomeViewModel>(parameters = { parametersOf(tripId) })
                val uiState by viewmodel.uiState.collectAsState()
                val tripInfo by viewmodel.trip.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                HomeScreen(
                    navController,
                    tripId = tripId,
                    uiState = uiState,
                    tripInfo = tripInfo,
                    onGetTravel = { viewmodel.getCurrentTravel() },
                    onShowSnackbar = { message ->
                        snackbarHostState.showSnackbar(message)
                    },
                    onRefresh = { viewmodel.refreshTrip() })
            }

            itineraryGraph(navController, tripId)

            expensesGraph(navController, tripId)

            composable(Route.Chat.route) {
                ChatScreen(navController, tripId)
            }

            composable(
                route = "trip_settings/{tripId}?scrollTo={scrollTo}",
                arguments = listOf(
                    navArgument("tripId") { type = NavType.StringType },
                    navArgument("scrollTo") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    })
            ) { backStackEntry ->
                val routeTripId = backStackEntry.arguments?.getString("tripId") ?: ""
                val scrollTo = backStackEntry.arguments?.getString("scrollTo")
                TripSettingsScreen(
                    navController = navController,
                    tripId = routeTripId,
                    scrollTo = scrollTo,
                    trip = tripInfo
                )
            }

            composable(
                route = "trip_info/{tripId}", arguments = listOf(
                    navArgument("tripId") { type = NavType.StringType })
            ) { backStackEntry ->
                val routeTripId = backStackEntry.arguments?.getString("tripId") ?: ""
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(uiState) {
                    when (val currentState = uiState) {
                        is HomeUiState.Success -> {
                            snackbarHostState.showSnackbar("Trip information updated successfully")
                            navController.popBackStack()
                        }

                        is HomeUiState.Error -> {
                            snackbarHostState.showSnackbar(currentState.message)
                        }

                        else -> {}
                    }
                }

                TripInfoSettingsScreen(
                    navController = navController,
                    tripId = routeTripId,
                    trip = tripInfo,
                    onUpdateTripInfo = { tripId, updateTrip ->
                        homeViewModel.updateTripInfo(tripId, updateTrip)
                    }
                )
            }
        }
    }
}
