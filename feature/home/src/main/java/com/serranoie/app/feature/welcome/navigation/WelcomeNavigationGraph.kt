package com.serranoie.app.feature.welcome.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.serranoie.app.core.navigation.NavigationGraph
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.SharedTravelViewModel
import com.serranoie.app.feature.TravelListViewModel
import com.serranoie.app.feature.TravelUiState
import com.serranoie.app.feature.welcome.CreateTravelScreen
import com.serranoie.app.feature.welcome.JoinTripScreen
import com.serranoie.app.feature.welcome.TravelListScreen
import com.serranoie.app.feature.welcome.WelcomeScreen
import com.serranoie.app.feature.welcome.camera.CameraScannerScreen
import org.koin.androidx.compose.koinViewModel

class WelcomeNavigationGraph : NavigationGraph {
    @SuppressLint("UnrememberedGetBackStackEntry")
    override fun NavGraphBuilder.build(navController: NavHostController) {
        navigation(
            route = Route.WelcomeNavigation.route,
            startDestination = Route.TravelList.route
        ) {
            composable(route = Route.Welcome.route) {
                WelcomeScreen(onNavigateToCreateTravel = {
                    navController.navigate(Route.CreateTravel.route)
                }, onNavigateToJoinTrip = {
                    navController.navigate(Route.JoinTrip.route)
                })
            }

            composable(route = Route.CreateTravel.route) {
                val sharedViewModel = koinViewModel<SharedTravelViewModel>()
                val createUiState by sharedViewModel.createUiState.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(createUiState) {
                    when (val currentState = createUiState) {
                        is TravelUiState.Success<*> -> {
                            Log.d("ISAAC", "Success travel creation")
                            navController.navigate(Route.TravelList.route) {
                                launchSingleTop = true
                            }
                            sharedViewModel.resetCreateState()
                        }

                        is TravelUiState.Error -> {
                            snackbarHostState.showSnackbar(currentState.message)
                            sharedViewModel.resetCreateState()
                        }

                        else -> Unit
                    }
                }

                CreateTravelScreen(
                    uiState = createUiState,
                    onTravelCreated = { groupName, destination, startDate, endDate, summary, accommodationName, accommodationPhone, accommodationCheckIn, accommodationCheckOut, accommodationLocation, accommodationMapUri, reservationCode, extraInfo, additionalInfo ->
                        sharedViewModel.createTravel(
                            groupName,
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
                    })
            }

            composable(route = Route.JoinTrip.route) {
                val sharedViewModel = koinViewModel<SharedTravelViewModel>()
                val joinUiState by sharedViewModel.joinUiState.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(joinUiState) {
                    when (val currentState = joinUiState) {
                        is TravelUiState.Success<*> -> {
                            Log.e("ISAAC", "Success travel join, $currentState")

                            navController.navigate(Route.TravelList.route) {
                                launchSingleTop = true
                            }
                            sharedViewModel.resetJoinState()
                        }

                        is TravelUiState.Error -> {
                            Log.e("ISAAC", "Error travel join, $currentState")
                        }

                        else -> Unit
                    }
                }

                JoinTripScreen(
                    uiState = joinUiState,
                    onTripJoined = { groupCode ->
                        sharedViewModel.joinTravel(groupCode)
                    },
                    onNavigateToCameraScanner = {
                        navController.navigate(Route.CameraScanner.route)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    })
            }

            composable(route = Route.CameraScanner.route) {
                CameraScannerScreen(
                    onBackPressed = {
                        navController.popBackStack()
                    })
            }

            composable(route = Route.TravelList.route) {
                val travelListViewModel = koinViewModel<TravelListViewModel>()
                val uiState by travelListViewModel.uiState.collectAsState()
                val travels by travelListViewModel.travels.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                // Handle navigation based on state and trips
                LaunchedEffect(uiState, travels) {
                    when (val currentState = uiState) {
                        is TravelUiState.Success<*> -> {
                            if (travels.isEmpty()) {
                                Log.d("ITINERO", "No trips found, navigating to Welcome")
                                navController.navigate(Route.Welcome.route) {
                                    popUpTo(Route.TravelList.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }

                        is TravelUiState.Error -> {
                            Log.e("ITINERO", "Error loading trips: ${currentState.message}")
                            navController.navigate(Route.Welcome.route) {
                                popUpTo(Route.TravelList.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }

                        else -> {
                            // Still loading or idle, stay on this screen
                        }
                    }
                }

                TravelListScreen(
                    uiState = uiState,
                    trips = travels,
                    onGetAllTravels = { travelListViewModel.getAllTravels() },
                    onResetState = { travelListViewModel.resetState() },
                    onAddTravelClick = {
                        navController.navigate(Route.CreateTravel.route)
                    },
                    onTravelClick = { tripId ->
                        navController.navigate(Route.Home.createRoute(tripId)) {
                            popUpTo(Route.WelcomeNavigation.route) { inclusive = true }
                        }
                    },
                    onShowSnackbar = { message ->
                        snackbarHostState.showSnackbar(message)
                    },
                    onJoinTravelClick = {
                        navController.navigate(Route.JoinTrip.route)
                    }
                )
            }
        }
    }
}
