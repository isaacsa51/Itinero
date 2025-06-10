package com.serranoie.itinero.core.domain.usecase

import com.serranoie.itinero.core.domain.model.CreateTrip
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.repository.TravelRepository
import com.serranoie.itinero.core.domain.result.Result

data class TravelUseCase(
    val getAllTravels: GetAllTravelsUseCase,
    val getTravelById: GetTravelByIdUseCase,
    val joinTravel: JoinTravelUseCase,
    val leaveTravel: LeaveTravelUseCase,
    val createTravel: CreateTravelUseCase
)

class GetAllTravelsUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(): Result<List<Trip>> = repository.getAllTravels()
}

class GetTravelByIdUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(id: String, forceRefresh: Boolean = false): Result<Trip> =
        repository.getTravelById(id, forceRefresh)
}

class JoinTravelUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(groupCode: String): Result<Unit> = repository.joinTravel(groupCode)
}

class LeaveTravelUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.leaveTravel()
}

class CreateTravelUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(
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
    ): Result<CreateTrip> = repository.createTravel(
        groupName,
        destination,
        startDate,
        endDate,
        summary,
        accommodationName,
        accommodationPhone,
        accommodationCheckIn,
        accommodationCheckOut,
        accommodationLocation,
        accommodationMapUri,
        reservationCode,
        extraInfo,
        additionalInfo
    )
}
