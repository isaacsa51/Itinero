/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: TripDao.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 09 June 2025
 */

package com.serranoie.itinero.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.serranoie.itinero.core.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Query("SELECT * FROM trips LIMIT 1")
    suspend fun getTrip(): TripEntity?

    @Query("SELECT * FROM trips WHERE id = :groupCode OR groupCode = :groupCode LIMIT 1")
    suspend fun getTripById(groupCode: String): TripEntity?

    @Query("SELECT * FROM trips")
    suspend fun getAllTrips(): List<TripEntity>

    @Query("SELECT * FROM trips LIMIT 1")
    fun getTripFlow(): Flow<TripEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("DELETE FROM trips")
    suspend fun clearAllTrips()

    @Query("DELETE FROM trips WHERE id = :groupCode")
    suspend fun deleteTripById(groupCode: String)
}
