package com.serranoie.app.feature

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranoie.itinero.core.data.mappers.toTrip
import com.serranoie.itinero.core.domain.model.CreateTrip
import com.serranoie.itinero.core.domain.model.Trip
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

// Shared ViewModel for creation and joining operations only
class SharedTravelViewModel(
    private val travelUseCase: TravelUseCase
) : ViewModel() {

    private val _createUiState = MutableStateFlow<TravelUiState>(TravelUiState.Idle)
    val createUiState: StateFlow<TravelUiState> = _createUiState.asStateFlow()

    private val _joinUiState = MutableStateFlow<TravelUiState>(TravelUiState.Idle)
    val joinUiState: StateFlow<TravelUiState> = _joinUiState.asStateFlow()

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
            _createUiState.value = TravelUiState.Loading
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
                    val created = result.data
                    val newTrip = created.toTrip()
                    _createUiState.value = TravelUiState.Success(newTrip)
                }

                is Result.Error -> {
                    _createUiState.value = TravelUiState.Error(result.exception.toString())
                }
            }
        }
    }

    fun joinTravel(groupCode: String) {
        viewModelScope.launch {
            _joinUiState.value = TravelUiState.Loading
            when (val result = travelUseCase.joinTravel(groupCode)) {
                is Result.Success -> {
                    _joinUiState.value = TravelUiState.Success(Unit)
                }

                is Result.Error -> {
                    _joinUiState.value =
                        TravelUiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }

    fun resetCreateState() {
        _createUiState.value = TravelUiState.Idle
    }

    fun resetJoinState() {
        _joinUiState.value = TravelUiState.Idle
    }
}

// Separate ViewModel for travel list operations
class TravelListViewModel(
    private val travelUseCase: TravelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TravelUiState>(TravelUiState.Idle)
    val uiState: StateFlow<TravelUiState> = _uiState.asStateFlow()

    private val _travels = MutableStateFlow<List<Trip>>(emptyList())
    val travels: StateFlow<List<Trip>> = _travels.asStateFlow()

    fun getAllTravels() {
        viewModelScope.launch {
            _uiState.value = TravelUiState.Loading
            when (val result = travelUseCase.getAllTravels()) {
                is Result.Success -> {
                    _travels.value = result.data
                    _uiState.value = TravelUiState.Success(result.data)
                }

                is Result.Error -> {
                    _uiState.value =
                        TravelUiState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = TravelUiState.Idle
    }
}

// Keep original for backward compatibility (can be removed later)
class TravelViewModel(
    private val travelUseCase: TravelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TravelUiState>(TravelUiState.Idle)
    val uiState: StateFlow<TravelUiState> = _uiState.asStateFlow()

    private val _travels = MutableStateFlow<List<Trip>>(emptyList())
    val travels: StateFlow<List<Trip>> = _travels.asStateFlow()

    private val _currentTrip = MutableStateFlow<Trip?>(null)
    val currentTrip: StateFlow<Trip?> = _currentTrip.asStateFlow()

    private val _createdTrip = MutableStateFlow<Trip?>(null)
    val createdTrip: StateFlow<Trip?> = _createdTrip.asStateFlow()

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
                    _currentTrip.value = result.data
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
                    val created = result.data // CreateTrip
                    val newTrip = created.toTrip()
                    _createdTrip.value = newTrip
                    _travels.value += newTrip
                    _uiState.value = TravelUiState.Success(newTrip)
                }
                is Result.Error -> {
                    _uiState.value = TravelUiState.Error(result.exception.toString())
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = TravelUiState.Idle
    }
}
