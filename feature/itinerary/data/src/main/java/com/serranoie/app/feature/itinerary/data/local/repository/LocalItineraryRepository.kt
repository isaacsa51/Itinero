/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: LocalItineraryRepository.kt
 - Project: Itinero
 - Module: Itinero.feature.itinerary.data.main
 -
 - This file belongs to the project: Itinero. 
 - Last edited: 18 junio 2025
 */

package com.serranoie.app.feature.itinerary.data.local.repository

import com.serranoie.app.feature.itinerary.data.local.dao.ItineraryDao
import com.serranoie.app.feature.itinerary.domain.model.ItineraryItem
import kotlinx.coroutines.flow.Flow

interface LocalItineraryRepository {
    suspend fun getAllCachedItineraryItems(): Result<List<ItineraryItem>>
    suspend fun getCachedItineraryItemById(itineraryId: String): Result<ItineraryItem?>
    suspend fun updateItineraryItem(itinerary: ItineraryItem): Result<Unit>
    suspend fun deleteItineraryItemById(itineraryId: String): Result<Unit>
    suspend fun clearAllItineraryItems(): Result<Unit>
    fun getCachedItineraryFlow(): Flow<List<ItineraryItem>>
}

class LocalItineraryRepositoryImpl(
    private val itineraryDao: ItineraryDao
) : LocalItineraryRepository {
    override suspend fun getAllCachedItineraryItems(): Result<List<ItineraryItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCachedItineraryItemById(itineraryId: String): Result<ItineraryItem?> {
        TODO("Not yet implemented")
    }

    override suspend fun updateItineraryItem(itinerary: ItineraryItem): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteItineraryItemById(itineraryId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun clearAllItineraryItems(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun getCachedItineraryFlow(): Flow<List<ItineraryItem>> {
        TODO("Not yet implemented")
    }

}