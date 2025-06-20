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
import com.serranoie.app.feature.itinerary.data.mappers.toDomain
import com.serranoie.app.feature.itinerary.data.mappers.toEntity
import com.serranoie.app.feature.itinerary.domain.model.ItineraryItem
import com.serranoie.itinero.core.domain.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
        return try {
            val entities = itineraryDao.getAllItineraryItems()
            val items = entities.map { it.toDomain() }
            Result.Success(items)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getCachedItineraryItemById(itineraryId: String): Result<ItineraryItem?> {
        return try {
            val entity = itineraryDao.getItineraryItemById(itineraryId.toInt())
            val item = entity?.toDomain()
            Result.Success(item)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateItineraryItem(itinerary: ItineraryItem): Result<Unit> {
        return try {
            itineraryDao.insertOrUpdateItineraryItem(itinerary.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteItineraryItemById(itineraryId: String): Result<Unit> {
        return try {
            itineraryDao.deleteItineraryItem(itineraryId.toInt())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun clearAllItineraryItems(): Result<Unit> {
        return try {
            itineraryDao.clearAllItineraryItems()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getCachedItineraryFlow(): Flow<List<ItineraryItem>> {
        return itineraryDao.getAllItineraryItemsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
