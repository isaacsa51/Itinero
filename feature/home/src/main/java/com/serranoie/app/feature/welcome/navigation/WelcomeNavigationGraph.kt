package com.serranoie.app.feature.welcome.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
                val autocompleteResults by sharedViewModel.locationAutofill.collectAsState()
                val selectedPlaceDetails by sharedViewModel.selectedPlaceDetails.collectAsState()
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
                    onTravelCreated = { groupName, destination, startDate, endDate, summary, accommodationName, accommodationPhone, accommodationCheckIn, accommodationCheckOut, accommodationLocation, accommodationMapUri, reservationCode, extraInfo, additionalInfo, accommodationExtraInfo ->
                        val coordinates = sharedViewModel.getSelectedLocationCoordinates()
                        sharedViewModel.createTravel(
                            groupName = groupName,
                            destination = destination,
                            startDate = startDate,
                            endDate = endDate,
                            summary = summary,
                            accommodationName = accommodationName,
                            accommodationPhone = accommodationPhone,
                            accommodationCheckIn = accommodationCheckIn,
                            accommodationCheckOut = accommodationCheckOut,
                            accommodationReservationCode = reservationCode, // Use reservationCode for accommodation
                            accommodationExtraInfo = accommodationExtraInfo, // Use the new accommodationExtraInfo field
                            reservationCode = reservationCode, // Trip-level reservation code
                            extraInfo = extraInfo // Use extraInfo for trip-level extra info
                        )
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onAccommodationLocationQueryChanged = { query ->
                        sharedViewModel.searchPlaces(query)
                    },
                    onAccommodationNameQueryChanged = { query ->
                        // Use the same search functionality as location
                        sharedViewModel.searchPlaces(query)
                    },
                    autocompleteResults = autocompleteResults,
                    accommodationNameAutocompleteResults = autocompleteResults, // Use the same results as location
                    onAutocompleteResultClick = { selectedResult ->
                        sharedViewModel.applyAutocompleteSelection(selectedResult)
                    },
                    onAccommodationNameAutocompleteResultClick = { selectedResult ->
                        // Use the same apply function as location
                        sharedViewModel.applyAutocompleteSelection(selectedResult)
                    },
                    selectedPlaceDetails = selectedPlaceDetails
                )
            }

            composable(route = Route.JoinTrip.route) {
                val sharedViewModel = koinViewModel<SharedTravelViewModel>()
                val travelListViewModel = koinViewModel<TravelListViewModel>()
                val joinUiState by sharedViewModel.joinUiState.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(joinUiState) {
                    when (val currentState = joinUiState) {
                        is TravelUiState.Success<*> -> {
                            travelListViewModel.getAllTravels()

                            navController.navigate(Route.TravelList.route) {
                                launchSingleTop = true
                            }
                            sharedViewModel.resetJoinState()
                        }

                        is TravelUiState.Error -> {
                            Log.e("ITINERO - WelcomeNav", "Error travel join, $currentState")
                            // To show a snackbar here, you'd call:
                            // snackbarHostState.showSnackbar(currentState.message)
                            // However, JoinTripScreen or a wrapper needs to use this snackbarHostState.
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

                LaunchedEffect(uiState, travels) {
                    when (val currentState = uiState) {
                        is TravelUiState.Success<*> -> {
                            if (travels.isEmpty()) {
                                Log.d("ITINERO - WelcomeNav", "No trips found, navigating to Welcome")
                                navController.navigate(Route.Welcome.route) {
                                    popUpTo(Route.TravelList.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }

                        is TravelUiState.Error -> {
                            Log.e("ITINERO - WelcomeNav", "Error loading trips: ${currentState.message}")

                            if (currentState.message.contains(
                                    "Please log in again",
                                    ignoreCase = true
                                ) ||
                                currentState.message.contains(
                                    "User not found",
                                    ignoreCase = true
                                ) ||
                                currentState.message.contains(
                                    "Session expired",
                                    ignoreCase = true
                                ) ||
                                currentState.message.contains("Unauthorized", ignoreCase = true)
                            ) {

                                Log.d(
                                    "ITINERO - WelcomeNav",
                                    "Authentication error detected, navigating to auth screen"
                                )
                                navController.navigate(Route.AuthNavigation.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Route.Welcome.route) {
                                    popUpTo(Route.TravelList.route) { inclusive = true }
                                    launchSingleTop = true
                                }
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
