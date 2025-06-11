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

import android.database.sqlite.SQLiteException
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
        return safeQueryCall {
            val tripEntity = tripDao.getTrip()
            tripEntity?.toDomain()
        }
    }

    override suspend fun getCachedTripById(tripId: String): Result<Trip?> {
        return safeQueryCall {
            val tripEntity = tripDao.getTripById(tripId)
            tripEntity?.toDomain()
        }
    }

    override suspend fun getAllCachedTrips(): Result<List<Trip>> {
        return safeQueryCall {
            val tripEntities = tripDao.getAllTrips()
            tripEntities.map { it.toDomain() }
        }
    }

    override suspend fun cacheTrip(trip: Trip): Result<Unit> {
        return safeQueryCall {
            val tripEntity = trip.toEntity()
            tripDao.insertTrip(tripEntity)
        }
    }

    override suspend fun updateTrip(trip: Trip): Result<Unit> {
        return safeQueryCall {
            val tripEntity = trip.toEntity()
            tripDao.updateTrip(tripEntity)
        }
    }

    override suspend fun deleteTripById(tripId: String): Result<Unit> {
        return safeQueryCall {
            tripDao.deleteTripById(tripId)
        }
    }

    override suspend fun clearAllTrips(): Result<Unit> {
        return safeQueryCall {
            tripDao.clearAllTrips()
        }
    }


    override fun getCachedTripFlow(): Flow<Trip?> {
        return tripDao.getTripFlow().map { tripEntity ->
            tripEntity?.toDomain()
        }
    }
}

private inline fun <T> safeQueryCall(block: () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: SQLiteException) {
    Result.Error(e)
} catch (e: IllegalStateException) {
    Result.Error(e)
}