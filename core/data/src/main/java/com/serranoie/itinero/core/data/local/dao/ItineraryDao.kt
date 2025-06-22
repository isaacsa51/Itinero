/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineraryDao.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 18 junio 2025
 */

package com.serranoie.itinero.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.serranoie.itinero.core.data.local.entity.ItineraryItemEntity
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
     * Returns a Flow that emits the list of all itinerary items in the database.
     *
     * The Flow emits a new list whenever the data in the `itinerary_items` table changes.
     * 
     * @return A Flow emitting lists of all itinerary items.
     */
    @Query("SELECT * FROM itinerary_items")
    fun getAllItineraryItemsFlow(): Flow<List<ItineraryItemEntity>>

    /**
     * Retrieves an itinerary item by its unique ID.
     *
     * @param itemId The ID of the itinerary item to retrieve.
     * @return The matching itinerary item, or null if not found.
     */
    @Query("SELECT * FROM itinerary_items WHERE id = :itemId")
    suspend fun getItineraryItemById(itemId: Int): ItineraryItemEntity?

    /**
     * Inserts a new itinerary item or updates an existing one if a conflict occurs.
     *
     * If an item with the same primary key exists, it will be replaced.
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