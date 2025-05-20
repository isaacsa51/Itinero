package com.serranoie.app.feature.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.serranoie.app.core.navigation.NavigationGraph
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.auth.ui.AuthScreen
import com.serranoie.app.feature.auth.ui.ForgotPasswordScreen
import com.serranoie.app.feature.auth.ui.RegisterScreen

class AuthNavigationGraph : NavigationGraph {
    override fun NavGraphBuilder.build(navController: NavHostController) {
        navigation(
            route = Route.AuthNavigation.route, startDestination = Route.Authentication.route
        ) {
            composable(route = Route.Authentication.route) {
                AuthScreen(navController)
            }

            composable(route = Route.Register.route) {
                RegisterScreen(navController = navController)
            }

            composable(route = Route.ForgotPassword.route) {
                ForgotPasswordScreen(navController = navController)
            }
        }
    }
}