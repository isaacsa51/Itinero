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

import android.util.Log
import com.serranoie.itinero.core.data.local.dao.ItineraryDao
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

    companion object {
        private const val TAG = "LocalItineraryRepository"
    }

    override suspend fun getAllCachedItineraryItems(): Result<List<ItineraryItem>> {
        return try {
            val entities = itineraryDao.getAllItineraryItems()
            val items = entities.map { it.toDomain() }
            Result.Success(items)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all cached itinerary items", e)
            Result.Error(e)
        }
    }

    override suspend fun getCachedItineraryItemById(itineraryId: String): Result<ItineraryItem?> {
        return try {
            val numericId = validateAndConvertId(itineraryId)
                ?: return Result.Error(
                    IllegalArgumentException("Invalid itinerary ID format: '$itineraryId'. Expected numeric value.")
                )

            val entity = itineraryDao.getItineraryItemById(numericId)
            val item = entity?.toDomain()
            Result.Success(item)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cached itinerary item by ID: $itineraryId", e)
            Result.Error(e)
        }
    }

    override suspend fun updateItineraryItem(itinerary: ItineraryItem): Result<Unit> {
        return try {
            itineraryDao.insertOrUpdateItineraryItem(itinerary.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating itinerary item: ${itinerary.id}", e)
            Result.Error(e)
        }
    }

    override suspend fun deleteItineraryItemById(itineraryId: String): Result<Unit> {
        return try {
            val numericId = validateAndConvertId(itineraryId)
                ?: return Result.Error(
                    IllegalArgumentException("Invalid itinerary ID format: '$itineraryId'. Expected numeric value.")
                )

            itineraryDao.deleteItineraryItem(numericId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting itinerary item by ID: $itineraryId", e)
            Result.Error(e)
        }
    }

    override suspend fun clearAllItineraryItems(): Result<Unit> {
        return try {
            itineraryDao.clearAllItineraryItems()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing all itinerary items", e)
            Result.Error(e)
        }
    }

    override fun getCachedItineraryFlow(): Flow<List<ItineraryItem>> {
        return itineraryDao.getAllItineraryItemsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Safely validates and converts a String ID to Int
     * @param idString The string representation of the ID
     * @return The numeric ID if valid, null if invalid
     */
    private fun validateAndConvertId(idString: String): Int? {
        return try {
            // Check if the string is not empty and contains only digits (with optional negative sign)
            if (idString.isBlank()) {
                Log.w(TAG, "Empty ID string provided")
                return null
            }

            // Remove any whitespace
            val trimmedId = idString.trim()

            // Validate format: should be a valid integer
            val numericId = trimmedId.toIntOrNull()

            if (numericId == null) {
                Log.w(TAG, "Invalid ID format: '$idString' cannot be converted to integer")
                return null
            }

            // Additional validation: ensure the ID is positive (business rule)
            if (numericId <= 0) {
                Log.w(TAG, "Invalid ID value: '$idString' should be a positive integer")
                return null
            }

            numericId
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error converting ID '$idString' to integer", e)
            null
        }
    }
}
