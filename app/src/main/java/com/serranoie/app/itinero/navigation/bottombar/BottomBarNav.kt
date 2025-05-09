package com.serranoie.app.itinero.navigation.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Message
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryAddCheck
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.serranoie.app.itinero.navigation.Screen

@Composable
fun BottomBarNav(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navigationItems = listOf(
        NavigationItem(
            title = "Home", icon = Icons.Rounded.Home, route = Screen.HOME.name
        ),
        NavigationItem(
            title = "Itinerary", icon = Icons.Rounded.LibraryAddCheck, route = Screen.ITINERARY.name
        ),
        NavigationItem(
            title = "Chat", icon = Icons.AutoMirrored.Rounded.Message, route = Screen.CHAT.name
        ),
    )

    BottomAppBar {
        navigationItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected, onClick = {
                if (currentRoute != item.route) {
                    navController.navigate(item.route) {
                        popUpTo(Screen.HOME.name) {
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
                    item.title, color = if (isSelected) Color.Black else Color.Gray
                )
            }, colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.surface,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
            )
        }
    }
}