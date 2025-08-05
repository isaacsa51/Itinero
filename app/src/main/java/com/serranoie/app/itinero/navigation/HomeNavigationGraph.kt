package com.serranoie.app.itinero.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.serranoie.app.core.navigation.NavigationGraph
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.itinero.ui.HomeRootScreen
import com.serranoie.core.settings.SettingsScreen
import com.serranoie.core.settings.SettingsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class HomeNavigationGraph : NavigationGraph {
    override fun NavGraphBuilder.build(navController: NavHostController) {
        navigation(
            route = Route.HomeNavigation.route,
            startDestination = Route.Home.route
        ) {
            composable(
                route = Route.Home.route + "?userName={userName}&userStatus={userStatus}",
                arguments = listOf(
                    navArgument("tripId") { type = NavType.StringType },
                    navArgument("userName") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument("userStatus") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
                val userName = backStackEntry.arguments?.getString("userName") ?: ""
                val userStatus = backStackEntry.arguments?.getString("userStatus") ?: ""
                val coroutineScope = rememberCoroutineScope()
                HomeRootScreen(
                    tripId = tripId,
                    userName = userName,
                    userStatus = userStatus,
                    onTripDeleted = { hasOtherTrips ->
                        coroutineScope.launch {
                            // Navigate based on whether user has other trips
                            if (hasOtherTrips) {
                                // Navigate to travel list if user has other trips
                                navController.navigate(Route.WelcomeNavigation.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            } else {
                                // Navigate to welcome screen if no other trips
                                // The WelcomeNavigationGraph will automatically handle showing Welcome screen
                                // when TravelList is empty
                                navController.navigate(Route.WelcomeNavigation.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    },
                    onTripLeft = { hasOtherTrips ->
                        coroutineScope.launch {
                            // Navigate based on whether user has other trips
                            if (hasOtherTrips) {
                                // Navigate to travel list if user has other trips
                                navController.navigate(Route.WelcomeNavigation.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            } else {
                                // Navigate to welcome screen if no other trips
                                navController.navigate(Route.WelcomeNavigation.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    },
                    onLogout = {
                        // This uses the main navController which has access to AuthNavigation
                        navController.navigate(Route.AuthNavigation.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route = Route.Settings.route + "?userName={userName}&userStatus={userStatus}",
                arguments = listOf(
                    navArgument("userName") { type = NavType.StringType },
                    navArgument("userStatus") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val userName = backStackEntry.arguments?.getString("userName") ?: ""
                val userStatus = backStackEntry.arguments?.getString("userStatus") ?: ""
                val settingsViewModel: SettingsViewModel = koinViewModel()
                val themeMode by settingsViewModel.themeMode.collectAsState()
                val isMaterialYouEnabled by settingsViewModel.isMaterialYouEnabled.collectAsState()

                SettingsScreen(
                    navController = navController,
                    settingsViewModel = settingsViewModel,
                    currentThemeMode = themeMode,
                    isMaterialYouEnabled = isMaterialYouEnabled,
                    userName = userName,
                    userStatus = userStatus,
                    onThemeModeChanged = { newThemeMode ->
                        settingsViewModel.setThemeMode(newThemeMode)
                    },
                    onMaterialYouChanged = { enabled ->
                        settingsViewModel.setMaterialYouEnabled(enabled)
                    }
                )
            }
        }
    }
}
