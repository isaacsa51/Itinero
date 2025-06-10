package com.serranoie.itinero.core.data.remote.repository

import android.util.Log
import com.serranoie.itinero.core.data.local.repository.LocalTravelRepository
import com.serranoie.itinero.core.data.mappers.toDomain
import com.serranoie.itinero.core.data.remote.ItineroApi
import com.serranoie.itinero.core.data.remote.dto.AccommodationDto
import com.serranoie.itinero.core.data.remote.dto.CreateTripDto
import com.serranoie.itinero.core.domain.model.CreateTrip
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.repository.TravelRepository
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.result.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TravelRepositoryImpl(
    private val api: ItineroApi,
    private val localRepository: LocalTravelRepository,
) : TravelRepository {

    /**
     * Gets all travels with cache-first strategy
     * 1. Try to get from cache first
     * 2. If cache miss or force refresh, fetch from remote
     * 3. Update cache with fresh data
     */
    override suspend fun getAllTravels(): Result<List<Trip>> {
        return safeApiCall {
            api.getAllTrips().map {
                it.toDomain()
            }
        }
    }

    /**
     * Gets travel by ID with comprehensive caching strategy
     * 1. Try to get from local cache first
     * 2. If cache miss, fetch from remote and cache the result
     * 3. If force refresh requested, always fetch from remote
     */
    override suspend fun getTravelById(groupCode: String, forceRefresh: Boolean): Result<Trip> {
        // If not forcing refresh, try cache first
        if (!forceRefresh) {
            when (val cacheResult = localRepository.getCachedTrip()) {
                is Result.Success -> {
                    cacheResult.data?.let { cachedTrip ->
                        // Check if cached trip matches the requested ID
                        if (cachedTrip.groupCode == groupCode || cachedTrip.id == groupCode) {
                            return Result.Success(cachedTrip)
                        }
                    }
                }

                is Result.Error -> {
                    // Cache error, continue to remote fetch
                    Log.e("ITINERO - TravelRepository", "Cache error: ${cacheResult.exception.message}")
                }
            }
        }

        // Fetch from remote
        return when (val remoteResult = safeApiCall { api.getTripById(groupCode).toDomain() }) {
            is Result.Success -> {
                // Cache the fresh data
                localRepository.cacheTrip(remoteResult.data)
                remoteResult
            }

            is Result.Error -> {
                // If remote fails and we have cached data, return cached data
                if (!forceRefresh) {
                    when (val cacheResult = localRepository.getCachedTrip()) {
                        is Result.Success -> {
                            cacheResult.data?.let { cachedTrip ->
                                if (cachedTrip.groupCode == groupCode || cachedTrip.id == groupCode) {
                                    return Result.Success(cachedTrip)
                                }
                            }
                        }

                        is Result.Error -> {
                            // Both remote and cache failed
                            Log.e("ITINERO - TravelRepository", "Remote and cache failed: ${remoteResult.exception.message}")
                        }
                    }
                }
                remoteResult
            }
        }
    }

    /**
     * Gets cached trip as a Flow for reactive UI updates
     */
    fun getCachedTripFlow(): Flow<Trip?> = localRepository.getCachedTripFlow()

    /**
     * Clears the cached trip data
     */
    suspend fun clearCache(): Result<Unit> = localRepository.clearAllTrips()

    /**
     * Checks if there's cached data available
     */
    suspend fun hasCachedData(): Boolean {
        return when (val result = localRepository.getCachedTrip()) {
            is Result.Success -> result.data != null
            is Result.Error -> false
        }
    }

    override suspend fun joinTravel(groupCode: String): Result<Unit> {
        return when (val result = safeApiCall { api.joinTrip(groupCode) }) {
            is Result.Success -> {
                // After joining, fetch and cache the trip data
                getTravelById(groupCode, forceRefresh = true)
                result
            }

            is Result.Error -> result
        }
    }

    override suspend fun leaveTravel(): Result<Unit> {
        return when (val result = safeApiCall { api.leaveTrip() }) {
            is Result.Success -> {
                // Clear cache when leaving travel
                localRepository.clearAllTrips()
                result
            }

            is Result.Error -> result
        }
    }

    override suspend fun createTravel(
        groupName: String,
        destination: String,
        startDate: String,
        endDate: String,
        summary: String,
        accommodationName: String,
        accommodationPhone: String,
        accommodationCheckIn: String,
        accommodationCheckOut: String,
        accommodationLocation: String,
        accommodationMapUri: String,
        reservationCode: String,
        extraInfo: String,
        additionalInfo: String
    ): Result<CreateTrip> {
        return safeApiCall {
            val accommodationDto = AccommodationDto(
                name = accommodationName,
                phone = accommodationPhone,
                checkIn = accommodationCheckIn,
                checkOut = accommodationCheckOut,
                location = accommodationLocation,
                mapUri = accommodationMapUri
            )

            val request = CreateTripDto(
                groupName = groupName,
                destination = destination,
                startDate = startDate,
                endDate = endDate,
                summary = summary,
                accommodation = accommodationDto,
                reservationCode = reservationCode,
                extraInfo = extraInfo,
                additionalInfo = additionalInfo
            )
            val createdTrip = api.createTrip(request)
            createdTrip.toDomain()
        }
    }
}
