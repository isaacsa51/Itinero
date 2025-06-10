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
    private val travelUseCase: TravelUseCase,
    private val groupCode: String
): ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip.asStateFlow()

    /**
     * Gets current travel data with caching strategy
     * @param forceRefresh If true, bypasses cache and fetches fresh data
     */
    fun getCurrentTravel(groupCode: String, forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = HomeUiState.Loading

            when (val result = travelUseCase.getTravelById(groupCode, forceRefresh)) {
                is com.serranoie.itinero.core.domain.result.Result.Success -> {
                    _trip.value = result.data
                    _uiState.value = HomeUiState.Success(result.data)
                }

                is com.serranoie.itinero.core.domain.result.Result.Error -> {
                    _uiState.value = HomeUiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }

    /**
     * Convenience method that uses the group code from constructor
     */
    fun getCurrentTravel() {
        getCurrentTravel(groupCode, forceRefresh = false)
    }

    /**
     * Refreshes trip data by forcing a remote fetch
     */
    fun refreshTrip() {
        getCurrentTravel(groupCode, forceRefresh = true)
    }
}
