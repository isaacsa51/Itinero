package com.serranoie.app.feature.expenses

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.serranoie.app.designsystemlib.ui.PreviewWrapper
import org.junit.Rule
import org.junit.Test

class AddExpenseScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showsDefaultTitle_newExpense() {
        composeRule.setContent {
            PreviewWrapper {
                AddExpenseScreen(
                    navController = rememberNavController(),
                    expenseState = ExpenseDetailsViewModel.ExpenseState(
                        notes = null,
                        extraInfo = null
                    ),
                    formUiState = ExpenseDetailsViewModel.UIState(),
                    splitType = SplitType.EQUAL,
                    groupMembers = emptyList(),
                    persons = emptyList(),
                    snackbarHostState = remember { SnackbarHostState() },
                    onExpenseNameChange = {},
                    onAmountChange = {},
                    onShowDatePicker = {},
                    onCategoryChange = {},
                    onShowCategoryDropdownChange = {},
                    onPaidByChange = {},
                    onPaymentMethodChange = {},
                    onSplitTypeChange = {},
                    onToggleMemberIncluded = { _, _ -> },
                    onUpdateMemberPercentage = { _, _ -> },
                    onUpdateMemberAmount = { _, _ -> },
                    onNotesChange = {},
                    onExtraInfoChange = {},
                    onSaveExpense = {},
                    onClearErrorMessage = {},
                    onClearSuccessMessage = {},
                    onDateSelected = {},
                    isPercentageValid = true,
                    isManualAmountValid = true
                )
            }
        }

        composeRule.onNodeWithText("New expense").assertIsDisplayed()
        composeRule.onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun showsErrorDialog_whenErrorMessageProvided() {
        composeRule.setContent {
            PreviewWrapper {
                AddExpenseScreen(
                    navController = rememberNavController(),
                    expenseState = ExpenseDetailsViewModel.ExpenseState(
                        notes = null,
                        extraInfo = null
                    ),
                    formUiState = ExpenseDetailsViewModel.UIState(errorMessage = "Failed"),
                    splitType = SplitType.EQUAL,
                    groupMembers = emptyList(),
                    persons = emptyList(),
                    snackbarHostState = remember { SnackbarHostState() },
                    onExpenseNameChange = {}, onAmountChange = {}, onShowDatePicker = {},
                    onCategoryChange = {}, onShowCategoryDropdownChange = {},
                    onPaidByChange = {}, onPaymentMethodChange = {}, onSplitTypeChange = {},
                    onToggleMemberIncluded = { _, _ -> }, onUpdateMemberPercentage = { _, _ -> },
                    onUpdateMemberAmount = { _, _ -> }, onNotesChange = {}, onExtraInfoChange = {},
                    onSaveExpense = {}, onClearErrorMessage = {}, onClearSuccessMessage = {},
                    onDateSelected = {}, isPercentageValid = true, isManualAmountValid = true
                )
            }
        }

        composeRule.onNodeWithText("Error").assertIsDisplayed()
        composeRule.onNodeWithText("Failed").assertIsDisplayed()
        composeRule.onNodeWithText("OK").assertIsDisplayed()
    }

    @Test
    fun showsDatePicker_whenFlagIsTrue() {
        composeRule.setContent {
            PreviewWrapper {
                AddExpenseScreen(
                    navController = rememberNavController(),
                    expenseState = ExpenseDetailsViewModel.ExpenseState(
                        notes = null,
                        extraInfo = null
                    ),
                    formUiState = ExpenseDetailsViewModel.UIState(showDatePicker = true),
                    splitType = SplitType.EQUAL,
                    groupMembers = emptyList(),
                    persons = emptyList(),
                    snackbarHostState = remember { SnackbarHostState() },
                    onExpenseNameChange = {}, onAmountChange = {}, onShowDatePicker = {},
                    onCategoryChange = {}, onShowCategoryDropdownChange = {},
                    onPaidByChange = {}, onPaymentMethodChange = {}, onSplitTypeChange = {},
                    onToggleMemberIncluded = { _, _ -> }, onUpdateMemberPercentage = { _, _ -> },
                    onUpdateMemberAmount = { _, _ -> }, onNotesChange = {}, onExtraInfoChange = {},
                    onSaveExpense = {}, onClearErrorMessage = {}, onClearSuccessMessage = {},
                    onDateSelected = {}, isPercentageValid = true, isManualAmountValid = true
                )
            }
        }

        composeRule.onNodeWithText("Select Date").assertIsDisplayed()
    }
}