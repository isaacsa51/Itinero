package com.serranoie.app.itinero.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.serranoie.app.core.navigation.NavigationGraph
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.core.navigation.Screen
import com.serranoie.app.itinero.ui.HomeRootScreen

class HomeNavigationGraph : NavigationGraph {
    override fun NavGraphBuilder.build(navController: NavHostController) {
        navigation(
            route = Route.HomeNavigation.route,
            startDestination = Route.Home.route
        ) {
            composable(Route.Home.route) {
                HomeRootScreen()
            }
        }
    }
}