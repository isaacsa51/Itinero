package com.serranoie.app.itinero.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.serranoie.app.itinero.feature.auth.ui.forgotpass.ForgotPasswordScreen
import com.serranoie.app.itinero.feature.auth.ui.login.AuthScreen
import com.serranoie.app.itinero.feature.auth.ui.register.RegisterScreen
import com.serranoie.app.itinero.feature.bills.ExpenseDetailsScreen
import com.serranoie.app.itinero.feature.bills.ExpensesScreen
import com.serranoie.app.itinero.feature.bills.ExpenseItem
import com.serranoie.app.itinero.feature.chat.ChatScreen
import com.serranoie.app.itinero.feature.home.ui.HomeScreen
import com.serranoie.app.itinero.feature.itinerary.ItineraryItem
import com.serranoie.app.itinero.feature.itinerary.ItineraryScreen
import com.serranoie.app.itinero.feature.onboard.ui.OnboardScreen
import com.serranoie.app.itinero.feature.settings.trip.TripInfoSettingsScreen
import com.serranoie.app.itinero.feature.settings.trip.TripSettingsScreen
import java.time.LocalDate

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

fun NavGraphBuilder.appStartNavigation(navController: NavHostController) {
    navigation(
        route = Route.AppStartNavigation.route, startDestination = Route.HomeNavigation.route
    ) {
        composable(route = Route.Onboarding.route) {
            OnboardScreen(
                onFinished = {
                    navController.navigate(Route.AuthNavigation.route) {
                        popUpTo(Route.AppStartNavigation.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

fun NavGraphBuilder.homeNavigation(navController: NavHostController) {
    navigation(
        route = Route.HomeNavigation.route, startDestination = Route.Home.route
    ) {
        val mockItinerary = mapOf(
            LocalDate.of(2025, 5, 6) to listOf(
                ItineraryItem(
                    title = "Visit Museum",
                    time = "12:00 PM",
                    location = "Art Museum",
                    description = "Explore the modern art section"
                ),
                ItineraryItem(
                    title = "Lunch",
                    time = "2:00 PM",
                    location = "City Café",
                    description = "Try the local special"
                )
            ),
            LocalDate.of(2025, 5, 7) to listOf(
                ItineraryItem(
                    title = "Beach Day",
                    time = "10:00 AM",
                    location = "Palm Beach",
                    description = "Sunbathing and volleyball"
                )
            ),
            LocalDate.of(2025, 5, 8) to emptyList(),
            LocalDate.of(2025, 5, 9) to emptyList(),
            LocalDate.of(2025, 5, 10) to emptyList(),
            LocalDate.of(2025, 5, 11) to listOf(
                ItineraryItem(
                    title = "Visit Museum",
                    time = "12:00 PM",
                    location = "Art Museum",
                    description = "Explore the modern art section"
                ),
                ItineraryItem(
                    title = "Lunch",
                    time = "2:00 PM",
                    location = "City Café",
                    description = "Try the local special"
                )
            ),
        )

        val mockExpenses = mapOf(
            LocalDate.of(2025, 5, 6) to listOf(
                ExpenseItem(
                    id = 1,
                    expenseDate = LocalDate.of(2025, 5, 6),
                    expenseType = "Groceries",
                    expenseCategory = "Food",
                    expenseName = "Supermarket",
                    membersCount = 3,
                    amountOwed = 100.0,
                    isCompleted = false,
                    isYours = true,
                ),
                ExpenseItem(
                    id = 2,
                    expenseDate = LocalDate.of(2025, 5, 6),
                    expenseType = "Food",
                    expenseCategory = "Food",
                    expenseName = "Groceries",
                    membersCount = 2,
                    amountOwed = 45.20,
                    isCompleted = true,
                ),
                ExpenseItem(
                    id = 3,
                    expenseDate = LocalDate.of(2025, 5, 6),
                    expenseType = "Entertainment",
                    expenseCategory = "Entertainment",
                    expenseName = "Movie Tickets",
                    membersCount = 3,
                    amountOwed = 32.50,
                    isYours = true,
                ),
                ExpenseItem(
                    id = 4,
                    expenseDate = LocalDate.of(2025, 5, 6),
                    expenseType = "Food",
                    expenseCategory = "Restaurant",
                    expenseName = "Dinner at La Taquería",
                    membersCount = 4,
                    amountOwed = 56.75,
                )
            ), LocalDate.of(2025, 5, 6).plusDays(10) to emptyList()
        )

        composable(route = Route.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(route = Route.Itinerary.route) {
            ItineraryScreen(
                itinerary = mockItinerary,
                onSwiped = {  },
                navController = navController
            )
        }

        composable(route = Route.Chat.route) {
            ChatScreen(navController = navController)
        }

        composable(route = Route.Expenses.route) {
            ExpensesScreen(
                navController = navController,
                expenses = mockExpenses
            )
        }

        composable(route = Route.AddExpense.route) {
            ExpenseDetailsScreen(navController)
        }

        composable(route = Route.TripSettings.route) {
            TripSettingsScreen(navController = navController)
        }
    }
}

fun NavGraphBuilder.authNavigation(navController: NavHostController) {
    navigation(
        route = Route.AuthNavigation.route,
        startDestination = Route.Authentication.route
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