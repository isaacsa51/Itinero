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
    private val api: ItineraryApi, private val localRepository: LocalItineraryRepository
) : ItineraryRepository {

    /**
     * Toggles the completion status of an itinerary item
     */
    override suspend fun toggleActivityCompletion(groupCode: String, itemId: String): Result<Unit> {
        return when (val result =
            safeApiCall { api.toggleItineraryItemCompletion(groupCode, itemId) }) {
            is Result.Success -> {
                // Refresh the item data to get updated completion status
                getActivityById(groupCode, itemId, forceRefresh = true)
                result
            }

            is Result.Error -> result
        }
    }

    /**
     * Gets all itinerary items with cache-first strategy
     */
    override suspend fun getAllActivities(
        groupCode: String, forceRefresh: Boolean
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
     * Gets itinerary item by ID with comprehensive caching strategy
     */
    override suspend fun getActivityById(
        groupCode: String, itemId: String, forceRefresh: Boolean
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
            api.getItineraryItem(groupCode, itemId).toDomain()
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
     * Creates a new itinerary item
     */
    override suspend fun createActivity(
        groupCode: String, request: CreateItineraryItem
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
     * Updates an existing itinerary item
     */
    override suspend fun updateActivityInfo(
        groupCode: String, itemId: String, request: UpdateItineraryItem
    ): Result<ItineraryItem> {
        return when (val result = safeApiCall {
            api.updateItineraryItem(groupCode, itemId, request.toDto()).toDomain()
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
     * Deletes an itinerary item
     */
    override suspend fun deleteActivityById(groupCode: String, itemId: String): Result<Unit> {
        return when (val result = safeApiCall { api.deleteItineraryItem(groupCode, itemId) }) {
            is Result.Success -> {
                // Remove from cache
                localRepository.deleteItineraryItemById(itemId)
                result
            }

            is Result.Error -> result
        }
    }

    /**
     * Gets cached itinerary items as a Flow for reactive UI updates
     * This is an internal method for implementation-specific functionality
     */
    internal fun getCachedItineraryFlow(): Flow<List<ItineraryItem>> =
        localRepository.getCachedItineraryFlow()

    /**
     * Clears all cached itinerary items
     */
    override suspend fun clearCache(): Result<Unit> = localRepository.clearAllItineraryItems()

    /**
     * Checks if there's cached data available for a specific group
     */
    override suspend fun hasCachedData(groupCode: String): Boolean {
        return when (val result = localRepository.getAllCachedItineraryItems()) {
            is Result.Success -> result.data.any { it.groupCode == groupCode }
            is Result.Error -> false
        }
    }
}
