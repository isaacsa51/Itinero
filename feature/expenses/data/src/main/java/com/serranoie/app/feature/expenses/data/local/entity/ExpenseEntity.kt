/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ExpenseEntity.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: Int,
    val tripId: Int,
    val groupCode: String,
    val name: String,
    val amount: Double,
    val date: String,
    val category: String,
    val paidByUserId: Int,
    val paymentMethod: String,
    val splitType: String,
    val notes: String?,
    val isCompleted: Boolean = false,
    @Embedded(prefix = "paidBy_")
    val paidBy: EmbeddedUserBasic?,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Entity(tableName = "expense_debtors")
data class ExpenseDebtorEntity(
    @PrimaryKey val id: Int,
    val expenseId: Int,
    val userId: Int,
    val amount: Double,
    val splitValue: Double,
    @Embedded(prefix = "user_")
    val user: EmbeddedUserBasic
)

@Entity(tableName = "user_expense_summaries")
data class UserExpenseSummaryEntity(
    @PrimaryKey val id: String, // groupCode + userId combination
    val groupCode: String,
    val userId: Int,
    val totalExpenses: Double,
    val totalOwed: Double,
    val totalPaid: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_balances")
data class UserBalanceEntity(
    @PrimaryKey val id: String, // summaryId + userId combination
    val summaryId: String,
    val userId: Int,
    val name: String,
    val balance: Double
)

data class EmbeddedUserBasic(
    val id: Int,
    val name: String,
    val surname: String
)