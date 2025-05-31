package com.serranoie.itinero.core.data.remote.repository

import android.util.Log
import com.serranoie.itinero.core.data.mappers.toDomain
import com.serranoie.itinero.core.data.remote.ItineroApi
import com.serranoie.itinero.core.data.remote.dto.TripDto
import com.serranoie.itinero.core.domain.model.Travel
import com.serranoie.itinero.core.domain.repository.TravelRepository
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.result.safeApiCall

class TravelRepositoryImpl(
    private val api: ItineroApi, private val userId: String
) : TravelRepository {
    override suspend fun getAllTravels(): Result<List<Travel>> {
        return safeApiCall {
            api.getAllTrips().map {
                it.toDomain()
            }
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
            val request = TripDto(
                destination = destination,
                startDate = startDate,
                endDate = endDate,
                summary = summary,
                accommodation = accommodation,
                reservationCode = reservationCode,
                extraInfo = extraInfo,
                additionalInfo = additionalInfo
            )

            Log.d("ISAAC", "Create travel Request: $request")

            // Call the API to create the trip
            api.createTrip(request)

            // Use the mapper to create a Travel object from the request
            request.toDomain()
        }
    }
}
