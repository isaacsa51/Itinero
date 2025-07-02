/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ExpensesRepository.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.domain.repository

import com.serranoie.app.feature.expenses.domain.model.CreateExpense
import com.serranoie.app.feature.expenses.domain.model.Expense
import com.serranoie.app.feature.expenses.domain.model.UserExpenseSummary
import com.serranoie.itinero.core.domain.result.Result
import kotlinx.coroutines.flow.Flow

interface ExpensesRepository {
    suspend fun addExpense(expense: CreateExpense): Result<CreateExpense>

    suspend fun deleteExpense(): Result<Unit>

    suspend fun updateExpense(expense: Expense): Flow<Result<Expense>>

    fun getUserExpenses(groupCode: String): Flow<List<UserExpenseSummary>>

    fun getExpenseById(groupCode: String, id: String): Result<Expense>
}