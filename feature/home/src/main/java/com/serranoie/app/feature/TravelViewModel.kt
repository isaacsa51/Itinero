package com.serranoie.app.feature

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.model.Place
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
import kotlinx.coroutines.flow.update
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

data class SelectedPlaceDetails(
    val address: String,
    val placeId: String,
    val lat: Double?,
    val lng: Double?
)

class SharedTravelViewModel(
    private val travelUseCase: TravelUseCase,
    private val placesClient: PlacesClient
) : ViewModel() {

    private val _createUiState = MutableStateFlow<TravelUiState>(TravelUiState.Idle)
    val createUiState: StateFlow<TravelUiState> = _createUiState.asStateFlow()

    private val _joinUiState = MutableStateFlow<TravelUiState>(TravelUiState.Idle)
    val joinUiState: StateFlow<TravelUiState> = _joinUiState.asStateFlow()

    private val _locationAutofill = MutableStateFlow<List<AutocompleteResult>>(emptyList())
    val locationAutofill: StateFlow<List<AutocompleteResult>> = _locationAutofill.asStateFlow()

    private val _selectedPlaceDetails = MutableStateFlow<SelectedPlaceDetails?>(null)
    val selectedPlaceDetails: StateFlow<SelectedPlaceDetails?> = _selectedPlaceDetails.asStateFlow()

    private var searchJob: Job? = null
    private var currentRequestId = 0

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
        accommodationReservationCode: String,
        accommodationExtraInfo: String,
        reservationCode: String,
        extraInfo: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _createUiState.value = TravelUiState.Loading

            val coordinates = getSelectedLocationCoordinates()

            val accommodation = Accommodation(
                name = accommodationName,
                phone = accommodationPhone,
                checkIn = accommodationCheckIn,
                checkOut = accommodationCheckOut,
                latitude = coordinates?.first,
                longitude = coordinates?.second,
                reservationCode = accommodationReservationCode,
                extraInfo = accommodationExtraInfo,
                location = _selectedPlaceDetails.value?.address ?: "",
                mapUri = null
            )

            val createTripRequest = CreateTrip(
                ownerId = 123, // TODO: Get actual user ID from auth
                destination = destination,
                startDate = startDate,
                endDate = endDate,
                summary = summary,
                accommodation = accommodation,
                groupName = groupName,
                reservationCode = reservationCode,
                extraInfo = extraInfo
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
        _locationAutofill.update { emptyList() }
        if (query.isBlank()) {
            return
        }

        val requestId = ++currentRequestId

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(300L) // Debounce requests
            val request = FindAutocompletePredictionsRequest.builder().setQuery(query).build()
            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    viewModelScope.launch(Dispatchers.Main) {
                        if (requestId == currentRequestId) {
                            _locationAutofill.update {
                                response.autocompletePredictions.map {
                                    AutocompleteResult(
                                        address = it.getFullText(null).toString(),
                                        placeId = it.placeId
                                    )
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    viewModelScope.launch(Dispatchers.Main) {
                        if (requestId == currentRequestId) {
                            exception.printStackTrace()
                        }
                    }
                }
        }
    }

    fun clearLocationAutofill() {
        _locationAutofill.update { emptyList() }
    }

    fun applyAutocompleteSelection(selectedResult: AutocompleteResult) {
        _locationAutofill.update { emptyList() }

        val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ID)
        val request = FetchPlaceRequest.builder(selectedResult.placeId, placeFields).build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                viewModelScope.launch(Dispatchers.Main) {
                    val place = response.place
                    val latLng = place.latLng
                    _selectedPlaceDetails.update {
                        SelectedPlaceDetails(
                            address = place.address ?: selectedResult.address,
                            placeId = place.id ?: selectedResult.placeId,
                            lat = latLng?.latitude,
                            lng = latLng?.longitude
                        )
                    }
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch(Dispatchers.Main) {
                    exception.printStackTrace()
                    _selectedPlaceDetails.update { null }
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

    fun getSelectedLocationCoordinates(): Pair<Double, Double>? {
        val details = _selectedPlaceDetails.value
        return if (details?.lat != null && details.lng != null) {
            Pair(details.lat, details.lng)
        } else null
    }

    fun clearSelectedPlace() {
        _selectedPlaceDetails.update { null }
    }
}

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
