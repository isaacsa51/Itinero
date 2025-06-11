package com.serranoie.itinero.core.domain.usecase

import com.serranoie.itinero.core.domain.model.CreateTrip
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.model.UpdateTrip
import com.serranoie.itinero.core.domain.repository.TravelRepository
import com.serranoie.itinero.core.domain.result.Result

data class TravelUseCase(
    val getAllTravels: GetAllTravelsUseCase,
    val getTravelById: GetTravelByIdUseCase,
    val joinTravel: JoinTravelUseCase,
    val leaveTravel: LeaveTravelUseCase,
    val createTravel: CreateTravelUseCase,
    val updateTripInfo: UpdateTripInfoUseCase
)

class GetAllTravelsUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(): Result<List<Trip>> = repository.getAllTravels()
}

class GetTravelByIdUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(groupCode: String, forceRefresh: Boolean = false): Result<Trip> =
        repository.getTravelById(groupCode, forceRefresh)
}

class JoinTravelUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(groupCode: String): Result<Unit> = repository.joinTravel(groupCode)
}

class LeaveTravelUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.leaveTravel()
}

class CreateTravelUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(request: CreateTrip): Result<CreateTrip> =
        repository.createTravel(request)
}

class UpdateTripInfoUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(groupCode: String, request: UpdateTrip): Result<Trip> =
        repository.updateTripInfo(groupCode, request)
}
