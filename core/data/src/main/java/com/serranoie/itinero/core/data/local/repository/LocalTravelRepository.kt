/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: LocalTravelRepository.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 09 June 2025
 */

package com.serranoie.itinero.core.data.local.repository

import com.serranoie.itinero.core.data.local.dao.TripDao
import com.serranoie.itinero.core.data.mappers.toDomain
import com.serranoie.itinero.core.data.mappers.toEntity
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LocalTravelRepository {
    suspend fun getCachedTrip(): Result<Trip?>
    suspend fun getCachedTripById(tripId: String): Result<Trip?>
    suspend fun getAllCachedTrips(): Result<List<Trip>>
    suspend fun cacheTrip(trip: Trip): Result<Unit>
    suspend fun updateTrip(trip: Trip): Result<Unit>
    suspend fun deleteTripById(tripId: String): Result<Unit>
    suspend fun clearAllTrips(): Result<Unit>
    fun getCachedTripFlow(): Flow<Trip?>
}

class LocalTravelRepositoryImpl(
    private val tripDao: TripDao
) : LocalTravelRepository {

    override suspend fun getCachedTrip(): Result<Trip?> {
        return try {
            val tripEntity = tripDao.getTrip()
            val trip = tripEntity?.toDomain()
            Result.Success(trip)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getCachedTripById(tripId: String): Result<Trip?> {
        return try {
            val tripEntity = tripDao.getTripById(tripId)
            val trip = tripEntity?.toDomain()
            Result.Success(trip)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getAllCachedTrips(): Result<List<Trip>> {
        return try {
            val tripEntities = tripDao.getAllTrips()
            val trips = tripEntities.map { it.toDomain() }
            Result.Success(trips)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun cacheTrip(trip: Trip): Result<Unit> {
        return try {
            val tripEntity = trip.toEntity()
            tripDao.insertTrip(tripEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateTrip(trip: Trip): Result<Unit> {
        return try {
            val tripEntity = trip.toEntity()
            tripDao.updateTrip(tripEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteTripById(tripId: String): Result<Unit> {
        return try {
            tripDao.deleteTripById(tripId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun clearAllTrips(): Result<Unit> {
        return try {
            tripDao.clearAllTrips()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getCachedTripFlow(): Flow<Trip?> {
        return tripDao.getTripFlow().map { tripEntity ->
            tripEntity?.toDomain()
        }
    }
}
