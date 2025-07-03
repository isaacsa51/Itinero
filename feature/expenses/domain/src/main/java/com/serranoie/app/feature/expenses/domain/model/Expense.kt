/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: Expense.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Expense(
    val id: Int = 0,
    val tripId: Int,
    val name: String,
    val amount: Double,
    val date: String,
    val category: String,
    val paidByUserId: Int,
    val paymentMethod: String,
    val splitType: String,
    val notes: String? = null,
    val isCompleted: Boolean = false,
    val debtors: List<ExpenseDebtor> = emptyList(),
    val paidBy: UserBasic? = null
)
