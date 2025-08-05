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

        SettingsScreen(
            navController = navController,
            settingsViewModel = viewModel,
            currentThemeMode = currentThemeMode,
            isMaterialYouEnabled = isMaterialYouEnabled,
            onThemeModeChanged = onThemeModeChanged,
            onMaterialYouChanged = onMaterialYouChanged
        )
    }

    composable(Route.Profile.route) {
        val viewModel: SettingsViewModel = koinViewModel()
        AccountSettingsScreen(
            navController = navController,
            settingsViewModel = viewModel,
            onLogout = onLogout
        )
    }
}
