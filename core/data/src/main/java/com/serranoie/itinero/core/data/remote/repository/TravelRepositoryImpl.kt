package com.serranoie.itinero.core.data.remote.repository

import com.serranoie.itinero.core.data.mappers.toDomain
import com.serranoie.itinero.core.data.remote.ItineroApi
import com.serranoie.itinero.core.data.remote.dto.AccommodationDto
import com.serranoie.itinero.core.data.remote.dto.CreateTripDto
import com.serranoie.itinero.core.domain.model.CreateTrip
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.repository.TravelRepository
import com.serranoie.itinero.core.domain.result.Result
import com.serranoie.itinero.core.domain.result.safeApiCall

class TravelRepositoryImpl(
    private val api: ItineroApi, private val userId: String
) : TravelRepository {
    override suspend fun getAllTravels(): Result<List<Trip>> {
        return safeApiCall {
            api.getAllTrips().map {
                it.toDomain()
            }
        }
    }

    override suspend fun getTravelById(id: String): Result<Trip> {
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
