package com.serranoie.app.itinero.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryAddCheck
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.core.navigation.Screen
import com.serranoie.app.feature.chat.ChatScreen
import com.serranoie.app.feature.expenses.navigation.expensesGraph
import com.serranoie.app.feature.home.HomeScreen
import com.serranoie.app.feature.home.HomeViewModel
import com.serranoie.app.feature.home.HomeUiState
import com.serranoie.app.feature.itinerary.navigation.itineraryGraph
import com.serranoie.app.feature.settings.trip.TripInfoSettingsScreen
import com.serranoie.app.feature.settings.trip.TripSettingsScreen
import com.serranoie.app.feature.settings.trip.TripSettingsViewModel
import com.serranoie.itinero.core.data.local.persistence.AuthPreferences
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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

    Scaffold { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = navController, startDestination = Route.Home.route
            ) {
                composable(Route.Home.route) {
                    val viewmodel =
                        koinViewModel<HomeViewModel>(parameters = { parametersOf(tripId) })
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

                itineraryGraph(navController, tripId, tripInfo)

                expensesGraph(navController, tripId)

                composable(Route.Chat.route) {
                    ChatScreen(navController, tripId)
                }

                composable(
                    route = "trip_settings/{tripId}?scrollTo={scrollTo}", arguments = listOf(
                        navArgument("tripId") { type = NavType.StringType },
                        navArgument("scrollTo") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        })
                ) { backStackEntry ->
                    val routeTripId = backStackEntry.arguments?.getString("tripId") ?: ""
                    val scrollTo = backStackEntry.arguments?.getString("scrollTo")

                    val tripSettingsViewModel = koinViewModel<TripSettingsViewModel>(
                        parameters = { parametersOf(routeTripId) }
                    )
                    val qrBitmap by tripSettingsViewModel.qrBitmap.collectAsState()
                    val membersUiState by tripSettingsViewModel.membersUiState.collectAsState()
                    val currentUserMember by tripSettingsViewModel.currentUserMember.collectAsState()
                    val authPreferences = koinInject<AuthPreferences>()

                    LaunchedEffect(routeTripId) {
                        val userId = authPreferences.getUserId()
                        userId?.let {
                            tripSettingsViewModel.fetchCurrentUserMembershipStatus(
                                groupCode = routeTripId,
                                userId = it
                            )
                        }
                        Log.d("ITINERO - $TAG", "Fetching current user status, id: $userId")
                    }

                    TripSettingsScreen(
                        navController = navController,
                        tripId = routeTripId,
                        scrollTo = scrollTo,
                        trip = tripInfo,
                        qrBitmap = qrBitmap,
                        membersUiState = membersUiState,
                        currentUserMember = currentUserMember,
                        onGenerateQrCode = { tripId ->
                            tripSettingsViewModel.setQrText(tripId)
                            tripSettingsViewModel.generateQrCode()
                        },
                        onFetchMembers = { groupCode ->
                            tripSettingsViewModel.fetchMembers(groupCode)
                        },
                        onAcceptMember = { groupCode, memberId, onSuccess, onError ->
                            tripSettingsViewModel.acceptMember(
                                groupCode,
                                memberId,
                                onSuccess,
                                onError
                            )
                        },
                        onRejectMember = { groupCode, memberId, onSuccess, onError ->
                            tripSettingsViewModel.rejectMember(
                                groupCode,
                                memberId,
                                onSuccess,
                                onError
                            )
                        },
                        onRemoveMember = { groupCode, memberId, onSuccess, onError ->
                            tripSettingsViewModel.removeMember(
                                groupCode,
                                memberId,
                                onSuccess,
                                onError
                            )
                        }
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
                        })
                }
            }

            if (currentRoute in listOf(
                    Route.Home.route,
                    Route.Itinerary.route,
                    Route.Expenses.route,
                )
            ) {
                HorizontalFloatingToolbar(
                    expanded = true,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-16).dp),
                    scrollBehavior = FloatingToolbarDefaults.exitAlwaysScrollBehavior(
                        exitDirection = FloatingToolbarExitDirection.Bottom
                    ),
                    floatingActionButton = {
                        when (currentRoute) {
                            Route.Home.route -> {
                                FloatingToolbarDefaults.VibrantFloatingActionButton(
                                    onClick = {
                                        navController.navigate(
                                            Route.TripSettings.createRoute(
                                                tripId = tripInfo?.groupCode!!
                                            )
                                        )
                                    }) {
                                    Icon(
                                        Icons.Filled.Edit, contentDescription = "Add itinerary item"
                                    )
                                }
                            }

                            Route.Itinerary.route -> {
                                FloatingToolbarDefaults.VibrantFloatingActionButton(
                                    onClick = {
                                        navController.navigate(Screen.ADD_ITINERARY.name)
                                    }) {
                                    Icon(
                                        Icons.Filled.Add, contentDescription = "Add itinerary item"
                                    )
                                }
                            }

                            Route.Expenses.route -> {
                                FloatingToolbarDefaults.VibrantFloatingActionButton(
                                    onClick = {
                                        navController.navigate(Screen.ADD_EXPENSE.name)
                                    }) {
                                    Icon(
                                        Icons.Filled.Add, contentDescription = "Add expense"
                                    )
                                }
                            }

                            else -> {
                                FloatingToolbarDefaults.VibrantFloatingActionButton(
                                    onClick = { /* Default action */ }) {
                                    Icon(
                                        Icons.Filled.Add, contentDescription = "Add"
                                    )
                                }
                            }
                        }
                    },
                    content = {
                        // Home
                        IconButton(
                            onClick = {
                                if (currentRoute != Route.Home.route) {
                                    navController.navigate(Route.Home.createRoute(tripId)) {
                                        popUpTo(Route.Home.createRoute(tripId)) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }) {
                            Icon(
                                Icons.Rounded.Home, contentDescription = "Home"
                            )
                        }

                        // Itinerary
                        IconButton(
                            onClick = {
                                if (currentRoute != Route.Itinerary.route) {
                                    navController.navigate(Route.Itinerary.route) {
                                        popUpTo(Route.Home.createRoute(tripId)) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }) {
                            Icon(
                                Icons.Rounded.LibraryAddCheck, contentDescription = "Itinerary"
                            )
                        }

                        // Expenses
                        IconButton(
                            onClick = {
                                if (currentRoute != Route.Expenses.route) {
                                    navController.navigate(Route.Expenses.route) {
                                        popUpTo(Route.Home.createRoute(tripId)) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }) {
                            Icon(
                                Icons.Rounded.MonetizationOn, contentDescription = "Expenses"
                            )
                        }

                        // Chat
                        IconButton(
                            onClick = {
                                if (currentRoute != Route.Chat.route) {
                                    navController.navigate(Route.Chat.route) {
                                        popUpTo(Route.Home.createRoute(tripId)) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }) {
                            Icon(
                                Icons.AutoMirrored.Rounded.Message, contentDescription = "Chat"
                            )
                        }
                    })
            }
        }
    }
}
