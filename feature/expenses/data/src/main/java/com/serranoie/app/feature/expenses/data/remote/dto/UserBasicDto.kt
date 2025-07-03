/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: UserBasicDto.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.data.remote.dto

data class UserBasicDto(
    val id: Int,
    val name: String,
    val surname: String
)

data class UserBalanceDto(
    val userId: Int,
    val name: String,
    val balance: Double
)
