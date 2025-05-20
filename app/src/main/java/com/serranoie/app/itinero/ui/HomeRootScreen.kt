package com.serranoie.app.itinero.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.chat.ChatScreen
import com.serranoie.app.feature.home.HomeScreen
import com.serranoie.app.feature.home.navigation.bottombar.BottomBarNav
import com.serranoie.app.feature.expenses.navigation.expensesGraph
import com.serranoie.app.feature.itinerary.navigation.itineraryGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRootScreen() {
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf(
                    Route.Home.route,
                    Route.Itinerary.route,
                    Route.Expenses.route,
                )
            ) {
                BottomBarNav(navController)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Route.Home.route) {
                HomeScreen(navController)
            }

            itineraryGraph(navController)
            expensesGraph(navController)
            composable(Route.Chat.route) {
                ChatScreen(navController)
            }
        }
    }
}