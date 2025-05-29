package com.serranoie.app.itinero.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.auth.navigation.AuthNavigationGraph
import com.serranoie.app.feature.onboard.navigation.OnboardNavigationGraph
import com.serranoie.app.feature.welcome.navigation.WelcomeNavigationGraph

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    hasLinkedTrips: Boolean = false,
    hasMultipleTrips: Boolean = false,
) {
    // Determine the actual start destination based on trip status
    val actualStartDestination = when {
        !hasLinkedTrips -> Route.WelcomeNavigation.route // No trips - show welcome
        hasMultipleTrips -> Route.TravelList.route // Multiple trips - show travel list
        else -> Route.HomeNavigation.route // Single trip - show home directly
    }

    NavHost(navController = navController, startDestination = actualStartDestination) {
        with(OnboardNavigationGraph()) {
            build(navController)
        }
        with(HomeNavigationGraph()) {
            build(navController)
        }
        with(AuthNavigationGraph()) {
            build(navController)
        }
        with(WelcomeNavigationGraph()) {
            build(navController)
        }
    }
}
