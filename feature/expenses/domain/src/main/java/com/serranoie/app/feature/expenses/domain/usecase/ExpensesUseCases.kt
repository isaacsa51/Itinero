/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: GetUserExpensesUseCase.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.domain.usecase

import com.serranoie.app.feature.expenses.domain.model.CreateExpense
import com.serranoie.app.feature.expenses.domain.model.Expense
import com.serranoie.app.feature.expenses.domain.model.UserExpenseSummary
import com.serranoie.app.feature.expenses.domain.repository.ExpensesRepository
import com.serranoie.itinero.core.domain.result.Result
import kotlinx.coroutines.flow.Flow

data class ExpensesUseCases(
    val getUserExpensesUseCase: GetUserExpensesUseCase,
    val getExpenseByIdUseCase: GetExpenseByIdUseCase,
    val updateExpenseUseCase: UpdateExpenseUseCase,
    val deleteExpenseUseCase: DeleteExpenseUseCase,
    val addExpenseUseCase: AddExpenseUseCase,
)

class GetUserExpensesUseCase(private val repository: ExpensesRepository) {
    suspend operator fun invoke(groupCode: String): Flow<List<UserExpenseSummary>> {
        return repository.getUserExpenseSummary(groupCode)
    }
}

class GetExpenseByIdUseCase(private val repository: ExpensesRepository) {
    suspend operator fun invoke(groupCode: String, expenseId: String): Result<Expense> {
        return repository.getExpenseById(groupCode, expenseId)
    }
}

class UpdateExpenseUseCase(private val repository: ExpensesRepository) {
    suspend operator fun invoke(groupCode: String, expenseId: String, expense: CreateExpense): Result<Unit> {
        return repository.updateExpense(groupCode, expenseId, expense)
    }
}

class DeleteExpenseUseCase(private val repository: ExpensesRepository) {
    suspend operator fun invoke(groupCode: String, expenseId: String): Result<Unit> {
        return repository.deleteExpense(groupCode, expenseId)
    }
}

class AddExpenseUseCase(private val repository: ExpensesRepository) {
    suspend operator fun invoke(groupCode: String, expense: CreateExpense): Result<Expense> {
        return repository.createExpense(groupCode, expense)
    }
}