/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineraryApiImpl.kt
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
import com.serranoie.itinero.core.data.remote.BaseApiClient
import io.ktor.client.HttpClient

class ItineraryApiImpl(
    client: HttpClient
) : BaseApiClient(client), ItineraryApi {

    /****
     * Retrieves all itinerary items for the specified trip group.
     *
     * @param groupCode The unique code identifying the trip group.
     * @return A list of itinerary items associated with the trip group.
     */
    override suspend fun getAllItineraryItems(groupCode: String): List<ItineraryItemDto> {
        return get("/trips/$groupCode/itinerary")
    }

    /****
     * Creates a new itinerary item for the specified trip group.
     *
     * @param groupCode The unique code identifying the trip group.
     * @param request The data for the itinerary item to be created.
     * @return The created itinerary item.
     */
    override suspend fun createItineraryItem(
        groupCode: String,
        request: CreateItineraryItemDto
    ): ItineraryItemDto {
        return post("/trips/$groupCode/itinerary", request)
    }

    /**
     * Retrieves a specific itinerary item by its unique identifier.
     *
     * @param itemId The unique identifier of the itinerary item to retrieve.
     * @return The itinerary item corresponding to the provided ID.
     */
    override suspend fun getItineraryItem(itemId: String): ItineraryItemDto {
        return get("/itinerary/$itemId")
    }

    /**
     * Updates an existing itinerary item with the provided data.
     *
     * @param itemId The unique identifier of the itinerary item to update.
     * @param request The updated data for the itinerary item.
     * @return The updated itinerary item.
     */
    override suspend fun updateItineraryItem(
        itemId: String,
        request: UpdateItineraryItemDto
    ): ItineraryItemDto {
        return put("/itinerary/$itemId", request)
    }

    /****
     * Deletes an itinerary item identified by its ID.
     *
     * @param itemId The unique identifier of the itinerary item to delete.
     */
    override suspend fun deleteItineraryItem(itemId: String) {
        delete<Unit>("/itinerary/$itemId")
    }

    /****
     * Toggles the completion status of the specified itinerary item.
     *
     * @param itemId The unique identifier of the itinerary item to update.
     */
    override suspend fun toggleItineraryItemCompletion(itemId: String) {
        patch<Unit>("/itinerary/$itemId/complete")
    }
}
