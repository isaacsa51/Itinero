package com.serranoie.itinero.core.domain.repository

import com.serranoie.itinero.core.domain.model.CreateTrip
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.result.Result

interface TravelRepository {

    suspend fun getAllTravels(): Result<List<Trip>>
    suspend fun getTravelById(id: String): Result<Trip>
    suspend fun joinTravel(groupCode: String): Result<Unit>
    suspend fun leaveTravel(): Result<Unit>
    suspend fun createTravel(
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
    ): Result<CreateTrip>

}
