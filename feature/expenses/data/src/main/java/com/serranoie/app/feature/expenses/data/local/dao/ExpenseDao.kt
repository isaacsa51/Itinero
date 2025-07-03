/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ExpenseDao.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.serranoie.app.feature.expenses.data.local.entity.ExpenseDebtorEntity
import com.serranoie.app.feature.expenses.data.local.entity.ExpenseEntity
import com.serranoie.app.feature.expenses.data.local.entity.UserBalanceEntity
import com.serranoie.app.feature.expenses.data.local.entity.UserExpenseSummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    // Expense operations
    @Query("SELECT * FROM expenses WHERE groupCode = :groupCode ORDER BY date DESC")
    suspend fun getExpensesByGroupCode(groupCode: String): List<ExpenseEntity>

    @Query("SELECT * FROM expenses WHERE groupCode = :groupCode ORDER BY date DESC")
    fun getExpensesByGroupCodeFlow(groupCode: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    suspend fun getExpenseById(expenseId: Int): ExpenseEntity?

    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    fun getExpenseByIdFlow(expenseId: Int): Flow<ExpenseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseEntity>)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteExpenseById(expenseId: Int)

    @Query("DELETE FROM expenses WHERE groupCode = :groupCode")
    suspend fun deleteExpensesByGroupCode(groupCode: String)

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()

    // Expense Debtor operations
    @Query("SELECT * FROM expense_debtors WHERE expenseId = :expenseId")
    suspend fun getDebtorsByExpenseId(expenseId: Int): List<ExpenseDebtorEntity>

    @Query("SELECT * FROM expense_debtors WHERE expenseId = :expenseId")
    fun getDebtorsByExpenseIdFlow(expenseId: Int): Flow<List<ExpenseDebtorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebtor(debtor: ExpenseDebtorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebtors(debtors: List<ExpenseDebtorEntity>)

    @Update
    suspend fun updateDebtor(debtor: ExpenseDebtorEntity)

    @Delete
    suspend fun deleteDebtor(debtor: ExpenseDebtorEntity)

    @Query("DELETE FROM expense_debtors WHERE expenseId = :expenseId")
    suspend fun deleteDebtorsByExpenseId(expenseId: Int)

    @Query("DELETE FROM expense_debtors WHERE expenseId IN (SELECT id FROM expenses WHERE groupCode = :groupCode)")
    suspend fun deleteDebtorsByGroupCode(groupCode: String)

    @Query("DELETE FROM expense_debtors")
    suspend fun deleteAllDebtors()

    // User Expense Summary operations
    @Query("SELECT * FROM user_expense_summaries WHERE groupCode = :groupCode")
    suspend fun getUserExpenseSummariesByGroupCode(groupCode: String): List<UserExpenseSummaryEntity>

    @Query("SELECT * FROM user_expense_summaries WHERE groupCode = :groupCode")
    fun getUserExpenseSummariesByGroupCodeFlow(groupCode: String): Flow<List<UserExpenseSummaryEntity>>

    @Query("SELECT * FROM user_expense_summaries WHERE id = :summaryId")
    suspend fun getUserExpenseSummaryById(summaryId: String): UserExpenseSummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserExpenseSummary(summary: UserExpenseSummaryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserExpenseSummaries(summaries: List<UserExpenseSummaryEntity>)

    @Update
    suspend fun updateUserExpenseSummary(summary: UserExpenseSummaryEntity)

    @Delete
    suspend fun deleteUserExpenseSummary(summary: UserExpenseSummaryEntity)

    @Query("DELETE FROM user_expense_summaries WHERE groupCode = :groupCode")
    suspend fun deleteUserExpenseSummariesByGroupCode(groupCode: String)

    @Query("DELETE FROM user_expense_summaries")
    suspend fun deleteAllUserExpenseSummaries()

    // User Balance operations
    @Query("SELECT * FROM user_balances WHERE summaryId = :summaryId")
    suspend fun getUserBalancesBySummaryId(summaryId: String): List<UserBalanceEntity>

    @Query("SELECT * FROM user_balances WHERE summaryId = :summaryId")
    fun getUserBalancesBySummaryIdFlow(summaryId: String): Flow<List<UserBalanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserBalance(balance: UserBalanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserBalances(balances: List<UserBalanceEntity>)

    @Update
    suspend fun updateUserBalance(balance: UserBalanceEntity)

    @Delete
    suspend fun deleteUserBalance(balance: UserBalanceEntity)

    @Query("DELETE FROM user_balances WHERE summaryId LIKE :groupCode || '%'")
    suspend fun deleteUserBalancesByGroupCode(groupCode: String)

    @Query("DELETE FROM user_balances")
    suspend fun deleteAllUserBalances()

    // Transaction operations to maintain data consistency
    @Transaction
    suspend fun insertExpenseWithDebtors(
        expense: ExpenseEntity,
        debtors: List<ExpenseDebtorEntity>
    ) {
        insertExpense(expense)
        insertDebtors(debtors)
    }

    @Transaction
    suspend fun updateExpenseWithDebtors(
        expense: ExpenseEntity,
        debtors: List<ExpenseDebtorEntity>
    ) {
        updateExpense(expense)
        deleteDebtorsByExpenseId(expense.id)
        insertDebtors(debtors)
    }

    @Transaction
    suspend fun deleteExpenseWithDebtors(expenseId: Int) {
        deleteDebtorsByExpenseId(expenseId)
        deleteExpenseById(expenseId)
    }

    @Transaction
    suspend fun insertUserExpenseSummaryWithBalances(
        summary: UserExpenseSummaryEntity,
        balances: List<UserBalanceEntity>
    ) {
        insertUserExpenseSummary(summary)
        insertUserBalances(balances)
    }

    @Transaction
    suspend fun deleteAllExpenseData() {
        deleteAllDebtors()
        deleteAllExpenses()
        deleteAllUserBalances()
        deleteAllUserExpenseSummaries()
    }

    @Transaction
    suspend fun deleteAllExpenseDataForGroup(groupCode: String) {
        deleteDebtorsByGroupCode(groupCode)
        deleteExpensesByGroupCode(groupCode)
        deleteUserBalancesByGroupCode(groupCode)
        deleteUserExpenseSummariesByGroupCode(groupCode)
    }
}