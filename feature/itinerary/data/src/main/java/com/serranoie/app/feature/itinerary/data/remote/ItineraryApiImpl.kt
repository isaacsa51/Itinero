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
import com.serranoie.itinero.core.data.remote.resources.BaseApiClient
import io.ktor.client.HttpClient

class ItineraryApiImpl(
    client: HttpClient
) : BaseApiClient(client), ItineraryApi {

    override suspend fun getAllItineraryItems(groupCode: String): List<ItineraryItemDto> {
        return get("/trips/$groupCode/itinerary")
    }

    override suspend fun createItineraryItem(
        groupCode: String,
        request: CreateItineraryItemDto
    ): ItineraryItemDto {
        return post("/trips/$groupCode/itinerary", request)
    }

    override suspend fun getItineraryItem(groupCode: String, itemId: String): ItineraryItemDto {
        return get("/trips/$groupCode/itinerary/$itemId")
    }

    override suspend fun updateItineraryItem(
        groupCode: String,
        itemId: String,
        request: UpdateItineraryItemDto
    ): ItineraryItemDto {
        return put("/trips/$groupCode/itinerary/$itemId", request)
    }

    override suspend fun deleteItineraryItem(groupCode: String, itemId: String) {
        delete<Unit>("/trips/$groupCode/itinerary/$itemId")
    }

    override suspend fun toggleItineraryItemCompletion(groupCode: String, itemId: String) {
        patch<Unit>("/trips/$groupCode/itinerary/$itemId/complete")
    }
}
