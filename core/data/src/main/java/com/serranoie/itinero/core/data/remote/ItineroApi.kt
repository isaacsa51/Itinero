package com.serranoie.itinero.core.data.remote

import com.serranoie.itinero.core.data.remote.dto.AuthResponse
import com.serranoie.itinero.core.data.remote.dto.CreateTripDto
import com.serranoie.itinero.core.data.remote.dto.LoginRequestDto
import com.serranoie.itinero.core.data.remote.dto.RegisterRequestDto
import com.serranoie.itinero.core.data.remote.dto.TripDto
import com.serranoie.itinero.core.domain.model.UpdateTrip
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

    suspend fun getAllTrips(): List<TripDto>
    suspend fun getTripById(id: String): TripDto
    suspend fun createTrip(request: CreateTripDto): CreateTripDto
    suspend fun updateTripInfo(groupCode: String, request: UpdateTrip)
    suspend fun joinTrip(groupCode: String)
    /**
 * Deletes the current trip associated with the user.
 */
suspend fun deleteTrip()
    /**
 * Removes the current user from their active trip.
 */
suspend fun leaveTrip()
}
