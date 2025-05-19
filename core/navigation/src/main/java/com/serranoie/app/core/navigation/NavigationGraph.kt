package com.serranoie.app.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface NavigationGraph {
    fun NavGraphBuilder.build(navController: NavHostController)
}