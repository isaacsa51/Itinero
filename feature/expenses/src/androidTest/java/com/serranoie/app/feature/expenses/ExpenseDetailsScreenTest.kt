package com.serranoie.app.feature.expenses

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import com.serranoie.app.feature.expenses.util.ExpenseCategory
import org.junit.Rule
import org.junit.Test

class ExpenseDetailsScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showsSkeleton_whenLoading() {
        composeRule.setContent {
            PreviewWrapper {
                ExpenseDetailsScreen(
                    navController = rememberNavController(),
                    expenseState = ExpenseDetailsViewModel.ExpenseState(
                        name = "",
                        amount = "0",
                        date = "2025-01-01",
                        category = ExpenseCategory.FOOD,
                        paidBy = null,
                        paidByUserId = null,
                        paymentMethod = "Cash",
                        notes = null,
                        extraInfo = null,
                        nameError = null,
                        amountError = null,
                        isSaving = false
                    ),
                    formUiState = ExpenseDetailsViewModel.UIState(),
                    splitType = SplitType.EQUAL,
                    groupMembers = emptyList(),
                    persons = emptyList(),
                    paymentMethods = emptyList(),
                    selectedExpense = null,
                    currentUserId = 1,
                    viewModel = null,
                    isLoading = true,
                )
            }
        }

        composeRule.onNodeWithText("Loading expense...").assertIsDisplayed()
    }

    @Test
    fun showsContent_whenSelectedExpenseProvided() {
        val expense = com.serranoie.app.feature.expenses.domain.model.Expense(
            id = 10,
            tripId = 1,
            name = "Brunch",
            amount = 30.0,
            date = "2025-01-02",
            category = "Food",
            paidByUserId = 1,
            paymentMethod = "Cash",
            splitType = "Equal",
            notes = "Nice place",
            isCompleted = false,
            debtors = emptyList(),
            paidBy = null
        )

        composeRule.setContent {
            PreviewWrapper {
                ExpenseDetailsScreen(
                    navController = rememberNavController(),
                    expenseState = ExpenseDetailsViewModel.ExpenseState(
                        name = expense.name,
                        amount = expense.amount.toString(),
                        date = expense.date,
                        category = com.serranoie.app.feature.expenses.util.ExpenseCategory.FOOD,
                        paidBy = "Alice",
                        paidByUserId = 1,
                        paymentMethod = "Cash",
                        notes = expense.notes,
                        extraInfo = null,
                        nameError = null,
                        amountError = null,
                        isSaving = false
                    ),
                    formUiState = ExpenseDetailsViewModel.UIState(),
                    splitType = SplitType.EQUAL,
                    groupMembers = emptyList(),
                    persons = emptyList(),
                    paymentMethods = emptyList(),
                    selectedExpense = expense,
                    currentUserId = 1,
                    viewModel = null,
                    isLoading = false,
                )
            }
        }

        composeRule.onNodeWithText("Who owes").assertIsDisplayed()
        composeRule.onNodeWithText("Paid by").assertIsDisplayed()
        composeRule.onNodeWithText("Split type").assertIsDisplayed()
    }
}