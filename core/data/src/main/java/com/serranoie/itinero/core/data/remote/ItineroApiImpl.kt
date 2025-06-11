package com.serranoie.itinero.core.data.remote

import com.serranoie.itinero.core.data.remote.dto.AuthResponse
import com.serranoie.itinero.core.data.remote.dto.CreateTripDto
import com.serranoie.itinero.core.data.remote.dto.LoginRequestDto
import com.serranoie.itinero.core.data.remote.dto.RegisterRequestDto
import com.serranoie.itinero.core.data.remote.dto.TripDto
import com.serranoie.itinero.core.domain.model.UpdateTrip
import io.ktor.client.HttpClient

class ItineroApiImpl(
    client: HttpClient
) : ItineroApi {

    private val apiClient = ItineroApiClient(client)

    override suspend fun loginUser(email: String, password: String): AuthResponse {
        return apiClient.postLogin(LoginRequestDto(email, password))
    }

    override suspend fun registerUser(
        email: String, password: String, name: String, surname: String, phone: String
    ): AuthResponse {
        return apiClient.postRegister(
            RegisterRequestDto(name, surname, email, password, phone)
        )
    }

    override suspend fun logoutUser() {
        apiClient.postLogout()
    }

    override suspend fun forgotPasswordUser(email: String) {
        apiClient.postForgotPassword(email)
    }

    override suspend fun getTripById(id: String): TripDto {
        return apiClient.getTripById(id)
    }

    override suspend fun getAllTrips(): List<TripDto> {
        return apiClient.getTrips()
    }

    override suspend fun createTrip(request: CreateTripDto): CreateTripDto {
        return apiClient.createTrip(request)
    }

    override suspend fun updateTripInfo(groupCode: String, request: UpdateTrip) {
        apiClient.updateTripInfo(groupCode, request)
    }

    override suspend fun joinTrip(groupCode: String) {
        apiClient.postJoinTrip(groupCode)
    }

    override suspend fun deleteTrip() {
        // TODO: Implement when the API endpoint is ready
    }

    override suspend fun leaveTrip() {
        // TODO: Implement when the API endpoint is ready
    }
}
