/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ExpenseSummary.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ExpenseSummary(
    val totalExpenses: Double,
    val totalOwed: Double,
    val totalPaid: Double,
    val balances: List<UserBalance>
)


@Serializable
data class UserExpenseSummary(
    val totalTripExpenses: Double,
    val userAmountOwed: Double,
    val userAmountToReceive: Double,
    val userBalance: Double,
    val expenses: List<Expense>
)
