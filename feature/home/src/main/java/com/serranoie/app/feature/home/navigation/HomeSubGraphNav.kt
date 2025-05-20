package com.serranoie.app.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.serranoie.app.feature.home.HomeScreen

fun NavGraphBuilder.homeGraph(navController: NavHostController) {
    composable("home") {
        HomeScreen(navController)
    }
}