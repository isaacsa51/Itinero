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

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.model.TripOverview
import com.serranoie.itinero.core.domain.model.UpdateTrip
import com.serranoie.itinero.core.domain.repository.NotificationRepository
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.usecase.TravelUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Idle : HomeUiState
    data object Loading : HomeUiState
    data class Success(val data: Trip) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

sealed interface OverviewUiState {
    data object Idle : OverviewUiState
    data object Loading : OverviewUiState
    data class Success(val data: TripOverview) : OverviewUiState
    data class Error(val message: String) : OverviewUiState
}

class HomeViewModel(
    private val travelUseCase: TravelUseCase,
    private val groupCode: String,
    private val notificationRepository: NotificationRepository? = null
): ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _overviewUiState = MutableStateFlow<OverviewUiState>(OverviewUiState.Idle)
    val overviewUiState: StateFlow<OverviewUiState> = _overviewUiState.asStateFlow()

    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip.asStateFlow()

    fun getCurrentTravel(groupCode: String, forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = HomeUiState.Loading

            when (val result = travelUseCase.getTravelById(groupCode, forceRefresh)) {
                is Result.Success -> {
                    _trip.value = result.data
                    _uiState.value = HomeUiState.Success(result.data)
                }

                is Result.Error -> {
                    _uiState.value = HomeUiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }

    fun getCurrentTravel() {
        getCurrentTravel(groupCode, forceRefresh = false)
    }


    fun refreshTrip() {
        getCurrentTravel(groupCode, forceRefresh = true)
    }

    fun updateTripInfo(groupCode: String, request: UpdateTrip) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = HomeUiState.Loading
            when (val result = travelUseCase.updateTripInfo(groupCode, request)) {
                is Result.Success -> {
                    _trip.value = result.data
                    _uiState.value = HomeUiState.Success(result.data)
                }
                is Result.Error -> {
                    Log.d("HomeViewModel", "updateTripInfo: Error, ${result.exception.message}")
                    _uiState.value = HomeUiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }

    fun getTripOverview(groupCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _overviewUiState.value = OverviewUiState.Loading

            when (val result = travelUseCase.getTripOverview(groupCode)) {
                is Result.Success -> {
                    _overviewUiState.value = OverviewUiState.Success(result.data)
                }
                is Result.Error -> {
                    _overviewUiState.value = OverviewUiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }

    fun getAllTravels(callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = travelUseCase.getAllTravels()) {
                is Result.Success -> {
                    val hasOtherTrips = result.data.isNotEmpty()
                    callback(hasOtherTrips)
                }

                is Result.Error -> {
                    callback(false)
                }
            }
        }
    }
}
