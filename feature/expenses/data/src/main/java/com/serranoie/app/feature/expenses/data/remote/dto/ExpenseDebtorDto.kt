/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ExpenseDebtorDto.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.data.remote.dto

data class ExpenseDebtorDto(
    val id: Int,
    val userId: Int,
    val amount: Double,
    val splitValue: Double,
    val user: UserBasicDto
)
