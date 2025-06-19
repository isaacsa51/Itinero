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

import com.serranoie.app.feature.itinerary.domain.model.ItineraryItem
import com.serranoie.app.feature.itinerary.domain.repository.ItineraryRepository
import com.serranoie.itinero.core.domain.result.Result

data class ItineraryUseCase(
    val getAllActivitiesUseCase: GetAllActivitiesUseCase,
    val getActivityByIdUseCase: GetActivityByIdUseCase,
    val createActivityUseCase: CreateActivityUseCase,
    val deleteActivityByIdUseCase: DeleteActivityByIdUseCase,
    val updateActivityInfoUseCase: UpdateActivityInfoUseCase
)

class GetAllActivitiesUseCase(private val repository: ItineraryRepository) {
    suspend operator fun invoke(): Result<List<ItineraryItem>> {
        return repository.getAllActivities()
    }
}

class GetActivityByIdUseCase(private val repository: ItineraryRepository) {
    suspend operator fun invoke(itineraryId: String): Result<ItineraryItem> {
        return repository.getActivityById(itineraryId)
    }
}

class CreateActivityUseCase(private val repository: ItineraryRepository) {
    suspend operator fun invoke(): Result<ItineraryItem> {
        return repository.createActivity()
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
    suspend operator fun invoke(itineraryId: String): Result<ItineraryItem> {
        return repository.updateActivityInfo(itineraryId)
    }
}
