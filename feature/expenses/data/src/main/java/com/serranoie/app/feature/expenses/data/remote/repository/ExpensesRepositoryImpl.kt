/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ITINERO - ExpRepoImpl.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.data.remote.repository

import android.util.Log
import com.serranoie.app.feature.expenses.data.local.repository.LocalExpensesRepository
import com.serranoie.app.feature.expenses.data.mappers.toDomain
import com.serranoie.app.feature.expenses.data.mappers.toDto
import com.serranoie.app.feature.expenses.data.remote.ExpensesApi
import com.serranoie.app.feature.expenses.domain.model.CreateExpense
import com.serranoie.app.feature.expenses.domain.model.Expense
import com.serranoie.app.feature.expenses.domain.model.UserExpenseSummary
import com.serranoie.app.feature.expenses.domain.repository.ExpensesRepository
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.result.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow

class ExpensesRepositoryImpl(
    private val api: ExpensesApi,
    private val localRepository: LocalExpensesRepository
) : ExpensesRepository {

    override suspend fun createExpense(groupCode: String, expense: CreateExpense): Result<Expense> {
        return safeApiCall {
            val expenseDto = expense.toDto()
            val result = api.createExpense(groupCode, expenseDto)

            when (result) {
                is Result.Success -> {
                    val createdExpense = result.data.toDomain()
                    localRepository.cacheExpense(createdExpense)
                    Log.d("ITINERO - ExpRepo", "Created and cached expense: ${createdExpense.id}")
                    createdExpense
                }

                is Result.Error -> throw result.exception
            }
        }
    }

    override suspend fun getAllTripExpenses(groupCode: String): Result<List<Expense>> {
        return safeApiCall {
            val cachedResult = localRepository.getAllCachedExpenses(groupCode)
            if (cachedResult is Result.Success && cachedResult.data.isNotEmpty()) {
                Log.d("ITINERO - ExpRepo", "Returning cached expenses for group: $groupCode")
                return@safeApiCall cachedResult.data
            }

            val result = api.getAllTripExpenses(groupCode)
            when (result) {
                is Result.Success -> {
                    val expenses = result.data.map { it.toDomain() }
                    localRepository.cacheExpenses(groupCode, expenses)
                    Log.d("ITINERO - ExpRepo", "Fetched and cached ${expenses.size} expenses")
                    expenses
                }

                is Result.Error -> {
                    if (cachedResult is Result.Success) {
                        Log.w("ITINERO - ExpRepo", "API failed, returning cached expenses")
                        cachedResult.data
                    } else {
                        throw result.exception
                    }
                }
            }
        }
    }

    override suspend fun getUserExpenseSummary(groupCode: String): Flow<List<UserExpenseSummary>> =
        flow {
            val cachedResult = localRepository.getCachedUserExpenseSummary(groupCode)
            if (cachedResult is Result.Success && cachedResult.data != null) {
                emit(listOf(cachedResult.data!!))
            }

        try {
            val result = api.getUserExpenseSummary(groupCode)
            when (result) {
                is Result.Success -> {
                    val summary = result.data.toDomain()
                    localRepository.cacheUserExpenseSummary(groupCode, summary)
                    Log.d("ITINERO - ExpRepo", "Fetched and cached expense summary")
                    emit(listOf(summary))
                }

                is Result.Error -> {
                    if (cachedResult is Result.Success && cachedResult.data != null) {
                        Log.w("ITINERO - ExpRepo", "API failed, returning cached summary")
                        emit(listOf(cachedResult.data!!))
                    } else {
                        emit(emptyList())
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ITINERO - ExpRepo", "Failed to fetch expense summary", e)
            emit(emptyList())
        }
    }.catch { throwable ->
        Log.e("ITINERO - ExpRepo", "Error in getUserExpenseSummary", throwable)
        emit(emptyList())
    }

    override suspend fun getExpenseById(groupCode: String, expenseId: String): Result<Expense> {
        return safeApiCall {
            val cachedResult = localRepository.getCachedExpenseById(expenseId)
            if (cachedResult is Result.Success && cachedResult.data != null) {
                Log.d("ITINERO - ExpRepo", "Returning cached expense: $expenseId")
                return@safeApiCall cachedResult.data!!
            }

            val result = api.getExpenseById(groupCode, expenseId)
            when (result) {
                is Result.Success -> {
                    val expense = result.data.toDomain()
                    localRepository.cacheExpense(expense)
                    Log.d("ITINERO - ExpRepo", "Fetched and cached expense: $expenseId")
                    expense
                }
                is Result.Error -> {
                    if (cachedResult is Result.Success && cachedResult.data != null) {
                        Log.w("ITINERO - ExpRepo", "API failed, returning cached expense")
                        cachedResult.data!!
                    } else {
                        throw result.exception
                    }
                }
            }
        }
    }

    override suspend fun updateExpense(
        groupCode: String,
        expenseId: String,
        expense: CreateExpense
    ): Result<Unit> {
        return safeApiCall {
            val expenseDto = expense.toDto()
            val result = api.updateExpense(groupCode, expenseId, expenseDto)

            when (result) {
                is Result.Success -> {
                    val updatedExpense = result.data.toDomain()
                    localRepository.updateExpense(updatedExpense)
                    Log.d("ITINERO - ExpRepo", "Updated and cached expense: $expenseId")
                    Unit
                }

                is Result.Error -> throw result.exception
            }
        }
    }

    override suspend fun deleteExpense(groupCode: String, expenseId: String): Result<Unit> {
        return safeApiCall {
            val result = api.deleteExpense(groupCode, expenseId)
            when (result) {
                is Result.Success -> {
                    localRepository.deleteExpenseById(expenseId)
                    Log.d("ITINERO - ExpRepo", "Deleted expense from API and cache: $expenseId")
                }
                is Result.Error -> throw result.exception
            }
        }
    }

    override suspend fun markExpenseCompleted(groupCode: String, expenseId: String): Result<Unit> {
        return safeApiCall {
            val result = api.markExpenseCompleted(groupCode, expenseId)
            when (result) {
                is Result.Success -> {
                    val expenseResult = getExpenseById(groupCode, expenseId)
                    if (expenseResult is Result.Success) {
                        Log.d("ITINERO - ExpRepo", "Marked expense as completed: $expenseId")
                    }
                }

                is Result.Error -> throw result.exception
            }
        }
    }

    override fun getAllTripExpensesFlow(groupCode: String): Flow<List<Expense>> =
        flow<List<Expense>> {
        val cachedResult = localRepository.getAllCachedExpenses(groupCode)
        if (cachedResult is Result.Success) {
            emit(cachedResult.data)
        }

        try {
            val freshResult = getAllTripExpenses(groupCode)
            if (freshResult is Result.Success) {
                emit(freshResult.data)
            }
        } catch (e: Exception) {
            Log.e("ITINERO - ExpRepo", "Failed to fetch fresh expenses", e)
        }
    }.catch { throwable ->
        Log.e("ITINERO - ExpRepo", "Error in getAllTripExpensesFlow", throwable)
        emit(emptyList())
    }

    override fun getUserExpenseSummaryFlow(groupCode: String): Flow<UserExpenseSummary> =
        localRepository.getCachedUserExpenseSummaryFlow(groupCode).filterNotNull()

    override fun getExpenseByIdFlow(groupCode: String, expenseId: String): Flow<Expense> =
        localRepository.getCachedExpenseFlow(expenseId).filterNotNull()

    override suspend fun clearCache(): Result<Unit> {
        return localRepository.clearAllExpenses()
    }

    override suspend fun refreshExpenses(groupCode: String): Result<Unit> {
        return safeApiCall {
            val expensesResult = api.getAllTripExpenses(groupCode)
            val summaryResult = api.getUserExpenseSummary(groupCode)

            when {
                expensesResult is Result.Success && summaryResult is Result.Success -> {

                    val expenses = expensesResult.data.map { it.toDomain() }
                    val summary = summaryResult.data.toDomain()

                    localRepository.clearExpensesForGroup(groupCode)
                    localRepository.cacheExpenses(groupCode, expenses)
                    localRepository.cacheUserExpenseSummary(groupCode, summary)

                    Log.d("ITINERO - ExpRepo", "Force refreshed expenses for group: $groupCode")
                }

                expensesResult is Result.Error -> throw expensesResult.exception
                summaryResult is Result.Error -> throw summaryResult.exception
            }
        }
    }
}