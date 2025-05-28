package com.serranoie.itinero.core.data.remote.repository

import com.serranoie.itinero.core.data.mappers.toDomain
import com.serranoie.itinero.core.data.remote.ItineroApi
import com.serranoie.itinero.core.domain.model.TripRequest
import com.serranoie.itinero.core.domain.model.Travel
import com.serranoie.itinero.core.domain.repository.TravelRepository
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.result.safeApiCall
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class TravelRepositoryImpl(
    private val api: ItineroApi,
    private val userId: String
) : TravelRepository {
    override suspend fun getAllTravels(): Result<List<Travel>> {
        return safeApiCall {
            TODO("Not yet implemented")
        }
    }

    override suspend fun getTravelById(id: String): Result<Travel> {
        return safeApiCall {
            TODO("Not yet implemented")
        }
    }

    override suspend fun joinTravel(groupCode: String): Result<Unit> {
        return safeApiCall {
            api.joinTrip(groupCode)
        }
    }

    override suspend fun leaveTravel(): Result<Unit> {
        return safeApiCall {
            api.leaveTrip()
        }
    }

    override suspend fun createTravel(
        destination: String,
        startDate: String,
        endDate: String,
        summary: String,
        accommodation: String,
        reservationCode: String,
        extraInfo: String,
        additionalInfo: String
    ): Result<Travel> {
        return safeApiCall {
            val request = TripRequest(
                destination, startDate, endDate, summary,
                accommodation, reservationCode, extraInfo, additionalInfo
            )
            // Call the API to create the trip
            api.createTrip(request)

            // Use the mapper to create a Travel object from the request
            request.toDomain(userId)
        }
    }
}
