package com.serranoie.itinero.core.domain.usecase

import com.serranoie.itinero.core.domain.model.Travel
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
    suspend operator fun invoke(): Result<List<Travel>> = repository.getAllTravels()
}

class GetTravelByIdUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(id: String): Result<Travel> = repository.getTravelById(id)
}

class JoinTravelUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(groupCode: String): Result<Unit> = repository.joinTravel(groupCode)
}

class LeaveTravelUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.leaveTravel()
}

class CreateTravelUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(
        destination: String,
        startDate: String,
        endDate: String,
        summary: String,
        accommodation: String,
        reservationCode: String,
        extraInfo: String,
        additionalInfo: String
    ): Result<Travel> = repository.createTravel(
        destination,
        startDate,
        endDate,
        summary,
        accommodation,
        reservationCode,
        extraInfo,
        additionalInfo
    )
}
