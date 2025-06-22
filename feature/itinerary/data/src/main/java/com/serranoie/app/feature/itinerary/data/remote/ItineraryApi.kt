/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineraryApi.kt
 - Project: Itinero
 - Module: Itinero.feature.itinerary.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 18 junio 2025
 */

package com.serranoie.app.feature.itinerary.data.remote

import com.serranoie.app.feature.itinerary.data.remote.dto.CreateItineraryItemDto
import com.serranoie.app.feature.itinerary.data.remote.dto.ItineraryItemDto
import com.serranoie.app.feature.itinerary.data.remote.dto.UpdateItineraryItemDto

interface ItineraryApi {
    /****
 * Retrieves all itinerary items associated with the specified group code.
 *
 * @param groupCode The unique code identifying the group whose itinerary items are to be fetched.
 * @return A list of itinerary items belonging to the given group.
 */
suspend fun getAllItineraryItems(groupCode: String): List<ItineraryItemDto>
    /****
     * Creates a new itinerary item within the specified group.
     *
     * @param groupCode The code identifying the group to which the new itinerary item will be added.
     * @param request The data required to create the itinerary item.
     * @return The created itinerary item.
     */
    suspend fun createItineraryItem(
        groupCode: String, request: CreateItineraryItemDto
    ): ItineraryItemDto

    /**
 * Retrieves a single itinerary item by its unique identifier.
 *
 * @param itemId The unique identifier of the itinerary item to retrieve.
 * @return The itinerary item corresponding to the provided ID.
 */
suspend fun getItineraryItem(itemId: String): ItineraryItemDto
    /****
     * Updates an existing itinerary item with the specified ID using the provided update data.
     *
     * @param itemId The unique identifier of the itinerary item to update.
     * @param request The data used to update the itinerary item.
     * @return The updated itinerary item.
     */
    suspend fun updateItineraryItem(
        itemId: String, request: UpdateItineraryItemDto
    ): ItineraryItemDto

    /**
 * Deletes the itinerary item identified by the given item ID.
 *
 * @param itemId The unique identifier of the itinerary item to delete.
 */
suspend fun deleteItineraryItem(itemId: String)
    /**
 * Toggles the completion status of the itinerary item identified by the given ID.
 *
 * @param itemId The unique identifier of the itinerary item to update.
 */
suspend fun toggleItineraryItemCompletion(itemId: String)
}
