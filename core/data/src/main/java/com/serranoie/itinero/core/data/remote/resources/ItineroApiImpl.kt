/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineroApiImpl.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 22 junio 2025
 */

package com.serranoie.itinero.core.data.remote.resources

import com.serranoie.itinero.core.data.remote.dto.AuthResponse
import com.serranoie.itinero.core.data.remote.dto.CreateTripDto
import com.serranoie.itinero.core.data.remote.dto.LoginRequestDto
import com.serranoie.itinero.core.data.remote.dto.RegisterRequestDto
import com.serranoie.itinero.core.data.remote.dto.TripDto
import com.serranoie.itinero.core.data.remote.dto.TripMemberDto
import com.serranoie.itinero.core.domain.model.UpdateTrip
import io.ktor.client.HttpClient

class ItineroApiImpl(
    client: HttpClient
) : BaseApiClient(client), ItineroApi {

    override suspend fun loginUser(email: String, password: String): AuthResponse {
        return post("/auth/login", LoginRequestDto(email, password))
    }

    override suspend fun registerUser(
        email: String, password: String, name: String, surname: String, phone: String
    ): AuthResponse {
        return post("/auth/register", RegisterRequestDto(name, surname, email, password, phone))
    }

    override suspend fun logoutUser() {
        post<Unit, Unit>("/auth/logout")
    }

    override suspend fun forgotPasswordUser(email: String) {
        val body = mapOf("email" to email)
        post<Unit, Map<String, String>>("/auth/forgot-password", body)
    }

    override suspend fun getTripById(id: String): TripDto {
        return get("/trips/$id")
    }

    override suspend fun getAllTrips(): List<TripDto> {
        return get("/trips")
    }

    override suspend fun createTrip(request: CreateTripDto): CreateTripDto {
        return post("/trips/new", request)
    }

    override suspend fun updateTripInfo(groupCode: String, request: UpdateTrip) {
        put<Unit, UpdateTrip>("/trips/$groupCode/info", request)
    }

    override suspend fun joinTrip(groupCode: String) {
        post<Unit, Unit>("/trips/$groupCode/join")
    }

    override suspend fun deleteTrip() {
        // TODO: Implement when the API endpoint is ready
    }

    override suspend fun leaveTrip() {
        // TODO: Implement when the API endpoint is ready
    }

    // Member management endpoints implementation
    override suspend fun getAllMembers(groupCode: String): List<TripMemberDto> {
        return get("/trips/$groupCode/members")
    }

    override suspend fun acceptMember(groupCode: String, idMember: Int) {
        post<Unit, Unit>("/trips/$groupCode/members/$idMember/accept")
    }

    override suspend fun rejectMember(groupCode: String, idMember: Int) {
        post<Unit, Unit>("/trips/$groupCode/members/$idMember/reject")
    }

    override suspend fun removeMember(groupCode: String, idMember: Int) {
        delete<Unit>("/trips/$groupCode/members/$idMember")
    }

    override suspend fun makeOwner(groupCode: String, idMember: Int) {
        post<Unit, Unit>("/trips/$groupCode/members/$idMember/make-owner")
    }

    override suspend fun getCurrentUserMembershipStatus(groupCode: String): TripMemberDto {
        return get("/trips/$groupCode/members/me")
    }

    override suspend fun leaveSpecificTrip(groupCode: String) {
        delete<Unit>("/trips/$groupCode/members/me")
    }
}
