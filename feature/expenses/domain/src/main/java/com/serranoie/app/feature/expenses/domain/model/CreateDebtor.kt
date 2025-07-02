/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: CreateDebtor.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 02 julio 2025
 */

package com.serranoie.app.feature.expenses.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateDebtor(
    val userId: Int,
    val splitValue: Double
)
