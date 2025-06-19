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

import com.serranoie.app.feature.itinerary.domain.model.ItineraryItem
import com.serranoie.itinero.core.domain.result.Result

interface ItineraryRepository {
    suspend fun getAllActivities(): Result<List<ItineraryItem>>
    suspend fun getActivityById(itineraryId: String): Result<ItineraryItem>
    suspend fun createActivity(): Result<ItineraryItem>
    suspend fun deleteActivityById(itineraryId: String)
    suspend fun updateActivityInfo(itineraryId: String): Result<ItineraryItem>
}