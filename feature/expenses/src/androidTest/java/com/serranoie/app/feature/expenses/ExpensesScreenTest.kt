package com.serranoie.app.feature.expenses

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.feature.expenses.domain.model.Expense
import com.serranoie.app.feature.expenses.domain.model.UserExpenseSummary
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class ExpensesScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showsEmptyState_whenNoExpenses() {
        composeRule.setContent {
            PreviewWrapper {
                ExpensesScreen(
                    navController = rememberNavController(),
                    tripId = "1",
                    uiState = ExpensesUiState.Success(emptyList<UserExpenseSummary>()),
                    expenses = emptyList(),
                    onRefresh = {},
                    onSwiped = {},
                    onExpenseClick = {},
                    onAddExpenseClick = {},
                    snackbarHostState = remember { SnackbarHostState() },
                    currentUserId = 1
                )
            }
        }

        composeRule.onNodeWithText("No expenses yet").assertIsDisplayed()
    }

    @Test
    fun showsContent_whenIdleWithData() {
        val today = LocalDate.now().toString()
        val expense = Expense(
            id = 1,
            tripId = 1,
            name = "Dinner",
            amount = 42.0,
            date = today,
            category = "Food",
            paidByUserId = 1,
            paymentMethod = "Cash",
            splitType = "Equal",
            notes = null,
            isCompleted = false,
            debtors = emptyList(),
            paidBy = null
        )
        val summaries = listOf(
            UserExpenseSummary(
                totalTripExpenses = 42.0,
                userAmountOwed = 0.0,
                userAmountToReceive = 0.0,
                userBalance = 0.0,
                expenses = listOf(expense)
            )
        )

        composeRule.setContent {
            PreviewWrapper {
                ExpensesScreen(
                    navController = rememberNavController(),
                    tripId = "1",
                    uiState = ExpensesUiState.Idle,
                    expenses = summaries,
                    onRefresh = {},
                    onSwiped = {},
                    onExpenseClick = {},
                    onAddExpenseClick = {},
                    snackbarHostState = remember { SnackbarHostState() },
                    currentUserId = 1
                )
            }
        }

        composeRule.onNodeWithText("History of expenses").assertIsDisplayed()
        composeRule.onNodeWithText("Dinner").assertIsDisplayed()
    }

    @Test
    fun showsContent_whenSuccessWithData() {
        val today = LocalDate.now().toString()
        val expense = Expense(
            id = 2,
            tripId = 1,
            name = "Taxi",
            amount = 12.0,
            date = today,
            category = "Transport",
            paidByUserId = 2,
            paymentMethod = "Cash",
            splitType = "Equal",
            notes = null,
            isCompleted = false,
            debtors = emptyList(),
            paidBy = null
        )
        val summaries = listOf(
            UserExpenseSummary(
                totalTripExpenses = 12.0,
                userAmountOwed = 0.0,
                userAmountToReceive = 0.0,
                userBalance = 0.0,
                expenses = listOf(expense)
            )
        )

        composeRule.setContent {
            PreviewWrapper {
                ExpensesScreen(
                    navController = rememberNavController(),
                    tripId = "1",
                    uiState = ExpensesUiState.Success(summaries),
                    expenses = summaries,
                    onRefresh = {},
                    onSwiped = {},
                    onExpenseClick = {},
                    onAddExpenseClick = {},
                    snackbarHostState = remember { SnackbarHostState() },
                    currentUserId = 2
                )
            }
        }

        composeRule.onNodeWithText("History of expenses").assertIsDisplayed()
        composeRule.onNodeWithText("Taxi").assertIsDisplayed()
    }

    @Test
    fun showsContent_whenLoadingWithExistingData() {
        val today = LocalDate.now().toString()
        val expense = Expense(
            id = 3,
            tripId = 1,
            name = "Hotel",
            amount = 100.0,
            date = today,
            category = "Accommodation",
            paidByUserId = 1,
            paymentMethod = "Card",
            splitType = "Equal",
            notes = null,
            isCompleted = false,
            debtors = emptyList(),
            paidBy = null
        )
        val summaries = listOf(
            UserExpenseSummary(
                totalTripExpenses = 100.0,
                userAmountOwed = 0.0,
                userAmountToReceive = 0.0,
                userBalance = 0.0,
                expenses = listOf(expense)
            )
        )

        composeRule.setContent {
            PreviewWrapper {
                ExpensesScreen(
                    navController = rememberNavController(),
                    tripId = "1",
                    uiState = ExpensesUiState.Loading,
                    expenses = summaries,
                    onRefresh = {},
                    onSwiped = {},
                    onExpenseClick = {},
                    onAddExpenseClick = {},
                    snackbarHostState = remember { SnackbarHostState() },
                    currentUserId = 1
                )
            }
        }

        composeRule.onNodeWithText("History of expenses").assertIsDisplayed()
        composeRule.onNodeWithText("Hotel").assertIsDisplayed()
    }

    @Test
    fun showsEmptyState_whenErrorAndNoData() {
        composeRule.setContent {
            PreviewWrapper {
                ExpensesScreen(
                    navController = rememberNavController(),
                    tripId = "1",
                    uiState = ExpensesUiState.Error("Network error"),
                    expenses = emptyList(),
                    onRefresh = {},
                    onSwiped = {},
                    onExpenseClick = {},
                    onAddExpenseClick = {},
                    snackbarHostState = remember { SnackbarHostState() },
                    currentUserId = 1
                )
            }
        }

        composeRule.onNodeWithText("No expenses yet").assertIsDisplayed()
    }
}