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
    suspend fun createExpense(groupCode: String, expense: CreateExpense): Result<Expense>
    suspend fun getAllTripExpenses(groupCode: String): Result<List<Expense>>
    suspend fun getUserExpenseSummary(groupCode: String): Flow<List<UserExpenseSummary>>
    suspend fun getExpenseById(groupCode: String, expenseId: String): Result<Expense>
    suspend fun updateExpense(
        groupCode: String,
        expenseId: String,
        expense: CreateExpense
    ): Result<Unit>

    suspend fun deleteExpense(groupCode: String, expenseId: String): Result<Unit>
    suspend fun markExpenseCompleted(groupCode: String, expenseId: String): Result<Unit>

    fun getAllTripExpensesFlow(groupCode: String): Flow<List<Expense>>
    fun getUserExpenseSummaryFlow(groupCode: String): Flow<UserExpenseSummary>
    fun getExpenseByIdFlow(groupCode: String, expenseId: String): Flow<Expense>

    suspend fun clearCache(): Result<Unit>
    suspend fun refreshExpenses(groupCode: String): Result<Unit>
}