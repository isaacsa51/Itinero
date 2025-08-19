package com.serranoie.app.feature

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.serranoie.itinero.core.data.mappers.toTrip
import com.serranoie.itinero.core.domain.exception.NetworkException
import com.serranoie.itinero.core.domain.model.Accommodation
import com.serranoie.itinero.core.domain.model.CreateTrip
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.usecase.TravelUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TravelUiState {
    data object Idle : TravelUiState
    data object Loading : TravelUiState
    data class Success<T>(val data: T) : TravelUiState
    data class Error(val message: String) : TravelUiState
    data object NetworkError : TravelUiState
    data object NoInternet : TravelUiState
}

data class AutocompleteResult(
    val address: String,
    val placeId: String
)

// Shared ViewModel for creation and joining operations only
class SharedTravelViewModel(
    private val travelUseCase: TravelUseCase,
    private val placesClient: PlacesClient
) : ViewModel() {

    private val _createUiState = MutableStateFlow<TravelUiState>(TravelUiState.Idle)
    val createUiState: StateFlow<TravelUiState> = _createUiState.asStateFlow()

    private val _joinUiState = MutableStateFlow<TravelUiState>(TravelUiState.Idle)
    val joinUiState: StateFlow<TravelUiState> = _joinUiState.asStateFlow()

    private val _locationAutofill = mutableStateListOf<AutocompleteResult>()
    val locationAutofill: List<AutocompleteResult> = _locationAutofill

    private var searchJob: Job? = null

    fun createTravel(
        groupName: String,
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
        viewModelScope.launch(Dispatchers.IO) {
            _createUiState.value = TravelUiState.Loading

            val accommodation = Accommodation(
                name = accommodationName,
                phone = accommodationPhone,
                checkIn = accommodationCheckIn,
                checkOut = accommodationCheckOut,
                location = accommodationLocation,
                mapUri = accommodationMapUri.takeIf { it.isNotBlank() }
            )

            val createTripRequest = CreateTrip(
                groupName = groupName,
                destination = destination,
                startDate = startDate,
                endDate = endDate,
                summary = summary,
                accommodation = accommodation,
                reservationCode = reservationCode,
                extraInfo = extraInfo,
                additionalInfo = additionalInfo
            )

            when (val result = travelUseCase.createTravel(createTripRequest)) {
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

    fun searchPlaces(query: String) {
        searchJob?.cancel()
        _locationAutofill.clear()
        if (query.isBlank()) {
            return
        }
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(300L) // Debounce requests
            val request = FindAutocompletePredictionsRequest.builder().setQuery(query).build()
            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    _locationAutofill.addAll(response.autocompletePredictions.map {
                        AutocompleteResult(
                            address = it.getFullText(null).toString(),
                            placeId = it.placeId
                        )
                    })
                }
                .addOnFailureListener { exception ->
                    // You can log the error or update a UI state to show failure
                    exception.printStackTrace()
                }
        }
    }

    fun clearLocationAutofill() {
        _locationAutofill.clear()
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
                    val exception = result.exception
                    when (exception) {
                        is NetworkException -> _uiState.value = TravelUiState.NetworkError
                        else -> _uiState.value =
                            TravelUiState.Error(exception.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = TravelUiState.Idle
        _travels.value = emptyList()
    }
}
