/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineraryDao.kt
 - Project: Itinero
 - Module: Itinero.feature.itinerary.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 18 junio 2025
 */

package com.serranoie.app.feature.itinerary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.serranoie.app.feature.itinerary.data.local.entity.ItineraryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryDao {

    /**
     * Retrieves all itinerary items from the database.
     *
     * @return A list of all itinerary item entities.
     */
    @Query("SELECT * FROM itinerary_items")
    suspend fun getAllItineraryItems(): List<ItineraryItemEntity>

    /**
     * Returns a reactive stream of all itinerary items in the database.
     *
     * The returned [Flow] emits an updated list of [ItineraryItemEntity] objects whenever the underlying data changes.
     * @return A [Flow] emitting lists of all itinerary items.
     */
    @Query("SELECT * FROM itinerary_items")
    fun getAllItineraryItemsFlow(): Flow<List<ItineraryItemEntity>>

    /**
     * Retrieves a single itinerary item by its unique ID.
     *
     * @param itemId The unique identifier of the itinerary item to retrieve.
     * @return The matching ItineraryItemEntity if found, or null if no item exists with the given ID.
     */
    @Query("SELECT * FROM itinerary_items WHERE id = :itemId")
    suspend fun getItineraryItemById(itemId: Int): ItineraryItemEntity?

    /**
     * Inserts a new itinerary item or updates an existing one if a conflict occurs on the primary key.
     *
     * If an item with the same primary key exists, it will be replaced with the new data.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateItineraryItem(item: ItineraryItemEntity)

    /**
     * Deletes the itinerary item with the specified ID from the database.
     *
     * @param itemId The unique identifier of the itinerary item to delete.
     */
    @Query("DELETE FROM itinerary_items WHERE id = :itemId")
    suspend fun deleteItineraryItem(itemId: Int)

    /**
     * Deletes all itinerary items from the database.
     */
    @Query("DELETE FROM itinerary_items")
    suspend fun clearAllItineraryItems()
}
