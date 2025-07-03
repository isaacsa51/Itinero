/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: UserExpenseSummary.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserExpenseSummaryDto(
    val totalTripExpenses: Double,
    val userAmountOwed: Double,
    val userAmountToReceive: Double,
    val userBalance: Double,
    val expenses: List<ExpenseDto>
)