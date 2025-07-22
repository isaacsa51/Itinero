package com.serranoie.app.itinero.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import org.koin.androidx.compose.koinViewModel

class HomeNavigationGraph : NavigationGraph {
    override fun NavGraphBuilder.build(navController: NavHostController) {
        navigation(
            route = Route.HomeNavigation.route,
            startDestination = Route.Home.route
        ) {
            composable(
                route = Route.Home.route,
                arguments = listOf(navArgument("tripId") { type = NavType.StringType })
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
                HomeRootScreen(tripId = tripId)
            }
            composable(route = Route.Settings.route) {
                val settingsViewModel: SettingsViewModel = koinViewModel()
                val themeMode by settingsViewModel.themeMode.collectAsState()
                val isMaterialYouEnabled by settingsViewModel.isMaterialYouEnabled.collectAsState()

                SettingsScreen(
                    navController = navController,
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
        }
    }
}
