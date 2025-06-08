/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: HomeViewModel.kt
 - Project: Itinero
 - Module: Itinero.feature.home.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 05 junio 2025
 */

package com.serranoie.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.usecase.TravelUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Idle : HomeUiState
    data object Loading : HomeUiState
    data class Success<T>(val data: T) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val travelUseCase: TravelUseCase, groupCode: String
): ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _travels = MutableStateFlow<Trip?>(null)
    val travels: StateFlow<Trip?> = _travels.asStateFlow()


    fun getCurrentTravel(groupCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
           _uiState.value = HomeUiState.Loading

            when (val result = travelUseCase.getTravelById(groupCode)) {
                is com.serranoie.itinero.core.domain.result.Result.Success -> {
                    _travels.value = result.data
                    _uiState.value = HomeUiState.Success(result.data)
                }

                is com.serranoie.itinero.core.domain.result.Result.Error -> {
                    _uiState.value = HomeUiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }
}
