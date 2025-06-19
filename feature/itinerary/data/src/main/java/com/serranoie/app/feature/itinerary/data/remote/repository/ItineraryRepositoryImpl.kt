/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineraryRepositoryImpl.kt
 - Project: Itinero
 - Module: Itinero.feature.itinerary.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 18 junio 2025
 */

package com.serranoie.app.feature.itinerary.data.remote.repository

import com.serranoie.app.feature.itinerary.data.local.repository.LocalItineraryRepository
import com.serranoie.app.feature.itinerary.domain.model.ItineraryItem
import com.serranoie.app.feature.itinerary.domain.repository.ItineraryRepository
import com.serranoie.itinero.core.data.remote.ItineroApi
import com.serranoie.itinero.core.domain.result.Result

class ItineraryRepositoryImpl(
    private val api: ItineroApi, private val localRepository: LocalItineraryRepository
) : ItineraryRepository {
    override suspend fun getAllActivities(): Result<List<ItineraryItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getActivityById(itineraryId: String): Result<ItineraryItem> {
        TODO("Not yet implemented")
    }

    override suspend fun createActivity(): Result<ItineraryItem> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteActivityById(itineraryId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateActivityInfo(itineraryId: String): Result<ItineraryItem> {
        TODO("Not yet implemented")
    }


}