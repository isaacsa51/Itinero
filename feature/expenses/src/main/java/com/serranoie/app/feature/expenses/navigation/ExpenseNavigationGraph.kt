package com.serranoie.app.feature.expenses.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.serranoie.app.core.navigation.Route
import com.serranoie.app.feature.expenses.AddExpenseScreen
import com.serranoie.app.feature.expenses.ExpenseDetailsScreen
import com.serranoie.app.feature.expenses.ExpensesScreen
import com.serranoie.app.feature.expenses.ExpensesUiState
import com.serranoie.app.feature.expenses.ExpensesViewModel
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.expensesGraph(navController: NavController, tripId: String) {
    composable(Route.Expenses.route) {
        val viewmodel: ExpensesViewModel = koinViewModel()
        val uiState by viewmodel.uiState.collectAsState()
        val userExpenseSummaries by viewmodel.userExpenseSummaries.collectAsState()
        val selectedExpense by viewmodel.selectedExpense.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(tripId) {
            viewmodel.fetchUserExpenseSummaries(tripId)
        }

        LaunchedEffect(uiState) {
            when (val currentState = uiState) {
                is ExpensesUiState.Error -> {
                    snackbarHostState.showSnackbar(currentState.message)
                }
                else -> { /* Handle other states if needed */ }
            }
        }

        ExpensesScreen(
            navController = navController,
            tripId = tripId,
            snackbarHostState = snackbarHostState,
            expenses = userExpenseSummaries,
            uiState = uiState,
            onRefresh = {
                viewmodel.fetchUserExpenseSummaries(tripId, forceRefresh = true)
            },
            onSwiped = {
                viewmodel.refreshData()
            },
            onExpenseClick = {
                navController.navigate(Route.ExpenseDetails.route)
            },
            onAddExpenseClick = {
                navController.navigate(Route.AddExpense.route)
            },
            currentUserId = viewmodel.getCurrentUserId()
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
