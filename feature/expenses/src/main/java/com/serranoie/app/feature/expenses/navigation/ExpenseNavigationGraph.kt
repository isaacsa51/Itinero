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
import com.serranoie.app.feature.expenses.ExpenseDetailsViewModel
import com.serranoie.app.feature.expenses.ExpensesScreen
import com.serranoie.app.feature.expenses.ExpensesUiState
import com.serranoie.app.feature.expenses.ExpensesViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

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

        LaunchedEffect(navController) {
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<String>("expense_saved_message")
                ?.observeForever { message ->
                    if (message != null) {
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
                            .launch {
                            snackbarHostState.showSnackbar(message)
                        }
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.remove<String>("expense_saved_message")
                    }
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
        val viewModel: ExpenseDetailsViewModel = koinViewModel { parametersOf(tripId) }

        val expenseState by viewModel.expenseState.collectAsState()
        val formUiState by viewModel.formUiState.collectAsState()
        val splitType by viewModel.splitType.collectAsState()
        val groupMembers by viewModel.groupMembers.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }

        AddExpenseScreen(
            navController = navController,
            expenseState = expenseState,
            formUiState = formUiState,
            splitType = splitType,
            groupMembers = groupMembers,
            persons = viewModel.persons,
            snackbarHostState = snackbarHostState,
            onExpenseNameChange = viewModel::updateExpenseName,
            onAmountChange = viewModel::updateAmount,
            onShowDatePicker = { show -> viewModel.toggleDatePicker(show) },
            onCategoryChange = viewModel::updateCategory,
            onShowCategoryDropdownChange = viewModel::toggleCategoryDropdown,
            onPaidByChange = viewModel::updatePaidBy,
            onPaymentMethodChange = viewModel::updatePaymentMethod,
            onSplitTypeChange = viewModel::updateSplitType,
            onToggleMemberIncluded = viewModel::toggleMemberIncluded,
            onUpdateMemberPercentage = viewModel::updateMemberPercentage,
            onUpdateMemberAmount = viewModel::updateMemberAmount,
            onNotesChange = viewModel::updateNotes,
            onSaveExpense = viewModel::createExpense,
            onClearErrorMessage = viewModel::clearErrorMessage,
            onClearSuccessMessage = viewModel::clearSuccessMessage,
            onDateSelected = viewModel::updateDate,
            isPercentageValid = viewModel.isPercentageValid(),
            isManualAmountValid = viewModel.isManualAmountValid()
        )
    }

    composable(Route.ExpenseDetails.route) {
        val viewModel: ExpenseDetailsViewModel = koinViewModel { parametersOf(tripId) }

        val expenseState by viewModel.expenseState.collectAsState()
        val formUiState by viewModel.formUiState.collectAsState()
        val splitType by viewModel.splitType.collectAsState()
        val groupMembers by viewModel.groupMembers.collectAsState()

        ExpenseDetailsScreen(
            navController = navController,
            expenseState = expenseState,
            formUiState = formUiState,
            splitType = splitType,
            groupMembers = groupMembers,
            persons = viewModel.persons,
            paymentMethods = viewModel.paymentMethods,
            onExpenseNameChange = viewModel::updateExpenseName,
            onAmountChange = viewModel::updateAmount,
            onDateChange = viewModel::updateDate,
            onShowDatePicker = { show -> viewModel.toggleDatePicker(show) },
            onCategoryChange = viewModel::updateCategory,
            onShowCategoryDropdownChange = viewModel::toggleCategoryDropdown,
            onPaidByChange = viewModel::updatePaidBy,
            onShowPersonsDropdownChange = viewModel::togglePersonsDropdown,
            onPaymentMethodChange = viewModel::updatePaymentMethod,
            onSplitTypeChange = viewModel::updateSplitType,
            onToggleMemberIncluded = viewModel::toggleMemberIncluded,
            onUpdateMemberPercentage = viewModel::updateMemberPercentage,
            onUpdateMemberAmount = viewModel::updateMemberAmount,
            onNotesChange = viewModel::updateNotes,
            onSaveExpense = viewModel::createExpense,
            onClearErrorMessage = viewModel::clearErrorMessage,
            onDateSelected = viewModel::updateDate,
            isPercentageValid = viewModel.isPercentageValid(),
            isManualAmountValid = viewModel.isManualAmountValid()
        )
    }
}
