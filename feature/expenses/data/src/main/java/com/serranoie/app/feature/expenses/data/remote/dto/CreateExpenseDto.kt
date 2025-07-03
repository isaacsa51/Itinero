/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: CreateExpenseDto.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.data.remote.dto

data class CreateExpenseDto(
    val tripId: Int,
    val name: String,
    val amount: Double,
    val date: String,
    val category: String,
    val paidByUserId: Int,
    val paymentMethod: String,
    val splitType: String,
    val notes: String? = null,
    val debtors: List<CreateDebtorDto>
)