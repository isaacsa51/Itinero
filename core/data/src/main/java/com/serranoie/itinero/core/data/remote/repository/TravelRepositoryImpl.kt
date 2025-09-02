package com.serranoie.itinero.core.data.remote.repository

import android.util.Log
import com.serranoie.itinero.core.data.local.repository.LocalTravelRepository
import com.serranoie.itinero.core.data.mappers.toDomain
import com.serranoie.itinero.core.data.mappers.toCreateDto
import com.serranoie.itinero.core.data.remote.dto.trip.CreateTripDto
import com.serranoie.itinero.core.data.remote.resources.ItineroApi
import com.serranoie.itinero.core.domain.model.CreateTrip
import com.serranoie.itinero.core.domain.model.MembershipStatus
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.model.TripMember
import com.serranoie.itinero.core.domain.model.TripOverview
import com.serranoie.itinero.core.domain.model.UpdateTrip
import com.serranoie.itinero.core.domain.repository.TravelRepository
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.result.safeApiCall
import kotlinx.coroutines.flow.Flow

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
            getCachedTripIfMatches(groupCode)?.let { cachedTrip ->
                return Result.Success(cachedTrip)
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
                if (!forceRefresh) {
                    getCachedTripIfMatches(groupCode)?.let { cachedTrip ->
                        return Result.Success(cachedTrip)
                    }
                    Log.e(
                        "ITINERO - TravelRepository",
                        "Remote and cache failed: ${remoteResult.exception.message}"
                    )
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
                val fetchResult = getTravelById(groupCode, true)
                if (fetchResult is Result.Error) {
                    Log.e(
                        "ITINERO - TravelRepository",
                        "Failed to fetch joined trip: ${fetchResult.exception.message}"
                    )
                }
                result
            }

            is Result.Error -> result
        }
    }

    override suspend fun createTravel(request: CreateTrip): Result<CreateTrip> {
        return safeApiCall {
            val createTripDto = CreateTripDto(
                ownerId = request.ownerId,
                destination = request.destination,
                startDate = request.startDate,
                endDate = request.endDate,
                summary = request.summary,
                accommodation = request.accommodation.toCreateDto(),
                groupName = request.groupName,
                reservationCode = request.reservationCode,
                extraInfo = request.extraInfo
            )
            val createdTrip = api.createTrip(createTripDto)
            createdTrip.toDomain()
        }
    }

    override suspend fun updateTripInfo(groupCode: String, request: UpdateTrip): Result<Trip> {
        val apiCallResult = safeApiCall {
            api.updateTripInfo(groupCode, request)
            val updatedTrip = api.getTripById(groupCode).toDomain()
            localRepository.cacheTrip(updatedTrip)
            updatedTrip
        }

        return when (apiCallResult) {
            is Result.Success -> {
                apiCallResult
            }
            is Result.Error -> {
                Log.e(
                    "ITINERO - TravelRepository",
                    "Failed to update trip info: ${apiCallResult.exception.message}"
                )
                try {
                    localRepository.clearAllTrips()
                } catch (e: Exception) {
                    Log.e("ITINERO - TravelRepository", "Failed to clear cache: ${e.message}")
                }
                apiCallResult
            }
        }
    }

    override suspend fun acceptMember(groupCode: String, idMember: Int): Result<Unit> {
        return when (val result = safeApiCall { api.acceptMember(groupCode, idMember) }) {
            is Result.Success -> {
                try {
                    val updatedTrip = api.getTripById(groupCode).toDomain()
                    localRepository.cacheTrip(updatedTrip)
                } catch (e: Exception) {
                    Log.e(
                        "ITINERO - TravelRepository",
                        "Failed to refresh trip after accepting member: ${e.message}"
                    )
                    localRepository.clearAllTrips()
                    throw e
                }
                result
            }

            is Result.Error -> {
                Log.e(
                    "ITINERO - TravelRepository",
                    "Failed to accept member with group code $groupCode and member ID $idMember: ${result.exception.message}"
                )
                result
            }
        }
    }

    override suspend fun getAllMembers(groupCode: String): Result<List<TripMember>> {
        return safeApiCall { api.getAllMembers(groupCode).map { it.toDomain() } }
    }

    override suspend fun rejectMember(groupCode: String, idMember: Int): Result<Unit> {
        return when (val result = safeApiCall { api.rejectMember(groupCode, idMember) }) {
            is Result.Success -> {
                try {
                    val updatedTrip = api.getTripById(groupCode).toDomain()
                    localRepository.cacheTrip(updatedTrip)
                } catch (e: Exception) {
                    Log.e(
                        "ITINERO - TravelRepository",
                        "Failed to refresh trip after rejecting member: ${e.message}"
                    )
                    localRepository.clearAllTrips()
                }
                result
            }

            is Result.Error -> {
                Log.e(
                    "ITINERO - TravelRepository",
                    "Failed to reject member with group code $groupCode and member ID $idMember: ${result.exception.message}"
                )
                result
            }
        }
    }

    override suspend fun removeMember(groupCode: String, idMember: Int): Result<Unit> {
        return when (val result = safeApiCall { api.removeMember(groupCode, idMember) }) {
            is Result.Success -> {
                try {
                    val updatedTrip = api.getTripById(groupCode).toDomain()
                    localRepository.cacheTrip(updatedTrip)
                } catch (e: Exception) {
                    Log.e(
                        "ITINERO - TravelRepository",
                        "Failed to refresh trip after removing member: ${e.message}"
                    )
                    localRepository.clearAllTrips()
                }
                result
            }

            is Result.Error -> {
                Log.e(
                    "ITINERO - TravelRepository",
                    "Failed to remove member with group code $groupCode and member ID $idMember: ${result.exception.message}"
                )
                result
            }
        }
    }

    override suspend fun makeOwner(groupCode: String, idMember: Int): Result<Unit> {
        return when (val result = safeApiCall { api.makeOwner(groupCode, idMember) }) {
            is Result.Success -> {
                try {
                    val updatedTrip = api.getTripById(groupCode).toDomain()
                    localRepository.cacheTrip(updatedTrip)
                } catch (e: Exception) {
                    Log.e(
                        "ITINERO - TravelRepository",
                        "Failed to refresh trip after making member owner: ${e.message}"
                    )
                    localRepository.clearAllTrips()
                }
                result
            }

            is Result.Error -> {
                Log.e(
                    "ITINERO - TravelRepository",
                    "Failed to make member owner with group code $groupCode and member ID $idMember: ${result.exception.message}"
                )
                result
            }
        }
    }

    override suspend fun getCurrentUserMembershipStatus(
        groupCode: String
    ): Result<MembershipStatus> {
        return safeApiCall {
            api.getCurrentUserMembershipStatus(groupCode).toDomain()
        }
    }

    override suspend fun leaveTrip(groupCode: String): Result<Unit> {
        return when (val result = safeApiCall { api.leaveTrip(groupCode) }) {
            is Result.Success -> {
                localRepository.clearAllTrips()
                result
            }

            is Result.Error -> {
                Log.e(
                    "ITINERO - TravelRepository",
                    "Failed to leave trip with group code $groupCode: ${result.exception.message}"
                )
                result
            }
        }
    }

    override suspend fun getTripOverview(groupCode: String): Result<TripOverview> {
        return when (val result = safeApiCall { api.getTripOverview(groupCode).toDomain() }) {
            is Result.Success -> {
                result
            }

            is Result.Error -> {
                Log.e(
                    "ITINERO - TravelRepository",
                    "Failed to get trip overview for group code $groupCode: ${result.exception.message}"
                )
                result
            }
        }
    }

    override suspend fun deleteTrip(groupCode: String): Result<Unit> {
        return when (val result = safeApiCall { api.deleteTrip(groupCode) }) {
            is Result.Success -> {
                localRepository.clearAllTrips()
                result
            }
            is Result.Error -> {
                Log.e(
                    "ITINERO - TravelRepository",
                    "Failed to delete trip with group code $groupCode: ${result.exception.message}"
                )
                result
            }
        }
    }

    private suspend fun getCachedTripIfMatches(groupCode: String): Trip? {
        return when (val result = localRepository.getCachedTrip()) {
            is Result.Success -> {
                result.data?.takeIf { it.groupCode == groupCode }
            }

            is Result.Error -> {
                Log.e("ITINERO - TravelRepository", "Cache error: ${result.exception.message}")
                null
            }
        }
    }

}
