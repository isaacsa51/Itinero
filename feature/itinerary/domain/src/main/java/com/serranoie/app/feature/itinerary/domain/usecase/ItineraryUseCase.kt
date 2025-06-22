/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineraryUseCase.kt
 - Project: Itinero
 - Module: Itinero.feature.itinerary.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 18 junio 2025
 */

package com.serranoie.app.feature.itinerary.domain.usecase

import com.serranoie.app.feature.itinerary.domain.model.CreateItineraryItem
import com.serranoie.app.feature.itinerary.domain.model.ItineraryItem
import com.serranoie.app.feature.itinerary.domain.model.UpdateItineraryItem
import com.serranoie.app.feature.itinerary.domain.repository.ItineraryRepository
import com.serranoie.itinero.core.domain.result.Result

data class ItineraryUseCase(
    val getAllActivitiesUseCase: GetAllActivitiesUseCase,
    val getActivityByIdUseCase: GetActivityByIdUseCase,
    val createActivityUseCase: CreateActivityUseCase,
    val deleteActivityByIdUseCase: DeleteActivityByIdUseCase,
    val updateActivityInfoUseCase: UpdateActivityInfoUseCase,
    val toggleActivityCompletionUseCase: ToggleActivityCompletionUseCase
)

class GetAllActivitiesUseCase(private val repository: ItineraryRepository) {
    /**
     * Retrieves all itinerary activities for the specified group.
     *
     * @param groupCode The unique code identifying the group whose activities are to be fetched.
     * @param forceRefresh If true, forces a refresh from the data source instead of using cached data.
     * @return A [Result] containing a list of [ItineraryItem]s on success, or an error on failure.
     */
    suspend operator fun invoke(
        groupCode: String,
        forceRefresh: Boolean = false
    ): Result<List<ItineraryItem>> {
        return repository.getAllActivities(groupCode, forceRefresh)
    }
}

class GetActivityByIdUseCase(private val repository: ItineraryRepository) {
    /**
     * Retrieves an itinerary activity by its ID.
     *
     * @param itineraryId The unique identifier of the itinerary activity.
     * @param forceRefresh If true, forces data to be refreshed from the source.
     * @return A [Result] containing the requested [ItineraryItem] on success, or an error on failure.
     */
    suspend operator fun invoke(
        itineraryId: String,
        forceRefresh: Boolean = false
    ): Result<ItineraryItem> {
        return repository.getActivityById(itineraryId, forceRefresh)
    }
}

class CreateActivityUseCase(private val repository: ItineraryRepository) {
    /****
     * Creates a new itinerary activity within the specified group.
     *
     * @param groupCode The code identifying the group to which the activity will be added.
     * @param request The details of the activity to create.
     * @return A [Result] containing the created [ItineraryItem] on success, or an error on failure.
     */
    suspend operator fun invoke(
        groupCode: String,
        request: CreateItineraryItem
    ): Result<ItineraryItem> {
        return repository.createActivity(groupCode, request)
    }
}

class DeleteActivityByIdUseCase(private val repository: ItineraryRepository) {
    /**
     * Deletes an itinerary activity by its ID.
     *
     * @param itineraryId The unique identifier of the itinerary activity to delete.
     * @return A [Result] indicating success or containing an error if the deletion fails.
     */
    suspend operator fun invoke(itineraryId: String): Result<Unit> {
        return try {
            repository.deleteActivityById(itineraryId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

class UpdateActivityInfoUseCase(private val repository: ItineraryRepository) {
    /**
     * Updates the information of an itinerary activity identified by its ID.
     *
     * @param itineraryId The unique identifier of the itinerary activity to update.
     * @param request The update request containing new activity information.
     * @return A [Result] containing the updated [ItineraryItem] on success, or an error on failure.
     */
    suspend operator fun invoke(
        itineraryId: String,
        request: UpdateItineraryItem
    ): Result<ItineraryItem> {
        return repository.updateActivityInfo(itineraryId, request)
    }
}

class ToggleActivityCompletionUseCase(private val repository: ItineraryRepository) {
    /**
     * Toggles the completion status of an itinerary activity by its ID.
     *
     * @param itineraryId The unique identifier of the itinerary activity to toggle.
     * @return A [Result] indicating success or failure of the toggle operation.
     */
    suspend operator fun invoke(itineraryId: String): Result<Unit> {
        return repository.toggleActivityCompletion(itineraryId)
    }
}
