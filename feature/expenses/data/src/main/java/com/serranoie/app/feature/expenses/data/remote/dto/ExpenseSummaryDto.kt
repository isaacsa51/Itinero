/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ExpenseSummaryDto.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExpenseSummaryDto(
    val totalExpenses: Double,
    val totalOwed: Double,
    val totalPaid: Double,
    val balances: List<UserBalanceDto>
)
