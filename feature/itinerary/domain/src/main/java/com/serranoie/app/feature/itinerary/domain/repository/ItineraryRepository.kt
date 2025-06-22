/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineraryRepository.kt
 - Project: Itinero
 - Module: Itinero.feature.itinerary.domain.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 18 junio 2025
 */

package com.serranoie.app.feature.itinerary.domain.repository

import com.serranoie.app.feature.itinerary.domain.model.CreateItineraryItem
import com.serranoie.app.feature.itinerary.domain.model.ItineraryItem
import com.serranoie.app.feature.itinerary.domain.model.UpdateItineraryItem
import com.serranoie.itinero.core.domain.result.Result

interface ItineraryRepository {
    /**
     * Retrieves all itinerary activities associated with the specified group code.
     *
     * @param groupCode The identifier for the group whose activities are to be fetched.
     * @param forceRefresh If true, forces a refresh of the data from the source.
     * @return A [Result] containing a list of [ItineraryItem] objects on success, or an error on failure.
     */
    suspend fun getAllActivities(
        groupCode: String,
        forceRefresh: Boolean = false
    ): Result<List<ItineraryItem>>

    /**
     * Retrieves an itinerary item by its unique ID.
     *
     * @param itemId The unique identifier of the itinerary item to retrieve.
     * @param forceRefresh If true, forces a refresh from the data source instead of using cached data.
     * @return A [Result] containing the requested [ItineraryItem] on success, or an error on failure.
     */
    suspend fun getActivityById(
        itemId: String,
        forceRefresh: Boolean = false
    ): Result<ItineraryItem>

    /**
     * Creates a new itinerary activity within the specified group.
     *
     * @param groupCode The identifier for the group to which the activity will be added.
     * @param request The details of the activity to create.
     * @return A [Result] containing the created [ItineraryItem] on success, or an error on failure.
     */
    suspend fun createActivity(
        groupCode: String,
        request: CreateItineraryItem
    ): Result<ItineraryItem>

    /**
 * Deletes an itinerary activity identified by its ID.
 *
 * @param itemId The unique identifier of the itinerary activity to delete.
 * @return A [Result] indicating success or failure of the deletion operation.
 */
suspend fun deleteActivityById(itemId: String): Result<Unit>
    /**
     * Updates the details of an existing itinerary item identified by its ID.
     *
     * @param itemId The unique identifier of the itinerary item to update.
     * @param request The updated information for the itinerary item.
     * @return A [Result] containing the updated [ItineraryItem] on success, or an error on failure.
     */
    suspend fun updateActivityInfo(
        itemId: String,
        request: UpdateItineraryItem
    ): Result<ItineraryItem>

    /**
 * Toggles the completion status of the itinerary item identified by the given ID.
 *
 * @param itemId The unique identifier of the itinerary item to update.
 * @return A [Result] indicating success or failure of the operation.
 */
suspend fun toggleActivityCompletion(itemId: String): Result<Unit>
}
