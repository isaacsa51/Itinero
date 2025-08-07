/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: SettingsNavigationGraph.kt
 - Project: Itinero
 - Module: Itinero.core.settings.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 agosto 2025
 */

package com.serranoie.core.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.serranoie.app.core.navigation.Route
import com.serranoie.core.settings.SettingsScreen
import com.serranoie.core.settings.SettingsViewModel
import com.serranoie.core.settings.account.AccountSettingsScreen
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

fun NavGraphBuilder.settingsGraph(
    navController: NavController,
    currentThemeMode: String,
    isMaterialYouEnabled: Boolean,
    onThemeModeChanged: (String) -> Unit,
    onMaterialYouChanged: (Boolean) -> Unit,
    onLogout: (() -> Unit)? = null
) {
    composable(Route.Settings.route) {
        val viewModel: SettingsViewModel = koinViewModel()
        val userProfile by viewModel.userProfile.collectAsState()

        // Ensure user profile is fetched when navigating to this screen
        LaunchedEffect(Unit) {
            viewModel.fetchUserProfile()
        }

        SettingsScreen(
            navController = navController,
            settingsViewModel = viewModel,
            currentThemeMode = currentThemeMode,
            isMaterialYouEnabled = isMaterialYouEnabled,
            onThemeModeChanged = onThemeModeChanged,
            onMaterialYouChanged = onMaterialYouChanged,
            userName = userProfile?.fullName ?: "Loading...",
        )
    }

    composable(Route.Profile.route) {
        val viewModel: SettingsViewModel = koinViewModel()

        // Ensure user profile is fetched when navigating to this screen
        LaunchedEffect(Unit) {
            viewModel.fetchUserProfile()
        }

        AccountSettingsScreen(
            navController = navController,
            settingsViewModel = viewModel,
            onLogout = onLogout
        )
    }
}
