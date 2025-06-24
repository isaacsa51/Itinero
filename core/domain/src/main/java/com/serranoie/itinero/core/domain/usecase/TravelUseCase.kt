package com.serranoie.itinero.core.domain.usecase

import com.serranoie.itinero.core.domain.model.CreateTrip
import com.serranoie.itinero.core.domain.model.Trip
import com.serranoie.itinero.core.domain.model.TripMember
import com.serranoie.itinero.core.domain.model.UpdateTrip
import com.serranoie.itinero.core.domain.repository.TravelRepository
import com.serranoie.itinero.core.domain.result.Result

data class TravelUseCase(
    val getAllTravels: GetAllTravelsUseCase,
    val getTravelById: GetTravelByIdUseCase,
    val joinTravel: JoinTravelUseCase,
    val leaveTravel: LeaveTravelUseCase,
    val createTravel: CreateTravelUseCase,
    val updateTripInfo: UpdateTripInfoUseCase,
    val acceptMemberToTrip: AcceptMemberToTripUseCase,
    val getAllMembers: GetAllMembersUseCase,
    val rejectMember: RejectMemberUseCase,
    val removeMember: RemoveMemberUseCase,
    val makeOwner: MakeOwnerUseCase,
    val getCurrentUserMembershipStatus: GetCurrentUserMembershipStatusUseCase,
    val leaveTrip: LeaveTripUseCase,
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
    suspend operator fun invoke(groupCode: String): Result<Unit> = repository.leaveTrip(groupCode)
}

class CreateTravelUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(request: CreateTrip): Result<CreateTrip> =
        repository.createTravel(request)
}

class UpdateTripInfoUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(groupCode: String, request: UpdateTrip): Result<Trip> =
        repository.updateTripInfo(groupCode, request)
}

class AcceptMemberToTripUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(groupCode: String, idMember: Int): Result<Unit> =
        repository.acceptMember(groupCode, idMember)
}

// Additional member management use cases
class GetAllMembersUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(groupCode: String): Result<List<TripMember>> =
        repository.getAllMembers(groupCode)
}

class RejectMemberUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(groupCode: String, idMember: Int): Result<Unit> =
        repository.rejectMember(groupCode, idMember)
}

class RemoveMemberUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(groupCode: String, idMember: Int): Result<Unit> =
        repository.removeMember(groupCode, idMember)
}

class MakeOwnerUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(groupCode: String, idMember: Int): Result<Unit> =
        repository.makeOwner(groupCode, idMember)
}

class GetCurrentUserMembershipStatusUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(groupCode: String, userId: Int): Result<TripMember> =
        repository.getCurrentUserMembershipStatus(groupCode, userId)
}

class LeaveTripUseCase(private val repository: TravelRepository) {
    suspend operator fun invoke(groupCode: String): Result<Unit> =
        repository.leaveTrip(groupCode)
}
