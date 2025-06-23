package com.serranoie.itinero.core.domain.repository

import com.serranoie.itinero.core.domain.model.CreateTrip
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.model.TripMember
import com.serranoie.itinero.core.domain.model.UpdateTrip
import com.serranoie.itinero.core.domain.result.Result

interface TravelRepository {

    suspend fun getAllTravels(): Result<List<Trip>>
    suspend fun getTravelById(groupCode: String, forceRefresh: Boolean = false): Result<Trip>
    suspend fun joinTravel(groupCode: String): Result<Unit>
    suspend fun leaveTravel(): Result<Unit>
    suspend fun createTravel(request: CreateTrip): Result<CreateTrip>
    suspend fun updateTripInfo(groupCode: String, request: UpdateTrip): Result<Trip>
    
    // Member management methods
    suspend fun getAllMembers(groupCode: String): Result<List<TripMember>>
    suspend fun acceptMember(groupCode: String, idMember: Int): Result<Unit>
    suspend fun rejectMember(groupCode: String, idMember: Int): Result<Unit>
    suspend fun removeMember(groupCode: String, idMember: Int): Result<Unit>
    suspend fun makeOwner(groupCode: String, idMember: Int): Result<Unit>
    suspend fun getCurrentUserMembershipStatus(groupCode: String, userId: Int): Result<TripMember>
    suspend fun leaveTrip(groupCode: String): Result<Unit>
}
