package com.serranoie.itinero.core.data.remote

import com.serranoie.itinero.core.data.remote.dto.AuthResponse
import com.serranoie.itinero.core.data.remote.dto.LoginRequestDto
import com.serranoie.itinero.core.data.remote.dto.RegisterRequestDto
import com.serranoie.itinero.core.domain.model.TripRequest
import io.ktor.client.HttpClient

interface ItineroApi {
    suspend fun loginUser(email: String, password: String): AuthResponse
    suspend fun registerUser(
        email: String,
        password: String,
        name: String,
        surname: String,
        phone: String
    ): AuthResponse
    suspend fun logoutUser()
    suspend fun forgotPasswordUser(email: String)

    suspend fun getAllTrips(): List<TripRequest>
    suspend fun createTrip(request: TripRequest)
    suspend fun joinTrip(groupCode: String)
    suspend fun deleteTrip()
    suspend fun leaveTrip()
}

class ItineroApiClient(private val client: HttpClient) {
    private companion object {
        const val BASE_URL = "http://10.0.2.2:8080"
    }

    suspend fun postLogin(request: LoginRequestDto): AuthResponse {
        return client.postRequest(BASE_URL, "/auth/login", request)
    }

    suspend fun postRegister(request: RegisterRequestDto): AuthResponse {
        return client.postRequest(BASE_URL, "/auth/register", request)
    }

    suspend fun postLogout() {
        client.postRequest<Unit, Unit>(BASE_URL, "/auth/logout")
    }

    suspend fun postForgotPassword(email: String) {
        val body = mapOf("email" to email)
        client.postRequest<Unit, Map<String, String>>(BASE_URL, "/auth/forgot-password", body)
    }

    suspend fun postCreateTrip(request: TripRequest) {
        client.postRequest<Unit, TripRequest>(BASE_URL, "/trips/new", request)
    }

    suspend fun postJoinTrip(groupCode: String) {
        client.postRequest<Unit, Unit>(BASE_URL, "/trips/$groupCode/join")
    }
}
