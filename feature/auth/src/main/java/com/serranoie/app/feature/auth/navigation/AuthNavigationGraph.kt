package com.serranoie.app.feature.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.serranoie.app.core.navigation.NavigationGraph
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.auth.ui.AuthScreen
import com.serranoie.app.feature.auth.ui.AuthViewModel
import com.serranoie.app.feature.auth.ui.ForgotPasswordScreen
import com.serranoie.app.feature.auth.ui.RegisterScreen
import org.koin.androidx.compose.koinViewModel

class AuthNavigationGraph : NavigationGraph {
    override fun NavGraphBuilder.build(navController: NavHostController) {
        navigation(
            route = Route.AuthNavigation.route, startDestination = Route.Authentication.route
        ) {
            composable(route = Route.Authentication.route) {
                val viewModel: AuthViewModel = koinViewModel()
                AuthScreen(
                    navController = navController,
                    uiState = viewModel.uiState,
                    onLogin = { email, password -> viewModel.login(email, password) }
                )
            }

            composable(route = Route.Register.route) {
                val viewModel: AuthViewModel = koinViewModel()
                RegisterScreen(
                    navController = navController,
                    uiState = viewModel.uiState,
                    onRegister = { name, lastName, number, email, password ->
                        viewModel.register(name, lastName, number, email, password)
                    }
                )
            }

            composable(route = Route.ForgotPassword.route) {
                ForgotPasswordScreen(navController = navController)
            }
        }
    }
}
