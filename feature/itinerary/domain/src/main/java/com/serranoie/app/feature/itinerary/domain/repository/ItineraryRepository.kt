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
    suspend fun getAllActivities(
        groupCode: String,
        forceRefresh: Boolean = false
    ): Result<List<ItineraryItem>>

    suspend fun getActivityById(
        itemId: String,
        forceRefresh: Boolean = false
    ): Result<ItineraryItem>

    suspend fun createActivity(
        groupCode: String,
        request: CreateItineraryItem
    ): Result<ItineraryItem>

    suspend fun deleteActivityById(itemId: String): Result<Unit>
    suspend fun updateActivityInfo(
        itemId: String,
        request: UpdateItineraryItem
    ): Result<ItineraryItem>

    suspend fun toggleActivityCompletion(itemId: String): Result<Unit>
}
