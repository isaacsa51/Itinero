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

    /**
     * Loads all itinerary items for the specified group and updates the UI state accordingly.
     *
     * @param groupCode The code identifying the group whose itinerary should be fetched.
     * @param forceRefresh If true, forces a refresh from the data source instead of using cached data.
     */
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
                    Log.e("ITINERO - Itinerary ViewModel", "Error: ${result.exception.message}")
                    _uiState.value = ItineraryUiState.Error(
                        result.exception.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    /**
     * Fetches a specific itinerary activity by its ID and updates the UI state and selected item.
     *
     * If the operation succeeds, the selected item and UI state are updated with the retrieved activity.
     * If it fails, the UI state is set to an error with the corresponding message.
     *
     * @param itemId The unique identifier of the itinerary activity to fetch.
     * @param forceRefresh Whether to bypass any cached data and force a fresh fetch.
     */
    fun getActivityById(itemId: String, forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ItineraryUiState.Loading
            when (val result = itineraryUseCase.getActivityByIdUseCase(itemId, forceRefresh)) {
                is Result.Success -> {
                    _selectedItem.value = result.data
                    _uiState.value = ItineraryUiState.Success(result.data)
                }

                is Result.Error -> {
                    Log.e(
                        "ITINERO - Itinerary ViewModel",
                        "Failed to fetch activity with id: $itemId",
                        result.exception
                    )
                    _uiState.value = ItineraryUiState.Error(
                        result.exception.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    /**
     * Creates a new itinerary activity for the specified group and updates the UI state.
     *
     * On success, updates the UI state and refreshes the itinerary list to include the new activity.
     * On failure, sets the UI state to error with the relevant message.
     *
     * @param groupCode The code identifying the group for which the activity is created.
     * @param request The details of the activity to be created.
     */
    fun createActivity(groupCode: String, request: CreateItineraryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ItineraryUiState.Loading
            when (val result = itineraryUseCase.createActivityUseCase(groupCode, request)) {
                is Result.Success -> {
                    _uiState.value = ItineraryUiState.Success(result.data)
                    // Refresh the list to show the new item
                    fetchItinerary(groupCode, forceRefresh = true)
                }

                is Result.Error -> {
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

    /**
     * Updates an existing itinerary activity with new information.
     *
     * Updates the selected item and UI state upon success, and refreshes the itinerary list if a group code is set.
     * Sets the UI state to error if the update fails.
     *
     * @param itemId The ID of the itinerary activity to update.
     * @param request The updated activity information.
     */
    fun updateActivity(itemId: String, request: UpdateItineraryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ItineraryUiState.Loading
            when (val result = itineraryUseCase.updateActivityInfoUseCase(itemId, request)) {
                is Result.Success -> {
                    _selectedItem.value = result.data
                    _uiState.value = ItineraryUiState.Success(result.data)
                    // Refresh the list to show updated data
                    if (currentGroupCode.isNotEmpty()) {
                        fetchItinerary(currentGroupCode, forceRefresh = true)
                    }
                }

                is Result.Error -> {
                    Log.e(
                        "ITINERO - Itinerary ViewModel",
                        "Failed to update activity with id: $itemId",
                        result.exception
                    )
                    _uiState.value = ItineraryUiState.Error(
                        result.exception.message ?: "Failed to update activity"
                    )
                }
            }
        }
    }

    /**
     * Deletes an itinerary activity by its ID and updates the UI state and local data accordingly.
     *
     * Removes the deleted item from the local itinerary list and clears the selected item if it matches the deleted ID. If a group code is set, refreshes the itinerary data after deletion.
     *
     * @param itemId The unique identifier of the itinerary activity to delete.
     */
    fun deleteActivity(itemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ItineraryUiState.Loading
            when (val result = itineraryUseCase.deleteActivityByIdUseCase(itemId)) {
                is Result.Success -> {
                    _uiState.value = ItineraryUiState.Success(Unit)
                    // Remove the item from local state
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
                    Log.e(
                        "ITINERO - Itinerary ViewModel",
                        "Failed to delete activity with id: $itemId",
                        result.exception
                    )
                    _uiState.value = ItineraryUiState.Error(
                        result.exception.message ?: "Failed to delete activity"
                    )
                }
            }
        }
    }

    /**
     * Toggles the completion status of an itinerary activity by its ID.
     *
     * Updates the local itinerary data and selected item to reflect the new completion status.
     * If a group code is set, refreshes the itinerary data after toggling.
     *
     * @param itemId The ID of the itinerary activity to toggle.
     */
    fun toggleActivityCompletion(itemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = itineraryUseCase.toggleActivityCompletionUseCase(itemId)) {
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

    /**
     * Clears the currently selected itinerary item.
     */
    fun clearSelectedItem() {
        _selectedItem.value = null
    }

    /**
     * Resets the UI state to Idle, indicating no operation is in progress.
     */
    fun resetState() {
        _uiState.value = ItineraryUiState.Idle
    }

    /**
     * Forces a refresh of the itinerary data for the current group code, if set.
     *
     * If a current group code exists, triggers a reload of all itinerary items from the source.
     */
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
