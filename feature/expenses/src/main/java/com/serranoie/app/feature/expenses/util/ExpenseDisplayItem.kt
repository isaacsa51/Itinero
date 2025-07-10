/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ExpenseDisplayItem.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 09 julio 2025
 */

package com.serranoie.app.feature.expenses.util

import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDate

data class ExpenseDisplayItem(
    val id: Int,
    val expenseDate: LocalDate,
    val expenseType: String,
    val expenseCategory: String,
    val expenseName: String,
    val membersCount: Int,
    val amountOwed: Double,
    val isCompleted: Boolean,
    val isYours: Boolean,
    val icon: ImageVector
)
