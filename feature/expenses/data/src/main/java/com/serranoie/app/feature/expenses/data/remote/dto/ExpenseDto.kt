/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ExpenseDto.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.data.remote.dto

data class ExpenseDto(
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
    val debtors: List<ExpenseDebtorDto>,
    val paidBy: UserBasicDto? = null
)
