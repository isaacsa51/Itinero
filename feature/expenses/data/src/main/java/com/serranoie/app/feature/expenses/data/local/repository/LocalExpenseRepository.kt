/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: LocalExpenseRepository.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.data.local.repository

import com.serranoie.app.feature.expenses.data.local.dao.ExpenseDao
import com.serranoie.app.feature.expenses.data.mappers.toDomain
import com.serranoie.app.feature.expenses.data.mappers.toEntity
import com.serranoie.app.feature.expenses.domain.model.Expense
import com.serranoie.app.feature.expenses.domain.model.UserExpenseSummary
import com.serranoie.itinero.core.domain.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LocalExpensesRepository {
    suspend fun getAllCachedExpenses(groupCode: String): Result<List<Expense>>
    suspend fun getCachedExpenseById(expenseId: String): Result<Expense?>
    suspend fun getCachedUserExpenseSummary(groupCode: String): Result<UserExpenseSummary?>
    suspend fun cacheExpense(expense: Expense): Result<Unit>
    suspend fun cacheExpenses(groupCode: String, expenses: List<Expense>): Result<Unit>
    suspend fun cacheUserExpenseSummary(
        groupCode: String,
        summary: UserExpenseSummary
    ): Result<Unit>
    suspend fun updateExpense(expense: Expense): Result<Unit>
    suspend fun deleteExpenseById(expenseId: String): Result<Unit>
    suspend fun clearExpensesForGroup(groupCode: String): Result<Unit>
    suspend fun clearAllExpenses(): Result<Unit>

    fun getCachedExpensesFlow(groupCode: String): Flow<List<Expense>>
    fun getCachedUserExpenseSummaryFlow(groupCode: String): Flow<UserExpenseSummary?>
    fun getCachedExpenseFlow(expenseId: String): Flow<Expense?>
}

class LocalExpensesRepositoryImpl(
    private val expenseDao: ExpenseDao
) : LocalExpensesRepository {

    override suspend fun getAllCachedExpenses(groupCode: String): Result<List<Expense>> {
        return try {
            val expenseEntities = expenseDao.getExpensesByGroupCode(groupCode)
            val expenses = expenseEntities.map { expenseEntity ->
                val debtors = expenseDao.getDebtorsByExpenseId(expenseEntity.id)
                expenseEntity.toDomain(debtors)
            }
            Result.Success(expenses)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getCachedExpenseById(expenseId: String): Result<Expense?> {
        return try {
            val expenseEntity = expenseDao.getExpenseById(expenseId.toInt())
            if (expenseEntity != null) {
                val debtors = expenseDao.getDebtorsByExpenseId(expenseEntity.id)
                val expense = expenseEntity.toDomain(debtors)
                Result.Success(expense)
            } else {
                Result.Success(null)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getCachedUserExpenseSummary(groupCode: String): Result<UserExpenseSummary?> {
        return try {
            val summaryEntity =
                expenseDao.getUserExpenseSummariesByGroupCode(groupCode).firstOrNull()
            if (summaryEntity != null) {
                // Get cached expenses for this group
                val expenses = getAllCachedExpenses(groupCode)
                when (expenses) {
                    is Result.Success -> {
                        val summary = UserExpenseSummary(
                            totalTripExpenses = summaryEntity.totalExpenses,
                            userAmountOwed = summaryEntity.totalOwed,
                            userAmountToReceive = summaryEntity.totalPaid,
                            userBalance = summaryEntity.totalPaid - summaryEntity.totalOwed,
                            expenses = expenses.data
                        )
                        Result.Success(summary)
                    }

                    is Result.Error -> Result.Error(expenses.exception)
                }
            } else {
                Result.Success(null)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun cacheExpense(expense: Expense): Result<Unit> {
        return try {
            val expenseEntity = expense.toEntity()
            val debtorEntities = expense.debtors.map { it.toEntity(expense.id) }

            expenseDao.insertExpenseWithDebtors(expenseEntity, debtorEntities)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun cacheExpenses(groupCode: String, expenses: List<Expense>): Result<Unit> {
        return try {
            expenses.forEach { expense ->
                cacheExpense(expense)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun cacheUserExpenseSummary(
        groupCode: String,
        summary: UserExpenseSummary
    ): Result<Unit> {
        return try {
            val summaryEntity = summary.toEntity(groupCode)
            expenseDao.insertUserExpenseSummary(summaryEntity)

            // Also cache the expenses
            cacheExpenses(groupCode, summary.expenses)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateExpense(expense: Expense): Result<Unit> {
        return try {
            val expenseEntity = expense.toEntity()
            val debtorEntities = expense.debtors.map { it.toEntity(expense.id) }

            expenseDao.updateExpenseWithDebtors(expenseEntity, debtorEntities)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteExpenseById(expenseId: String): Result<Unit> {
        return try {
            expenseDao.deleteExpenseWithDebtors(expenseId.toInt())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun clearExpensesForGroup(groupCode: String): Result<Unit> {
        return try {
            expenseDao.deleteAllExpenseDataForGroup(groupCode)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun clearAllExpenses(): Result<Unit> {
        return try {
            expenseDao.deleteAllExpenseData()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getCachedExpensesFlow(groupCode: String): Flow<List<Expense>> {
        return expenseDao.getExpensesByGroupCodeFlow(groupCode)
            .map { expenseEntities ->
                expenseEntities.map { expenseEntity ->
                    val debtors = expenseDao.getDebtorsByExpenseId(expenseEntity.id)
                    expenseEntity.toDomain(debtors)
                }
            }
    }

    override fun getCachedUserExpenseSummaryFlow(groupCode: String): Flow<UserExpenseSummary?> {
        return expenseDao.getUserExpenseSummariesByGroupCodeFlow(groupCode)
            .map { summaryEntities ->
                val summaryEntity = summaryEntities.firstOrNull()
                if (summaryEntity != null) {
                    // Get cached expenses for this group
                    val expenseEntities = expenseDao.getExpensesByGroupCode(groupCode)
                    val expenses = expenseEntities.map { expenseEntity ->
                        val debtors = expenseDao.getDebtorsByExpenseId(expenseEntity.id)
                        expenseEntity.toDomain(debtors)
                    }

                    UserExpenseSummary(
                        totalTripExpenses = summaryEntity.totalExpenses,
                        userAmountOwed = summaryEntity.totalOwed,
                        userAmountToReceive = summaryEntity.totalPaid,
                        userBalance = summaryEntity.totalPaid - summaryEntity.totalOwed,
                        expenses = expenses
                    )
                } else null
            }
    }

    override fun getCachedExpenseFlow(expenseId: String): Flow<Expense?> {
        return expenseDao.getExpenseByIdFlow(expenseId.toInt())
            .map { expenseEntity ->
                expenseEntity?.let { entity ->
                    val debtors = expenseDao.getDebtorsByExpenseId(entity.id)
                    entity.toDomain(debtors)
                }
            }
    }
}