/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: CreateExpense.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateExpense(
    val tripId: Int,
    val name: String,
    val amount: Double,
    val date: String,
    val category: String,
    val paidByUserId: Int,
    val paymentMethod: String,
    val splitType: String,
    val notes: String? = null,
    val debtors: List<CreateDebtor>
)
