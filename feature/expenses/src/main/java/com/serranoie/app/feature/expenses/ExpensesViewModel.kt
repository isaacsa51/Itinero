/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ExpensesViewModel.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 04 julio 2025
 */

package com.serranoie.app.feature.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranoie.app.feature.expenses.domain.model.CreateExpense
import com.serranoie.app.feature.expenses.domain.model.Expense
import com.serranoie.app.feature.expenses.domain.model.UserExpenseSummary
import com.serranoie.app.feature.expenses.domain.usecase.ExpensesUseCases
import com.serranoie.itinero.core.domain.repository.AuthPreferencesRepository
import com.serranoie.itinero.core.domain.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

sealed interface ExpensesUiState {
    data object Idle : ExpensesUiState
    data object Loading : ExpensesUiState
    data class Success<T>(val data: T) : ExpensesUiState
    data class Error(val message: String) : ExpensesUiState
}

class ExpensesViewModel(
    private val expensesUseCases: ExpensesUseCases,
    private val authPreferencesRepository: AuthPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExpensesUiState>(ExpensesUiState.Idle)
    val uiState: StateFlow<ExpensesUiState> = _uiState.asStateFlow()

    private val _userExpenseSummaries = MutableStateFlow<List<UserExpenseSummary>>(emptyList())
    val userExpenseSummaries: StateFlow<List<UserExpenseSummary>> =
        _userExpenseSummaries.asStateFlow()

    private val _selectedExpense = MutableStateFlow<Expense?>(null)
    val selectedExpense: StateFlow<Expense?> = _selectedExpense.asStateFlow()

    private var currentGroupCode: String = ""

    fun fetchUserExpenseSummaries(groupCode: String, forceRefresh: Boolean = false) {
        currentGroupCode = groupCode
        viewModelScope.launch(Dispatchers.IO) {
            try {
                expensesUseCases.getUserExpensesUseCase(groupCode)
                    .onStart { _uiState.value = ExpensesUiState.Loading }
                    .catch { error ->
                        _uiState.value = ExpensesUiState.Error(
                            error.message ?: "Failed to fetch user expenses"
                        )
                    }
                    .collect { summaries ->
                        val processedSummaries = summaries.map { summary ->
                            summary.copy(
                                expenses = summary.expenses.map { expense ->
                                    expense.copy(isCompleted = isExpenseCompleted(expense))
                                }
                            )
                        }
                        _userExpenseSummaries.value = processedSummaries
                        _uiState.value = ExpensesUiState.Success(processedSummaries)
                    }
            } catch (e: Exception) {
                _uiState.value = ExpensesUiState.Error(
                    e.message ?: "Failed to fetch user expenses"
                )
            }
        }
    }

    private fun isExpenseCompleted(expense: Expense): Boolean {
        return expense.debtors.isNotEmpty() && expense.debtors.all { debtor ->
            debtor.hasPaid
        }
    }

    fun getExpenseById(groupCode: String, expenseId: String, forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ExpensesUiState.Loading
            when (val result = expensesUseCases.getExpenseByIdUseCase(groupCode, expenseId)) {
                is Result.Success -> {
                    val processedExpense =
                        result.data.copy(isCompleted = isExpenseCompleted(result.data))
                    _selectedExpense.value = processedExpense
                    _uiState.value = ExpensesUiState.Success(processedExpense)
                }
                is Result.Error -> {
                    _uiState.value = ExpensesUiState.Error(
                        result.exception.message ?: "Failed to fetch expense"
                    )
                }
            }
        }
    }

    fun createExpense(groupCode: String, expense: CreateExpense) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ExpensesUiState.Loading
            when (val result = expensesUseCases.addExpenseUseCase(groupCode, expense)) {
                is Result.Success -> {
                    val processedExpense =
                        result.data.copy(isCompleted = isExpenseCompleted(result.data))
                    _uiState.value = ExpensesUiState.Success(processedExpense)
                    if (currentGroupCode.isNotEmpty()) {
                        fetchUserExpenseSummaries(currentGroupCode, forceRefresh = true)
                    }
                }
                is Result.Error -> {
                    _uiState.value = ExpensesUiState.Error(
                        result.exception.message ?: "Failed to create expense"
                    )
                }
            }
        }
    }

    fun updateExpense(groupCode: String, expenseId: String, expense: CreateExpense) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ExpensesUiState.Loading
            when (val result =
                expensesUseCases.updateExpenseUseCase(groupCode, expenseId, expense)) {
                is Result.Success -> {
                    _uiState.value = ExpensesUiState.Success(Unit)
                    if (currentGroupCode.isNotEmpty()) {
                        fetchUserExpenseSummaries(currentGroupCode, forceRefresh = true)
                    }
                }
                is Result.Error -> {
                    _uiState.value = ExpensesUiState.Error(
                        result.exception.message ?: "Failed to update expense"
                    )
                }
            }
        }
    }

    fun deleteExpense(groupCode: String, expenseId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ExpensesUiState.Loading
            when (val result = expensesUseCases.deleteExpenseUseCase(groupCode, expenseId)) {
                is Result.Success -> {
                    _uiState.value = ExpensesUiState.Success(Unit)
                    _userExpenseSummaries.value = _userExpenseSummaries.value.map { summary ->
                        summary.copy(
                            expenses = summary.expenses.filter { it.id.toString() != expenseId }
                                .map { expense ->
                                    expense.copy(isCompleted = isExpenseCompleted(expense))
                                }
                        )
                    }
                    if (_selectedExpense.value?.id.toString() == expenseId) {
                        _selectedExpense.value = null
                    }
                    if (currentGroupCode.isNotEmpty()) {
                        fetchUserExpenseSummaries(currentGroupCode, forceRefresh = true)
                    }
                }
                is Result.Error -> {
                    _uiState.value = ExpensesUiState.Error(
                        result.exception.message ?: "Failed to delete expense"
                    )
                }
            }
        }
    }

    fun clearSelectedExpense() {
        _selectedExpense.value = null
    }

    fun resetState() {
        _uiState.value = ExpensesUiState.Idle
    }

    fun refreshData() {
        if (currentGroupCode.isNotEmpty()) {
            fetchUserExpenseSummaries(currentGroupCode, forceRefresh = true)
        }
    }

    fun getCurrentUserId(): Int {
        return authPreferencesRepository.getUserId() ?: 1
    }
}