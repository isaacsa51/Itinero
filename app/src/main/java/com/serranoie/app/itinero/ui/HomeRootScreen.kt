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
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.serranoie.app.feature.chat.exampleUiState
import com.serranoie.app.feature.expenses.navigation.expensesGraph
import com.serranoie.app.feature.home.HomeScreen
import com.serranoie.app.feature.home.HomeUiState
import com.serranoie.app.feature.home.HomeViewModel
import com.serranoie.app.feature.itinerary.navigation.itineraryGraph
import com.serranoie.app.feature.settings.trip.TripInfoSettingsScreen
import com.serranoie.app.feature.settings.trip.TripSettingsScreen
import com.serranoie.app.feature.settings.trip.TripSettingsViewModel
import com.serranoie.core.settings.SettingsScreen
import com.serranoie.core.settings.SettingsViewModel
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
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

    val floatingToolbarScrollBehavior = FloatingToolbarDefaults.exitAlwaysScrollBehavior(
        exitDirection = FloatingToolbarExitDirection.Bottom
    )

    LaunchedEffect(tripId) {
        homeViewModel.getCurrentTravel()
    }

    Scaffold(
        modifier = if (currentRoute in listOf(
                Route.Home.route,
                Route.Itinerary.route,
                Route.Expenses.route,
            )
        ) {
            Modifier.nestedScroll(floatingToolbarScrollBehavior)
        } else {
            Modifier
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = navController, startDestination = Route.Home.route
            ) {
                composable(Route.Home.route) {
                    val snackbarHostState = remember { SnackbarHostState() }

                    HomeScreen(
                        navController,
                        tripId = tripId,
                        uiState = uiState,
                        tripInfo = tripInfo,
                        onGetTravel = { homeViewModel.getCurrentTravel() },
                        onShowSnackbar = { message ->
                            snackbarHostState.showSnackbar(message)
                        },
                        onRefresh = { homeViewModel.refreshTrip() })
                }

                itineraryGraph(navController, tripId, tripInfo)

                expensesGraph(navController, tripId)

                composable(Route.Chat.route) {
                    ChatScreen(
                        uiState = exampleUiState,
                        onBackPressed = { navController.popBackStack() }
                    )
                }

                composable(Route.Settings.route) {
                    val settingsViewModel = koinViewModel<SettingsViewModel>()
                    val themeMode by settingsViewModel.themeMode.collectAsState()
                    val isMaterialYouEnabled by settingsViewModel.isMaterialYouEnabled.collectAsState()

                    SettingsScreen(
                        navController = navController,
                        settingsViewModel = settingsViewModel,
                        currentThemeMode = themeMode,
                        isMaterialYouEnabled = isMaterialYouEnabled,
                        onThemeModeChanged = { newThemeMode ->
                            settingsViewModel.setThemeMode(newThemeMode)
                        },
                        onMaterialYouChanged = { enabled ->
                            settingsViewModel.setMaterialYouEnabled(enabled)
                        }
                    )
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
                    val currentUserMembershipStatus by tripSettingsViewModel.currentUserMembershipStatus.collectAsState()
                    val authPreferencesRepository = koinInject<AuthPreferencesRepository>()

                    LaunchedEffect(routeTripId) {
                        Log.d(
                            "ITINERO - $TAG",
                            "Fetching current user status for tripId: $routeTripId"
                        )

                        if (routeTripId.isNotBlank()) {
                            tripSettingsViewModel.fetchCurrentUserMembershipStatus(
                                groupCode = routeTripId
                            )
                        } else {
                            Log.e(
                                "ITINERO - $TAG",
                                "Missing tripId ($routeTripId)"
                            )
                        }
                    }

                    TripSettingsScreen(
                        navController = navController,
                        tripId = routeTripId,
                        scrollTo = scrollTo,
                        trip = tripInfo,
                        qrBitmap = qrBitmap,
                        membersUiState = membersUiState,
                        currentUserMembershipStatus = currentUserMembershipStatus,
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
                    scrollBehavior = floatingToolbarScrollBehavior,
                    floatingActionButton = {
                        when (currentRoute) {
                            Route.Home.route -> {
                                FloatingToolbarDefaults.StandardFloatingActionButton(
                                    onClick = {
                                        navController.navigate(
                                            Route.TripSettings.createRoute(
                                                tripId = tripInfo?.groupCode!!
                                            )
                                        )
                                    }) {
                                    Icon(
                                        Icons.Filled.Edit, contentDescription = "Edit trip settings"
                                    )
                                }
                            }

                            Route.Itinerary.route -> {
                                FloatingToolbarDefaults.StandardFloatingActionButton(
                                    onClick = {
                                        navController.navigate(Screen.ADD_ITINERARY.name)
                                    }) {
                                    Icon(
                                        Icons.Filled.Add, contentDescription = "Add itinerary item"
                                    )
                                }
                            }

                            Route.Expenses.route -> {
                                FloatingToolbarDefaults.StandardFloatingActionButton(
                                    onClick = {
                                        navController.navigate(Screen.ADD_EXPENSE.name)
                                    }) {
                                    Icon(
                                        Icons.Filled.Add, contentDescription = "Add expense"
                                    )
                                }
                            }

                            else -> {
                                FloatingToolbarDefaults.StandardFloatingActionButton(
                                    onClick = { /* Default action */ }) {
                                    Icon(
                                        Icons.Filled.Add, contentDescription = "Add"
                                    )
                                }
                            }
                        }
                    },
                    content = {
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
