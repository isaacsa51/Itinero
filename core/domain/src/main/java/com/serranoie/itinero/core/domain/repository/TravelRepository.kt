package com.serranoie.itinero.core.domain.repository

import com.serranoie.itinero.core.domain.model.Travel
import com.serranoie.itinero.core.domain.result.Result

interface TravelRepository {

    suspend fun getAllTravels(): Result<List<Travel>>
    suspend fun getTravelById(id: String): Result<Travel>
    suspend fun joinTravel(groupCode: String): Result<Unit>
    suspend fun leaveTravel(): Result<Unit>
    suspend fun createTravel(
        destination: String,
        startDate: String,
        endDate: String,
        summary: String,
        accommodation: String,
        reservationCode: String,
        extraInfo: String,
        additionalInfo: String
    ): Result<Travel>

}
