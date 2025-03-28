package com.serranoie.app.itinero.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.serranoie.app.itinero.feature.auth.ui.login.AuthScreen
import com.serranoie.app.itinero.feature.home.ui.HomeScreen
import com.serranoie.app.itinero.feature.onboard.ui.OnboardScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        appStartNavigation(navController)
        homeNavigation(navController)
        authNavigation(navController)
    }
}

// Nested navigation for onboarding
fun NavGraphBuilder.appStartNavigation(navController: NavHostController) {
    navigation(
        route = Route.AppStartNavigation.route, startDestination = Route.Onboarding.route
    ) {
        composable(route = Route.Onboarding.route) {
            OnboardScreen(
                onFinished = {
                    navController.navigate(Route.AuthNavigation.route) {
                        popUpTo(Route.AppStartNavigation.route) { inclusive = true }
                    }
                })
        }
    }
}

// Nested navigation for Home
fun NavGraphBuilder.homeNavigation(navController: NavHostController) {
    navigation(
        route = Route.HomeNavigation.route, startDestination = Route.Home.route
    ) {
        composable(route = Route.Home.route) {
            HomeScreen()
        }
    }
}

fun NavGraphBuilder.authNavigation(navController: NavHostController) {
    navigation(
        route = Route.AuthNavigation.route, startDestination = Route.Authentication.route
    ) {
        composable(route = Route.Authentication.route) {
            AuthScreen()
        }
        composable(route = Route.Authentication.route) {
            AuthScreen()
        }
        composable(route = Route.Authentication.route) {
            AuthScreen()
        }
        composable(route = Route.Authentication.route) {
            AuthScreen()
        }
    }
}