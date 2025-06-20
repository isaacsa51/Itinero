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

    @Query("SELECT * FROM itinerary_items")
    suspend fun getAllItineraryItems(): List<ItineraryItemEntity>

    @Query("SELECT * FROM itinerary_items")
    fun getAllItineraryItemsFlow(): Flow<List<ItineraryItemEntity>>

    @Query("SELECT * FROM itinerary_items WHERE id = :itemId")
    suspend fun getItineraryItemById(itemId: Int): ItineraryItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateItineraryItem(item: ItineraryItemEntity)

    @Query("DELETE FROM itinerary_items WHERE id = :itemId")
    suspend fun deleteItineraryItem(itemId: Int)

    @Query("DELETE FROM itinerary_items")
    suspend fun clearAllItineraryItems()
}