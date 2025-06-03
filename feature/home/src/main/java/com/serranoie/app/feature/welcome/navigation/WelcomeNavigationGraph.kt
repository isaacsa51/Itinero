package com.serranoie.app.feature.welcome.navigation

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.serranoie.app.core.navigation.NavigationGraph
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.TravelUiState
import com.serranoie.app.feature.TravelViewModel
import com.serranoie.app.feature.welcome.CreateTravelScreen
import com.serranoie.app.feature.welcome.JoinTripScreen
import com.serranoie.app.feature.welcome.TravelListScreen
import com.serranoie.app.feature.welcome.WelcomeScreen
import com.serranoie.app.feature.welcome.camera.CameraScannerScreen
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

class WelcomeNavigationGraph : NavigationGraph {
    override fun NavGraphBuilder.build(navController: NavHostController) {
        navigation(
            route = Route.WelcomeNavigation.route,
            startDestination = Route.Welcome.route
        ) {
            composable(route = Route.Welcome.route) {
                WelcomeScreen(
                    onNavigateToCreateTravel = {
                        navController.navigate(Route.CreateTravel.route)
                    },
                    onNavigateToJoinTrip = {
                        navController.navigate(Route.JoinTrip.route)
                    }
                )
            }

            composable(route = Route.CreateTravel.route) {
                val viewModel: TravelViewModel = koinViewModel()
                val uiState by viewModel.uiState.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                val currentUiState = uiState

                LaunchedEffect(Unit) {
                    viewModel.uiState.collectLatest { state ->
                        when (state) {
                            is TravelUiState.Success<*> -> {
                                Log.d("ISAAC", "Success travel creation")

                                navController.navigate(Route.TravelList.route) {
                                    popUpTo(Route.Welcome.route) {
                                        inclusive = true
                                    }
                                }
                                viewModel.resetState()
                            }

                            is TravelUiState.Error -> {
                                snackbarHostState.showSnackbar(state.message)
                                viewModel.resetState()
                            }

                            else -> Unit
                        }
                    }
                }

                CreateTravelScreen(
                    uiState = uiState,
                    onTravelCreated = { destination, startDate, endDate, summary, accommodationName, accommodationPhone, accommodationCheckIn, accommodationCheckOut, accommodationLocation, accommodationMapUri, reservationCode, extraInfo, additionalInfo ->
                        viewModel.createTravel(
                            destination,
                            startDate,
                            endDate,
                            summary,
                            accommodationName,
                            accommodationPhone,
                            accommodationCheckIn,
                            accommodationCheckOut,
                            accommodationLocation,
                            accommodationMapUri,
                            reservationCode,
                            extraInfo,
                            additionalInfo
                        )
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(route = Route.JoinTrip.route) {
                JoinTripScreen(
                    onTripJoined = {
                        navController.navigate(Route.TravelList.route) {
                            popUpTo(Route.Welcome.route)
                        }
                    },
                    onNavigateToCameraScanner = {
                        navController.navigate(Route.CameraScanner.route)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(route = Route.CameraScanner.route) {
                CameraScannerScreen(
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }

            composable(route = Route.TravelList.route) {
                val viewModel: TravelViewModel = koinViewModel()
                val uiState by viewModel.uiState.collectAsState()
                val travels by viewModel.travels.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                TravelListScreen(
                    uiState = uiState,
                    trips = travels,
                    onGetAllTravels = { viewModel.getAllTravels() },
                    onResetState = { viewModel.resetState() },
                    onCreateTravelClick = {
                        navController.navigate(Route.CreateTravel.route)
                    },
                    onTravelClick = { travelId ->
                        navController.navigate(Route.HomeNavigation.route) {
                            popUpTo(Route.WelcomeNavigation.route) { inclusive = true }
                        }
                        // TODO: Pass travelId to home navigation to load specific travel
                    },
                    onShowSnackbar = { message ->
                        snackbarHostState.showSnackbar(message)
                    }
                )
            }
        }
    }
}
