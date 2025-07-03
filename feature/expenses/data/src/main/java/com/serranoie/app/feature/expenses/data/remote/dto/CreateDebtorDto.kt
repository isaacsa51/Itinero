/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: CreateDebtorDto.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.data.remote.dto

data class CreateDebtorDto(
    val userId: Int,
    val splitValue: Double
)
