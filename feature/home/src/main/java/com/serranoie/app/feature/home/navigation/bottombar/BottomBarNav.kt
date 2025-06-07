package com.serranoie.app.feature.home.navigation.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Message
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryAddCheck
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.onboard.navigation.bottombar.NavigationItem

@Composable
fun BottomBarNav(
    navController: NavController,
    tripId: String
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navigationItems = listOf(
        NavigationItem(
            title = "Home", icon = Icons.Rounded.Home, route = Route.Home.createRoute(tripId)
        ), NavigationItem(
            title = "Itinerary", icon = Icons.Rounded.LibraryAddCheck, route = Route.Itinerary.route
        ), NavigationItem(
            title = "Expenses", icon = Icons.Rounded.MonetizationOn, route = Route.Expenses.route
        ), NavigationItem(
            title = "Chat", icon = Icons.AutoMirrored.Rounded.Message, route = Route.Chat.route
        )
    )

    BottomAppBar {
        navigationItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected, onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Route.Home.createRoute(tripId)) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }, icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                }, label = {
                    Text(
                        item.title,
                    )
                }, colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
