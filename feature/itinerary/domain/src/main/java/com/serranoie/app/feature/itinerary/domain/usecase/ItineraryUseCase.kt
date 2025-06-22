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
    suspend operator fun invoke(
        groupCode: String,
        forceRefresh: Boolean = false
    ): Result<List<ItineraryItem>> {
        return repository.getAllActivities(groupCode, forceRefresh)
    }
}

class GetActivityByIdUseCase(private val repository: ItineraryRepository) {
    suspend operator fun invoke(
        itineraryId: String,
        forceRefresh: Boolean = false
    ): Result<ItineraryItem> {
        return repository.getActivityById(itineraryId, forceRefresh)
    }
}

class CreateActivityUseCase(private val repository: ItineraryRepository) {
    suspend operator fun invoke(
        groupCode: String,
        request: CreateItineraryItem
    ): Result<ItineraryItem> {
        return repository.createActivity(groupCode, request)
    }
}

class DeleteActivityByIdUseCase(private val repository: ItineraryRepository) {
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
    suspend operator fun invoke(
        itineraryId: String,
        request: UpdateItineraryItem
    ): Result<ItineraryItem> {
        return repository.updateActivityInfo(itineraryId, request)
    }
}

class ToggleActivityCompletionUseCase(private val repository: ItineraryRepository) {
    suspend operator fun invoke(itineraryId: String): Result<Unit> {
        return repository.toggleActivityCompletion(itineraryId)
    }
}
