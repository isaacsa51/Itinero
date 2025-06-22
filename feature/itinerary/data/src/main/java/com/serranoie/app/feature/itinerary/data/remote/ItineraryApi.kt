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
    suspend fun getAllItineraryItems(groupCode: String): List<ItineraryItemDto>
    suspend fun createItineraryItem(
        groupCode: String, request: CreateItineraryItemDto
    ): ItineraryItemDto

    suspend fun getItineraryItem(itemId: String): ItineraryItemDto
    suspend fun updateItineraryItem(
        itemId: String, request: UpdateItineraryItemDto
    ): ItineraryItemDto

    suspend fun deleteItineraryItem(itemId: String)
    suspend fun toggleItineraryItemCompletion(itemId: String)
}
