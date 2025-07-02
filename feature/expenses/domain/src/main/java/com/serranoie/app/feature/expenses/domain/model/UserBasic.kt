/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: UserBasic.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserBasic(
    val id: Int,
    val name: String,
    val surname: String
)

@Serializable
data class UserBalance(
    val userId: Int,
    val name: String,
    val balance: Double
)
