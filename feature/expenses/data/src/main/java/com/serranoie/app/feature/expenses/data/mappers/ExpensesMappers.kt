/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ExpensesMappers.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.data.mappers

import com.serranoie.app.feature.expenses.data.remote.dto.CreateDebtorDto
import com.serranoie.app.feature.expenses.data.remote.dto.CreateExpenseDto
import com.serranoie.app.feature.expenses.data.remote.dto.ExpenseDebtorDto
import com.serranoie.app.feature.expenses.data.remote.dto.ExpenseDto
import com.serranoie.app.feature.expenses.data.remote.dto.UserBalanceDto
import com.serranoie.app.feature.expenses.data.remote.dto.UserBasicDto
import com.serranoie.app.feature.expenses.data.remote.dto.UserExpenseSummaryDto
import com.serranoie.app.feature.expenses.domain.model.CreateDebtor
import com.serranoie.app.feature.expenses.domain.model.CreateExpense
import com.serranoie.app.feature.expenses.domain.model.Expense
import com.serranoie.app.feature.expenses.domain.model.ExpenseDebtor
import com.serranoie.app.feature.expenses.domain.model.UserBalance
import com.serranoie.app.feature.expenses.domain.model.UserBasic
import com.serranoie.app.feature.expenses.domain.model.UserExpenseSummary
import com.serranoie.itinero.core.data.local.entity.EmbeddedUserBasic
import com.serranoie.itinero.core.data.local.entity.ExpenseDebtorEntity
import com.serranoie.itinero.core.data.local.entity.ExpenseEntity
import com.serranoie.itinero.core.data.local.entity.UserBalanceEntity
import com.serranoie.itinero.core.data.local.entity.UserExpenseSummaryEntity

// DTO to Domain mappings
fun UserExpenseSummaryDto.toDomain(): UserExpenseSummary {
    return UserExpenseSummary(
        totalTripExpenses = totalTripExpenses,
        userAmountOwed = userAmountOwed,
        userAmountToReceive = userAmountToReceive,
        userBalance = userBalance,
        expenses = expenses.map { it.toDomain() }
    )
}

fun UserBalanceDto.toDomain(): UserBalance {
    return UserBalance(
        userId = userId,
        name = name,
        balance = balance
    )
}

fun ExpenseDto.toDomain(): Expense {
    return Expense(
        id = id,
        tripId = tripId,
        name = name,
        amount = amount,
        date = date,
        category = category,
        paidByUserId = paidByUserId,
        paymentMethod = paymentMethod,
        splitType = splitType,
        notes = notes,
        isCompleted = isCompleted,
        debtors = debtors.map { it.toDomain() },
        paidBy = paidBy?.toDomain()
    )
}

fun ExpenseDebtorDto.toDomain(): ExpenseDebtor {
    return ExpenseDebtor(
        id = id,
        userId = userId,
        amount = amount,
        splitValue = splitValue,
        user = user.toDomain()
    )
}

fun UserBasicDto.toDomain(): UserBasic {
    return UserBasic(
        id = id,
        name = name,
        surname = surname
    )
}

fun CreateDebtorDto.toDomain(): CreateDebtor {
    return CreateDebtor(
        userId = userId,
        splitValue = splitValue
    )
}

// Domain to DTO mappings
fun CreateExpense.toDto(): CreateExpenseDto {
    return CreateExpenseDto(
        tripId = tripId,
        name = name,
        amount = amount,
        date = date,
        category = category,
        paidByUserId = paidByUserId,
        paymentMethod = paymentMethod,
        splitType = splitType,
        notes = notes,
        debtors = debtors.map { it.toDto() }
    )
}

fun CreateDebtor.toDto(): CreateDebtorDto {
    return CreateDebtorDto(
        userId = userId,
        splitValue = splitValue
    )
}

fun Expense.toDto(): CreateExpenseDto {
    return CreateExpenseDto(
        tripId = tripId,
        name = name,
        amount = amount,
        date = date,
        category = category,
        paidByUserId = paidByUserId,
        paymentMethod = paymentMethod,
        splitType = splitType,
        notes = notes,
        debtors = debtors.map { it.toDto() }
    )
}

fun ExpenseDebtor.toDto(): CreateDebtorDto {
    return CreateDebtorDto(
        userId = userId,
        splitValue = splitValue
    )
}

// Helper extension for CreateExpense to Expense conversion
fun CreateExpense.toDomain(): Expense {
    return Expense(
        id = 0, // Will be set by the server
        tripId = tripId,
        name = name,
        amount = amount,
        date = date,
        category = category,
        paidByUserId = paidByUserId,
        paymentMethod = paymentMethod,
        splitType = splitType,
        notes = notes,
        isCompleted = false,
        debtors = debtors.map { it.toDomain() },
        paidBy = null // Will be populated by server
    )
}

fun CreateDebtor.toDomain(user: UserBasic? = null): ExpenseDebtor {
    return ExpenseDebtor(
        id = 0, // Will be set by server
        userId = userId,
        amount = 0.0, // Will be calculated by server based on splitValue
        splitValue = splitValue,
        user = user ?: UserBasic(
            id = userId,
            name = "",
            surname = ""
        ) // Will be populated by server unless provided
    )
}

// Entity to Domain mappings
fun ExpenseEntity.toDomain(debtors: List<ExpenseDebtorEntity>): Expense {
    return Expense(
        id = id,
        tripId = tripId,
        name = name,
        amount = amount,
        date = date,
        category = category,
        paidByUserId = paidByUserId,
        paymentMethod = paymentMethod,
        splitType = splitType,
        notes = notes,
        isCompleted = isCompleted,
        debtors = debtors.map { it.toDomain() },
        paidBy = paidBy?.toDomain()
    )
}

fun ExpenseDebtorEntity.toDomain(): ExpenseDebtor {
    return ExpenseDebtor(
        id = id,
        userId = userId,
        amount = amount,
        splitValue = splitValue,
        user = user.toDomain()
    )
}

fun UserExpenseSummaryEntity.toDomain(balances: List<UserBalanceEntity>): UserExpenseSummary {
    return UserExpenseSummary(
        totalTripExpenses = totalExpenses,
        userAmountOwed = totalOwed,
        userAmountToReceive = totalPaid,
        userBalance = totalPaid - totalOwed,
        expenses = listOf()
    )
}

fun UserBalanceEntity.toDomain(): UserBalance {
    return UserBalance(
        userId = userId,
        name = name,
        balance = balance
    )
}

fun EmbeddedUserBasic.toDomain(): UserBasic {
    return UserBasic(
        id = id,
        name = name,
        surname = surname
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        tripId = tripId,
        groupCode = tripId.toString(),
        name = name,
        amount = amount,
        date = date,
        category = category,
        paidByUserId = paidByUserId,
        paymentMethod = paymentMethod,
        splitType = splitType,
        notes = notes,
        isCompleted = isCompleted,
        paidBy = paidBy?.toEntity(),
        createdAt = "",
        updatedAt = ""
    )
}

fun ExpenseDebtor.toEntity(expenseId: Int): ExpenseDebtorEntity {
    return ExpenseDebtorEntity(
        id = id,
        expenseId = expenseId,
        userId = userId,
        amount = amount,
        splitValue = splitValue,
        user = user?.toEntity() ?: EmbeddedUserBasic(
            id = userId,
            name = "",
            surname = ""
        )
    )
}

fun UserExpenseSummary.toEntity(groupCode: String): UserExpenseSummaryEntity {
    return UserExpenseSummaryEntity(
        id = "${groupCode}_summary",
        groupCode = groupCode,
        userId = 0,
        totalExpenses = totalTripExpenses,
        totalOwed = userAmountOwed,
        totalPaid = userAmountToReceive,
        lastUpdated = System.currentTimeMillis()
    )
}

fun UserBalance.toEntity(summaryId: String): UserBalanceEntity {
    return UserBalanceEntity(
        id = "${summaryId}_${userId}",
        summaryId = summaryId,
        userId = userId,
        name = name,
        balance = balance
    )
}

fun UserBasic.toEntity(): EmbeddedUserBasic {
    return EmbeddedUserBasic(
        id = id,
        name = name,
        surname = surname
    )
}