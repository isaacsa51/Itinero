/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ExpensesApi.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.data.remote

import com.serranoie.app.feature.expenses.data.remote.dto.CreateExpenseDto
import com.serranoie.app.feature.expenses.data.remote.dto.ExpenseDto
import com.serranoie.app.feature.expenses.data.remote.dto.UserExpenseSummaryDto
import com.serranoie.itinero.core.data.remote.resources.BaseApiClient
import com.serranoie.itinero.core.domain.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface ExpensesApi {
    // 1. Create Expense
    suspend fun createExpense(groupCode: String, expense: CreateExpenseDto): Result<ExpenseDto>

    // 2. Get All Trip Expenses  
    suspend fun getAllTripExpenses(groupCode: String): Result<List<ExpenseDto>>

    // 3. Get User's Expense Summary
    suspend fun getUserExpenseSummary(groupCode: String): Result<UserExpenseSummaryDto>

    // 4. Get Expense by ID (for individual expense details)
    suspend fun getExpenseById(groupCode: String, expenseId: String): Result<ExpenseDto>

    // 5. Mark Expense as Completed
    suspend fun markExpenseCompleted(groupCode: String, expenseId: String): Result<Unit>

    // 6. Update Expense
    suspend fun updateExpense(
        groupCode: String,
        expenseId: String,
        expense: CreateExpenseDto
    ): Result<ExpenseDto>

    // 7. Delete Expense
    suspend fun deleteExpense(groupCode: String, expenseId: String): Result<Unit>
}

class ExpensesApiImpl(client: HttpClient) : BaseApiClient(client), ExpensesApi {

    override suspend fun createExpense(
        groupCode: String,
        expense: CreateExpenseDto
    ): Result<ExpenseDto> {
        return post("/trips/$groupCode/expenses", expense)
    }

    override suspend fun getAllTripExpenses(groupCode: String): Result<List<ExpenseDto>> {
        return get("/trips/$groupCode/expenses")
    }

    override suspend fun getUserExpenseSummary(groupCode: String): Result<UserExpenseSummaryDto> {
        return get("/trips/$groupCode/expenses/summary")
    }

    override suspend fun getExpenseById(groupCode: String, expenseId: String): Result<ExpenseDto> {
        return get("/trips/$groupCode/expenses/$expenseId")
    }

    override suspend fun markExpenseCompleted(groupCode: String, expenseId: String): Result<Unit> {
        return patch("/trips/$groupCode/expenses/$expenseId/complete")
    }

    override suspend fun updateExpense(
        groupCode: String,
        expenseId: String,
        expense: CreateExpenseDto
    ): Result<ExpenseDto> {
        return try {
            val response = client.put("$baseUrl/trips/$groupCode/expenses/$expenseId") {
                contentType(ContentType.Application.Json)
                setBody(expense)
            }
            Result.Success(handleResponse<ExpenseDto>(response))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteExpense(groupCode: String, expenseId: String): Result<Unit> {
        return delete("/trips/$groupCode/expenses/$expenseId")
    }
}