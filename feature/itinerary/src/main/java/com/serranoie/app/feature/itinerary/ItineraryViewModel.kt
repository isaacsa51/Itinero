/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineraryViewModel.kt
 - Project: Itinero
 - Module: Itinero.feature.itinerary.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 19 junio 2025
 */

package com.serranoie.app.feature.itinerary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serranoie.app.feature.itinerary.domain.model.CreateItineraryItem
import com.serranoie.app.feature.itinerary.domain.model.ItineraryItem
import com.serranoie.app.feature.itinerary.domain.model.UpdateItineraryItem
import com.serranoie.app.feature.itinerary.domain.usecase.ItineraryUseCase
import com.serranoie.itinero.core.domain.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ItineraryUiState {
    data object Idle : ItineraryUiState
    data object Loading : ItineraryUiState
    data class Success<T>(val data: T) : ItineraryUiState
    data class Error(val message: String) : ItineraryUiState
}

class ItineraryViewModel(private val itineraryUseCase: ItineraryUseCase, groupCode: String) :
    ViewModel() {

    private val _uiState = MutableStateFlow<ItineraryUiState>(ItineraryUiState.Idle)
    val uiState: StateFlow<ItineraryUiState> = _uiState.asStateFlow()

    private val _itineraryData = MutableStateFlow<List<ItineraryItem>>(emptyList())
    val itineraryData: StateFlow<List<ItineraryItem>> = _itineraryData.asStateFlow()

    private val _selectedItem = MutableStateFlow<ItineraryItem?>(null)
    val selectedItem: StateFlow<ItineraryItem?> = _selectedItem.asStateFlow()

    private var currentGroupCode: String = ""

    fun fetchItinerary(groupCode: String, forceRefresh: Boolean = false) {
        currentGroupCode = groupCode
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ItineraryUiState.Loading

            when (val result = itineraryUseCase.getAllActivitiesUseCase(groupCode, forceRefresh)) {
                is Result.Success -> {
                    _itineraryData.value = result.data
                    _uiState.value = ItineraryUiState.Success(result.data)
                }

                is Result.Error -> {
                    _uiState.value = ItineraryUiState.Error(
                        result.exception.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun getActivityById(groupCode: String, itemId: String, forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ItineraryUiState.Loading
            when (val result =
                itineraryUseCase.getActivityByIdUseCase(groupCode, itemId, forceRefresh)) {
                is Result.Success -> {
                    _selectedItem.value = result.data
                    _uiState.value = ItineraryUiState.Success(result.data)
                }

                is Result.Error -> {
                    _uiState.value = ItineraryUiState.Error(
                        result.exception.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun createActivity(groupCode: String, request: CreateItineraryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ItineraryUiState.Loading
            when (val result = itineraryUseCase.createActivityUseCase(groupCode, request)) {
                is Result.Success -> {
                    _uiState.value = ItineraryUiState.Success(result.data)
                    fetchItinerary(groupCode, forceRefresh = true)
                }

                is Result.Error -> {

                    Log.e("ITINERO - Itinerary ViewModel", "Failed to create activity, request: $request")

                    Log.e(
                        "ITINERO - Itinerary ViewModel",
                        "Failed to create activity",
                        result.exception
                    )
                    _uiState.value = ItineraryUiState.Error(
                        result.exception.message ?: "Failed to create activity"
                    )
                }
            }
        }
    }

    fun updateActivity(groupCode: String, itemId: String, request: UpdateItineraryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ItineraryUiState.Loading
            when (val result =
                itineraryUseCase.updateActivityInfoUseCase(groupCode, itemId, request)) {
                is Result.Success -> {
                    _selectedItem.value = result.data
                    _uiState.value = ItineraryUiState.Success(result.data)
                    if (currentGroupCode.isNotEmpty()) {
                        fetchItinerary(currentGroupCode, forceRefresh = true)
                    }
                }

                is Result.Error -> {
                    _uiState.value = ItineraryUiState.Error(
                        result.exception.message ?: "Failed to update activity"
                    )
                }
            }
        }
    }

    fun deleteActivity(groupCode: String, itemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ItineraryUiState.Loading
            when (val result = itineraryUseCase.deleteActivityByIdUseCase(groupCode, itemId)) {
                is Result.Success -> {
                    _uiState.value = ItineraryUiState.Success(Unit)
                    _itineraryData.value =
                        _itineraryData.value.filter { it.id.toString() != itemId }
                    if (_selectedItem.value?.id.toString() == itemId) {
                        _selectedItem.value = null
                    }
                    if (currentGroupCode.isNotEmpty()) {
                        fetchItinerary(currentGroupCode, forceRefresh = true)
                    }
                }

                is Result.Error -> {
                    _uiState.value = ItineraryUiState.Error(
                        result.exception.message ?: "Failed to delete activity"
                    )
                }
            }
        }
    }

    fun toggleActivityCompletion(groupCode: String, itemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = itineraryUseCase.toggleActivityCompletionUseCase(groupCode, itemId)) {
                is Result.Success -> {
                    _itineraryData.value = _itineraryData.value.map { item ->
                        if (item.id.toString() == itemId) {
                            item.copy(isCompleted = !item.isCompleted)
                        } else {
                            item
                        }
                    }

                    _selectedItem.value?.let { selected ->
                        if (selected.id.toString() == itemId) {
                            _selectedItem.value = selected.copy(isCompleted = !selected.isCompleted)
                        }
                    }

                    if (currentGroupCode.isNotEmpty()) {
                        fetchItinerary(currentGroupCode, forceRefresh = true)
                    }
                }

                is Result.Error -> {
                    Log.e(
                        "ITINERO - Itinerary ViewModel",
                        "Failed to toggle completion for activity with id: $itemId",
                        result.exception
                    )
                    _uiState.value = ItineraryUiState.Error(
                        result.exception.message ?: "Failed to toggle completion"
                    )
                }
            }
        }
    }

    fun clearSelectedItem() {
        _selectedItem.value = null
    }

    fun resetState() {
        _uiState.value = ItineraryUiState.Idle
    }

    fun refreshData() {
        Log.d(
            "ITINERO - Itinerary ViewModel",
            "refreshData called with currentGroupCode: $currentGroupCode"
        )
        if (currentGroupCode.isNotEmpty()) {
            fetchItinerary(currentGroupCode, forceRefresh = true)
        }
    }
}
