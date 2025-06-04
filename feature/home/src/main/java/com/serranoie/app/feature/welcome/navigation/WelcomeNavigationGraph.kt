package com.serranoie.app.feature.welcome.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
            route = Route.WelcomeNavigation.route, startDestination = Route.Welcome.route
        ) {
            composable(route = Route.Welcome.route) {
                val travelListViewModel = koinViewModel<TravelListViewModel>()
                val travels by travelListViewModel.travels.collectAsState()
                val uiState by travelListViewModel.uiState.collectAsState()
                var hasNavigated by remember { mutableStateOf(false) }

                // Load travels when entering welcome navigation (only once)
                LaunchedEffect(Unit) {
                    Log.d("ISAAC", "Loading travels when entering welcome navigation")
                    travelListViewModel.getAllTravels()
                }

                // Navigate to TravelList if user already has travels (only on successful load)
                LaunchedEffect(uiState) {
                    if (!hasNavigated && uiState is TravelUiState.Success<*> && travels.isNotEmpty()) {
                        Log.d("ISAAC", "Initial navigation - Trips from user: $travels")
                        navController.navigate(Route.TravelList.route) {
                            launchSingleTop = true
                        }
                        hasNavigated = true
                    } else if (!hasNavigated && uiState is TravelUiState.Success<*>) {
                        Log.d("ISAAC", "Initial navigation - No trips from user")
                        hasNavigated = true
                    }
                }

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
                    onTravelCreated = { destination, startDate, endDate, summary, accommodationName, accommodationPhone, accommodationCheckIn, accommodationCheckOut, accommodationLocation, accommodationMapUri, reservationCode, extraInfo, additionalInfo ->
                        sharedViewModel.createTravel(
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

                LaunchedEffect(joinUiState) {
                    if (joinUiState is TravelUiState.Success<*>) {
                        navController.navigate(Route.TravelList.route) {
                            launchSingleTop = true
                        }
                        sharedViewModel.resetJoinState()
                    }
                }

                JoinTripScreen(onTripJoined = { groupCode ->
                    sharedViewModel.joinTravel(groupCode)
                }, onNavigateToCameraScanner = {
                    navController.navigate(Route.CameraScanner.route)
                }, onNavigateBack = {
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

                TravelListScreen(
                    uiState = uiState,
                    trips = travels,
                    onGetAllTravels = { travelListViewModel.getAllTravels() },
                    onResetState = { travelListViewModel.resetState() },
                    onAddTravelClick = {
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
                    })
            }
        }
    }
}
