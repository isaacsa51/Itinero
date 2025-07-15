/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ExpensesViewModelModule.kt
 - Project: Itinero
 - Module: Itinero.feature.expenses.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 05 julio 2025
 */

package com.serranoie.app.feature.expenses.di

import com.serranoie.app.feature.expenses.ExpenseDetailsViewModel
import com.serranoie.app.feature.expenses.ExpensesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val expensesViewModelModule = module {
    viewModel { ExpensesViewModel(get(), get()) }
}

val expensesDetailsViewModel = module {
    viewModel { (groupCode: String) -> ExpenseDetailsViewModel(get(), get(), get(), groupCode) }
}