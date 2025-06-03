package com.serranoie.app.feature

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranoie.itinero.core.domain.model.Travel
import com.serranoie.itinero.core.domain.usecase.TravelUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.serranoie.itinero.core.domain.result.Result

sealed interface TravelUiState {
    data object Idle : TravelUiState
    data object Loading : TravelUiState
    data class Success<T>(val data: T) : TravelUiState
    data class Error(val message: String) : TravelUiState
}

class TravelViewModel(
    private val travelUseCase: TravelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TravelUiState>(TravelUiState.Idle)
    val uiState: StateFlow<TravelUiState> = _uiState.asStateFlow()

    private val _travels = MutableStateFlow<List<Travel>>(emptyList())
    val travels: StateFlow<List<Travel>> = _travels.asStateFlow()

    private val _currentTravel = MutableStateFlow<Travel?>(null)
    val currentTravel: StateFlow<Travel?> = _currentTravel.asStateFlow()

    fun getAllTravels() {
        viewModelScope.launch {
            _uiState.value = TravelUiState.Loading
            when (val result = travelUseCase.getAllTravels()) {
                is Result.Success -> {
                    _travels.value = result.data
                    _uiState.value = TravelUiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = TravelUiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }

    fun getTravelById(id: String) {
        viewModelScope.launch {
            _uiState.value = TravelUiState.Loading
            when (val result = travelUseCase.getTravelById(id)) {
                is Result.Success -> {
                    _currentTravel.value = result.data
                    _uiState.value = TravelUiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = TravelUiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }

    fun joinTravel(groupCode: String) {
        viewModelScope.launch {
            _uiState.value = TravelUiState.Loading
            when (val result = travelUseCase.joinTravel(groupCode)) {
                is Result.Success -> {
                    _uiState.value = TravelUiState.Success(Unit)
                    // Refresh the travel list after joining
                    getAllTravels()
                }
                is Result.Error -> {
                    _uiState.value = TravelUiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }

    fun createTravel(
        destination: String,
        startDate: String,
        endDate: String,
        summary: String,
        accommodationName: String,
        accommodationPhone: String,
        accommodationCheckIn: String,
        accommodationCheckOut: String,
        accommodationLocation: String,
        accommodationMapUri: String,
        reservationCode: String,
        extraInfo: String,
        additionalInfo: String
    ) {
        viewModelScope.launch {
            Log.d("ISAAC", "TravelViewModel.createTravel called with destination: $destination")
            _uiState.value = TravelUiState.Loading
            when (val result = travelUseCase.createTravel(
                destination,
                startDate,
                endDate,
                summary,
                accommodationName,
                accommodationPhone,
                accommodationCheckIn,
                accommodationCheckOut,
                accommodationLocation,
                accommodationMapUri,
                reservationCode,
                extraInfo,
                additionalInfo
            )) {
                is Result.Success -> {
                    val newTravel = result.data
                    _currentTravel.value = newTravel
                    // Add the new travel to the list
                    _travels.value += newTravel
                    _uiState.value = TravelUiState.Success(newTravel)
                    Log.d("ISAAC", "Travel created successfully: $newTravel")
                }
                is Result.Error -> {
                    _uiState.value = TravelUiState.Error(result.exception.message ?: "Unknown error")
                    Log.e("ISAAC", "Error creating travel: ${result.exception.message}")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = TravelUiState.Idle
    }
}
