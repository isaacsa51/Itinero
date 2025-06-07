package com.serranoie.app.feature.expenses.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.expenses.ExpensesScreen
import com.serranoie.app.feature.expenses.ExpenseItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shop
import com.serranoie.app.feature.expenses.AddExpenseScreen
import com.serranoie.app.feature.expenses.ExpenseDetailsScreen
import java.time.LocalDate

fun NavGraphBuilder.expensesGraph(navController: NavController, tripId: String) {
    composable(Route.Expenses.route) {
        val currentDate = LocalDate.now()
        val startDate = currentDate.minusDays(3)
        val endDate = currentDate.plusDays(3)

        val mockExpenses = mapOf(
            startDate to listOf(
                ExpenseItem(
                    id = 1,
                    expenseDate = startDate,
                    expenseType = "Food",
                    expenseCategory = "Groceries",
                    expenseName = "Supermarket Shopping",
                    membersCount = 4,
                    amountOwed = 27.50,
                    isCompleted = false,
                    isYours = true,
                    icon = Icons.Filled.Shop
                ),
                ExpenseItem(
                    id = 2,
                    expenseDate = startDate,
                    expenseType = "Transportation",
                    expenseCategory = "Taxi",
                    expenseName = "Airport Transfer",
                    membersCount = 3,
                    amountOwed = 15.33,
                    isCompleted = true,
                    isYours = false,
                    icon = Icons.Default.Money
                )
            ),
            currentDate to listOf(
                ExpenseItem(
                    id = 3,
                    expenseDate = currentDate,
                    expenseType = "Food",
                    expenseCategory = "Dining Out",
                    expenseName = "Lunch Meeting",
                    membersCount = 2,
                    amountOwed = 22.75,
                    isCompleted = false,
                    isYours = true,
                    icon = Icons.Filled.Restaurant
                )
            ),
            endDate to emptyList()
        )

        ExpensesScreen(
            navController = navController,
            expenses = mockExpenses
        )
    }

    composable(Route.AddExpense.route) {
        AddExpenseScreen(
            navController = navController,
        )
    }

    composable(Route.ExpenseDetails.route) {
        ExpenseDetailsScreen(
            navController = navController,
        )
    }
}