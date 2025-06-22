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

import com.serranoie.itinero.core.data.local.dao.ItineraryDao
import com.serranoie.app.feature.itinerary.data.mappers.toDomain
import com.serranoie.app.feature.itinerary.data.mappers.toEntity
import com.serranoie.app.feature.itinerary.domain.model.ItineraryItem
import com.serranoie.itinero.core.domain.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LocalItineraryRepository {
    /**
 * Retrieves all cached itinerary items from local storage.
 *
 * @return A [Result] containing a list of all cached [ItineraryItem]s on success, or an error on failure.
 */
suspend fun getAllCachedItineraryItems(): Result<List<ItineraryItem>>
    /**
 * Retrieves a cached itinerary item by its unique identifier.
 *
 * @param itineraryId The unique identifier of the itinerary item to retrieve.
 * @return A [Result] containing the itinerary item if found, or null if not found.
 */
suspend fun getCachedItineraryItemById(itineraryId: String): Result<ItineraryItem?>
    /**
 * Updates or inserts the given itinerary item in the local cache.
 *
 * @param itinerary The itinerary item to be updated or inserted.
 * @return A [Result] indicating success or containing an error if the operation fails.
 */
suspend fun updateItineraryItem(itinerary: ItineraryItem): Result<Unit>
    /**
 * Deletes the cached itinerary item with the specified ID.
 *
 * @param itineraryId The unique identifier of the itinerary item to delete.
 * @return A [Result] indicating success or containing an error if the operation fails.
 */
suspend fun deleteItineraryItemById(itineraryId: String): Result<Unit>
    /**
 * Removes all cached itinerary items from local storage.
 *
 * @return A [Result] indicating success or containing an error if the operation fails.
 */
suspend fun clearAllItineraryItems(): Result<Unit>
    /**
 * Returns a reactive flow emitting the current list of cached itinerary items.
 *
 * The flow emits updates whenever the underlying itinerary data changes.
 * @return A Flow emitting lists of itinerary items.
 */
fun getCachedItineraryFlow(): Flow<List<ItineraryItem>>
}

class LocalItineraryRepositoryImpl(
    private val itineraryDao: ItineraryDao
) : LocalItineraryRepository {
    /**
     * Retrieves all cached itinerary items from the local database.
     *
     * @return A [Result] containing a list of [ItineraryItem] on success, or an error if retrieval fails.
     */
    override suspend fun getAllCachedItineraryItems(): Result<List<ItineraryItem>> {
        return try {
            val entities = itineraryDao.getAllItineraryItems()
            val items = entities.map { it.toDomain() }
            Result.Success(items)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Retrieves a cached itinerary item by its ID.
     *
     * @param itineraryId The unique identifier of the itinerary item.
     * @return A [Result] containing the itinerary item if found, or null if not found; returns [Result.Error] if an exception occurs.
     */
    override suspend fun getCachedItineraryItemById(itineraryId: String): Result<ItineraryItem?> {
        return try {
            val entity = itineraryDao.getItineraryItemById(itineraryId.toInt())
            val item = entity?.toDomain()
            Result.Success(item)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Inserts or updates an itinerary item in the local database.
     *
     * @param itinerary The itinerary item to be inserted or updated.
     * @return A [Result] indicating success or containing an error if the operation fails.
     */
    override suspend fun updateItineraryItem(itinerary: ItineraryItem): Result<Unit> {
        return try {
            itineraryDao.insertOrUpdateItineraryItem(itinerary.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Deletes a cached itinerary item by its ID.
     *
     * @param itineraryId The unique identifier of the itinerary item to delete.
     * @return A [Result] indicating success or containing an error if the operation fails.
     */
    override suspend fun deleteItineraryItemById(itineraryId: String): Result<Unit> {
        return try {
            itineraryDao.deleteItineraryItem(itineraryId.toInt())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Removes all cached itinerary items from the local database.
     *
     * @return A [Result] indicating success or containing an error if the operation fails.
     */
    override suspend fun clearAllItineraryItems(): Result<Unit> {
        return try {
            itineraryDao.clearAllItineraryItems()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Returns a reactive flow emitting the current list of cached itinerary items.
     *
     * The flow emits updates whenever the underlying itinerary data changes.
     * @return A [Flow] of lists containing [ItineraryItem] objects.
     */
    override fun getCachedItineraryFlow(): Flow<List<ItineraryItem>> {
        return itineraryDao.getAllItineraryItemsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
