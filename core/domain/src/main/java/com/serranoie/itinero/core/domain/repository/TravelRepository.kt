package com.serranoie.itinero.core.domain.repository

import com.serranoie.itinero.core.domain.model.CreateTrip
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.model.UpdateTrip
import com.serranoie.itinero.core.domain.result.Result

interface TravelRepository {

    suspend fun getAllTravels(): Result<List<Trip>>
    suspend fun getTravelById(id: String, forceRefresh: Boolean = false): Result<Trip>
    suspend fun joinTravel(groupCode: String): Result<Unit>
    suspend fun leaveTravel(): Result<Unit>
    suspend fun createTravel(request: CreateTrip): Result<CreateTrip>
    suspend fun updateTripInfo(tripId: String, request: UpdateTrip): Result<Trip>
}
