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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
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
import com.serranoie.app.feature.chat.ChatViewModel
import com.serranoie.app.feature.expenses.navigation.expensesGraph
import com.serranoie.app.feature.home.HomeScreen
import com.serranoie.app.feature.home.HomeUiState
import com.serranoie.app.feature.home.HomeViewModel
import com.serranoie.app.feature.itinerary.navigation.itineraryGraph
import com.serranoie.app.feature.settings.trip.TripDeletionUiState
import com.serranoie.app.feature.settings.trip.TripInfoSettingsScreen
import com.serranoie.app.feature.settings.trip.TripLeaveTripUiState
import com.serranoie.app.feature.settings.trip.TripSettingsScreen
import com.serranoie.app.feature.settings.trip.TripSettingsViewModel
import com.serranoie.core.settings.SettingsViewModel
import com.serranoie.core.settings.navigation.settingsGraph
import com.serranoie.itinero.core.domain.usecase.GetCurrentUserIdUseCase
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeRootScreen(
    tripId: String,
    onTripDeleted: ((hasOtherTrips: Boolean) -> Unit)? = null,
    onTripLeft: ((hasOtherTrips: Boolean) -> Unit)? = null,
    userName: String = "",
    userStatus: String = "",
    onLogout: (() -> Unit)? = null
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val homeViewModel = koinViewModel<HomeViewModel>(parameters = { parametersOf(tripId) })
    val tripInfo by homeViewModel.trip.collectAsState()
    val uiState by homeViewModel.uiState.collectAsState()
    val overviewUiState by homeViewModel.overviewUiState.collectAsState()

    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val themeMode by settingsViewModel.themeMode.collectAsState()
    val isMaterialYouEnabled by settingsViewModel.isMaterialYouEnabled.collectAsState()

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
                        overviewUiState = overviewUiState,
                        tripInfo = tripInfo,
                        onGetTravel = { homeViewModel.getCurrentTravel() },
                        onGetOverview = { homeViewModel.getTripOverview(tripId) },
                        onShowSnackbar = { message ->
                            snackbarHostState.showSnackbar(message)
                        },
                        onRefresh = {
                            homeViewModel.refreshTrip()
                            homeViewModel.getTripOverview(tripId)
                        },
                        userName = userName,
                        userStatus = userStatus
                    )
                }

                itineraryGraph(navController, tripId, tripInfo)

                expensesGraph(navController, tripId)

                composable(Route.Chat.route) {
                    val chatViewModel = koinViewModel<ChatViewModel>()
                    val chatUiState by chatViewModel.uiState.collectAsState()
                    val isLoading by chatViewModel.isLoading.collectAsState()
                    val error by chatViewModel.error.collectAsState()
                    val isConnected by chatViewModel.isConnected.collectAsState()
                    val typingUsers by chatViewModel.typingUsers.collectAsState()

                    val getCurrentUserIdUseCase =
                        koinInject<GetCurrentUserIdUseCase>()
                    val currentUserId = getCurrentUserIdUseCase() ?: "unknown_user"

                    ChatScreen(
                        groupCode = tripInfo?.groupCode ?: "",
                        groupName = tripInfo?.groupName ?: "Chat",
                        memberCount = tripInfo?.totalMembers ?: 0,
                        currentUserId = currentUserId,
                        onBackPressed = { navController.popBackStack() },
                        uiState = chatUiState,
                        isLoading = isLoading,
                        isConnected = isConnected,
                        error = error,
                        typingUsers = typingUsers,
                        onInitializeChat = { groupCode, groupName, memberCount ->
                            chatViewModel.initializeChat(groupCode, groupName, memberCount)
                        },
                        onSendMessage = { message ->
                            chatViewModel.sendMessage(message)
                        },
                        onRetryConnection = {
                            chatViewModel.retryConnection()
                        },
                        onClearError = {
                            chatViewModel.clearError()
                        },
                        onTypingStarted = {
                            chatViewModel.sendTypingStarted()
                        },
                        onTypingStopped = {
                            chatViewModel.sendTypingStopped()
                        }
                    )
                }

                settingsGraph(
                    navController = navController,
                    currentThemeMode = themeMode,
                    isMaterialYouEnabled = isMaterialYouEnabled,
                    onThemeModeChanged = { newThemeMode ->
                        settingsViewModel.setThemeMode(newThemeMode)
                    },
                    onMaterialYouChanged = { enabled ->
                        settingsViewModel.setMaterialYouEnabled(enabled)
                    },
                    onLogout = onLogout
                )

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
                        parameters = { parametersOf(routeTripId) })
                    val qrBitmap by tripSettingsViewModel.qrBitmap.collectAsState()
                    val membersUiState by tripSettingsViewModel.membersUiState.collectAsState()
                    val currentUserMembershipStatus by tripSettingsViewModel.currentUserMembershipStatus.collectAsState()
                    val deletionUiState by tripSettingsViewModel.deletionUiState.collectAsState()
                    val leaveTripUiState by tripSettingsViewModel.leaveTripUiState.collectAsState()

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
                                "ITINERO - $TAG", "Missing tripId ($routeTripId)"
                            )
                        }
                    }

                    LaunchedEffect(deletionUiState) {
                        if (deletionUiState is TripDeletionUiState.Success) {
                            // Handle successful deletion with proper navigation
                            homeViewModel.getAllTravels { hasOtherTrips ->
                                tripSettingsViewModel.resetDeletionState()
                                onTripDeleted?.invoke(hasOtherTrips)
                            }
                        }
                    }

                    LaunchedEffect(leaveTripUiState) {
                        if (leaveTripUiState is TripLeaveTripUiState.Success) {
                            homeViewModel.getAllTravels { hasOtherTrips ->
                                tripSettingsViewModel.resetLeaveTripState()
                                onTripLeft?.invoke(hasOtherTrips)
                            }
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
                        deletionUiState = deletionUiState,
                        leaveTripUiState = leaveTripUiState,
                        onGenerateQrCode = { tripId ->
                            tripSettingsViewModel.setQrText(tripId)
                            tripSettingsViewModel.generateQrCode()
                        },
                        onFetchMembers = { groupCode ->
                            tripSettingsViewModel.fetchMembers(groupCode)
                        },
                        onAcceptMember = { groupCode, memberId, onSuccess, onError ->
                            tripSettingsViewModel.acceptMember(
                                groupCode, memberId, onSuccess, onError
                            )
                        },
                        onRejectMember = { groupCode, memberId, onSuccess, onError ->
                            tripSettingsViewModel.rejectMember(
                                groupCode, memberId, onSuccess, onError
                            )
                        },
                        onRemoveMember = { groupCode, memberId, onSuccess, onError ->
                            tripSettingsViewModel.removeMember(
                                groupCode, memberId, onSuccess, onError
                            )
                        },
                        onLeaveTrip = { groupCode, onSuccess, onError ->
                            tripSettingsViewModel.leaveTripCurrentTrip(
                                groupCode, onSuccess = {
                                    onSuccess()
                                }, onError = onError
                            )
                        },
                        onDeleteTrip = {
                            tripSettingsViewModel.deleteTrip(
                                groupCode = routeTripId,
                                onSuccess = {
                                    // Success handling is now done in LaunchedEffect above
                                },
                                onError = { errorMessage ->
                                    // Error handling is now done in the UI state
                                    Log.e("HomeRootScreen", "Failed to delete trip: $errorMessage")
                                }
                            )
                        })
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
                                    },
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.tertiary
                                ) {
                                    Icon(
                                        Icons.Filled.Edit, contentDescription = "Edit trip settings"
                                    )
                                }
                            }

                            Route.Itinerary.route -> {
                                FloatingToolbarDefaults.StandardFloatingActionButton(
                                    onClick = {
                                        navController.navigate(Screen.ADD_ITINERARY.name)
                                    },
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.tertiary
                                ) {
                                    Icon(
                                        Icons.Filled.Add, contentDescription = "Add itinerary item"
                                    )
                                }
                            }

                            Route.Expenses.route -> {
                                FloatingToolbarDefaults.StandardFloatingActionButton(
                                    onClick = {
                                        navController.navigate(Screen.ADD_EXPENSE.name)
                                    },
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.tertiary
                                ) {
                                    Icon(
                                        Icons.Filled.Add, contentDescription = "Add expense"
                                    )
                                }
                            }

                            else -> {
                                FloatingToolbarDefaults.StandardFloatingActionButton(
                                    onClick = { /* Default action */ },
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.tertiary
                                ) {
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
                    },
                )
            }
        }
    }
}
