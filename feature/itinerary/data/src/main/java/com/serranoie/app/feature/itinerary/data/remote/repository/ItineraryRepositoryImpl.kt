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

import android.util.Log
import com.serranoie.app.feature.itinerary.data.local.repository.LocalItineraryRepository
import com.serranoie.app.feature.itinerary.data.mappers.toDomain
import com.serranoie.app.feature.itinerary.data.mappers.toDto
import com.serranoie.app.feature.itinerary.data.remote.ItineraryApi
import com.serranoie.app.feature.itinerary.domain.model.CreateItineraryItem
import com.serranoie.app.feature.itinerary.domain.model.ItineraryItem
import com.serranoie.app.feature.itinerary.domain.model.UpdateItineraryItem
import com.serranoie.app.feature.itinerary.domain.repository.ItineraryRepository
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.result.safeApiCall
import kotlinx.coroutines.flow.Flow

class ItineraryRepositoryImpl(
    private val api: ItineraryApi,
    private val localRepository: LocalItineraryRepository
) : ItineraryRepository {

    /**
     * Retrieves all itinerary items for the specified group code using a cache-first strategy.
     *
     * Attempts to return cached items unless a forced refresh is requested. If the cache is empty or a refresh is forced, fetches items from the remote API and updates the cache. If the remote fetch fails and cached data exists, returns the cached items.
     *
     * @param groupCode The group code to filter itinerary items.
     * @param forceRefresh If true, bypasses the cache and fetches from the remote API.
     * @return A [Result] containing a list of itinerary items on success, or an error on failure.
     */
    override suspend fun getAllActivities(
        groupCode: String,
        forceRefresh: Boolean
    ): Result<List<ItineraryItem>> {
        // If not forcing refresh, try cache first
        if (!forceRefresh) {
            when (val cachedResult = localRepository.getAllCachedItineraryItems()) {
                is Result.Success -> {
                    val cachedItems = cachedResult.data.filter { it.groupCode == groupCode }
                    if (cachedItems.isNotEmpty()) {
                        return Result.Success(cachedItems)
                    }
                }
                is Result.Error -> {
                    Log.e(
                        "ITINERO - ItineraryRepository",
                        "Cache error: ${cachedResult.exception.message}"
                    )
                }
            }
        }

        // Fetch from remote
        return when (val remoteResult = safeApiCall {
            api.getAllItineraryItems(groupCode).map { dto ->
                dto.toDomain().copy(groupCode = groupCode)
            }
        }) {
            is Result.Success -> {
                // Cache the fresh data
                remoteResult.data.forEach { item ->
                    localRepository.updateItineraryItem(item)
                }
                remoteResult
            }
            is Result.Error -> {
                // If remote fails and we have cached data, return cached data
                if (!forceRefresh) {
                    when (val cachedResult = localRepository.getAllCachedItineraryItems()) {
                        is Result.Success -> {
                            val cachedItems = cachedResult.data.filter { it.groupCode == groupCode }
                            if (cachedItems.isNotEmpty()) {
                                return Result.Success(cachedItems)
                            }
                        }
                        is Result.Error -> {
                            Log.e(
                                "ITINERO - ItineraryRepository",
                                "Remote and cache failed: ${remoteResult.exception.message}"
                            )
                        }
                    }
                }
                remoteResult
            }
        }
    }

    /**
     * Retrieves a single itinerary item by its ID, using a cache-first strategy with optional forced refresh.
     *
     * If `forceRefresh` is false, attempts to return the cached item. If not found or if forced refresh is requested, fetches the item from the remote API and updates the cache on success. If the remote fetch fails and cached data exists, returns the cached item instead.
     *
     * @param itemId The unique identifier of the itinerary item to retrieve.
     * @param forceRefresh If true, bypasses the cache and fetches the item from the remote API.
     * @return A [Result] containing the itinerary item on success, or an error if both cache and remote fetch fail.
     */
    override suspend fun getActivityById(
        itemId: String,
        forceRefresh: Boolean
    ): Result<ItineraryItem> {
        // If not forcing refresh, try cache first
        if (!forceRefresh) {
            when (val cachedResult = localRepository.getCachedItineraryItemById(itemId)) {
                is Result.Success -> {
                    cachedResult.data?.let { cachedItem ->
                        return Result.Success(cachedItem)
                    }
                }
                is Result.Error -> {
                    Log.e(
                        "ITINERO - ItineraryRepository",
                        "Cache error: ${cachedResult.exception.message}"
                    )
                }
            }
        }

        // Fetch from remote
        return when (val remoteResult = safeApiCall {
            api.getItineraryItem(itemId).toDomain()
        }) {
            is Result.Success -> {
                // Cache the fresh data
                localRepository.updateItineraryItem(remoteResult.data)
                remoteResult
            }
            is Result.Error -> {
                // If remote fails and we have cached data, return cached data
                if (!forceRefresh) {
                    when (val cachedResult = localRepository.getCachedItineraryItemById(itemId)) {
                        is Result.Success -> {
                            cachedResult.data?.let { cachedItem ->
                                return Result.Success(cachedItem)
                            }
                        }
                        is Result.Error -> {
                            Log.e(
                                "ITINERO - ItineraryRepository",
                                "Remote and cache failed: ${remoteResult.exception.message}"
                            )
                        }
                    }
                }
                remoteResult
            }
        }
    }

    /**
     * Creates a new itinerary item for the specified group via the remote API and caches it locally on success.
     *
     * @param groupCode The code identifying the group to which the new itinerary item will belong.
     * @param request The details of the itinerary item to create.
     * @return A [Result] containing the created [ItineraryItem] on success, or an error on failure.
     */
    override suspend fun createActivity(
        groupCode: String,
        request: CreateItineraryItem
    ): Result<ItineraryItem> {
        return when (val result = safeApiCall {
            api.createItineraryItem(groupCode, request.toDto()).toDomain()
                .copy(groupCode = groupCode)
        }) {
            is Result.Success -> {
                // Cache the new item
                localRepository.updateItineraryItem(result.data)
                result
            }
            is Result.Error -> result
        }
    }

    /**
     * Updates an existing itinerary item remotely and refreshes the local cache with the updated data.
     *
     * @param itemId The unique identifier of the itinerary item to update.
     * @param request The update details for the itinerary item.
     * @return The result containing the updated itinerary item on success, or an error on failure.
     */
    override suspend fun updateActivityInfo(
        itemId: String,
        request: UpdateItineraryItem
    ): Result<ItineraryItem> {
        return when (val result = safeApiCall {
            api.updateItineraryItem(itemId, request.toDto()).toDomain()
        }) {
            is Result.Success -> {
                // Update cache with fresh data
                localRepository.updateItineraryItem(result.data)
                result
            }
            is Result.Error -> {
                Log.e("ITINERO - ItineraryRepository", "Update failed: ${result.exception.message}")
                result
            }
        }
    }

    /**
     * Deletes an itinerary item by its ID from the remote source and removes it from the local cache on success.
     *
     * @param itemId The unique identifier of the itinerary item to delete.
     * @return A [Result] indicating success or containing an error if the operation fails.
     */
    override suspend fun deleteActivityById(itemId: String): Result<Unit> {
        return when (val result = safeApiCall { api.deleteItineraryItem(itemId) }) {
            is Result.Success -> {
                // Remove from cache
                localRepository.deleteItineraryItemById(itemId)
                result
            }
            is Result.Error -> result
        }
    }

    /**
     * Toggles the completion status of the specified itinerary item via the remote API.
     *
     * After toggling, forces a refresh of the item's data to update the local cache.
     *
     * @param itemId The unique identifier of the itinerary item to toggle.
     * @return A [Result] indicating success or failure of the toggle operation.
     */
    override suspend fun toggleActivityCompletion(itemId: String): Result<Unit> {
        return when (val result = safeApiCall { api.toggleItineraryItemCompletion(itemId) }) {
            is Result.Success -> {
                // Refresh the item data to get updated completion status
                getActivityById(itemId, forceRefresh = true)
                result
            }
            is Result.Error -> result
        }
    }

    /**
         * Returns a reactive flow of cached itinerary items for UI updates.
         *
         * The flow emits updates whenever the cached itinerary data changes.
         * @return A Flow emitting lists of cached itinerary items.
         */
    fun getCachedItineraryFlow(): Flow<List<ItineraryItem>> =
        localRepository.getCachedItineraryFlow()

    /**
 * Removes all cached itinerary items from the local repository.
 *
 * @return A [Result] indicating success or failure of the cache clearing operation.
 */
    suspend fun clearCache(): Result<Unit> = localRepository.clearAllItineraryItems()

    /**
     * Determines whether cached itinerary data exists for the specified group code.
     *
     * @param groupCode The group code to check for cached itinerary items.
     * @return `true` if cached data exists for the group; `false` otherwise.
     */
    suspend fun hasCachedData(groupCode: String): Boolean {
        return when (val result = localRepository.getAllCachedItineraryItems()) {
            is Result.Success -> result.data.any { it.groupCode == groupCode }
            is Result.Error -> false
        }
    }
}
