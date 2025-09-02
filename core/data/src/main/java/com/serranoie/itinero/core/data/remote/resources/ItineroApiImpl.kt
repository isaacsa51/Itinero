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

import com.serranoie.itinero.core.data.mappers.toDto
import com.serranoie.itinero.core.data.remote.dto.auth.AuthResponse
import com.serranoie.itinero.core.data.remote.dto.auth.DeleteAccountRequestDto
import com.serranoie.itinero.core.data.remote.dto.auth.LoginRequestDto
import com.serranoie.itinero.core.data.remote.dto.auth.RegisterRequestDto
import com.serranoie.itinero.core.data.remote.dto.auth.UpdateLanguageRequest
import com.serranoie.itinero.core.data.remote.dto.auth.UpdateLanguageResponse
import com.serranoie.itinero.core.data.remote.dto.auth.UserPreferencesResponse
import com.serranoie.itinero.core.data.remote.dto.trip.CreateTripDto
import com.serranoie.itinero.core.data.remote.dto.trip.MembershipStatusDto
import com.serranoie.itinero.core.data.remote.dto.trip.TripDto
import com.serranoie.itinero.core.data.remote.dto.trip.TripMemberDto
import com.serranoie.itinero.core.data.remote.dto.trip.TripOverviewDto
import com.serranoie.itinero.core.data.remote.dto.trip.UpdateTripDto
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

    override suspend fun getUserPreferences(): UserPreferencesResponse {
        return get("/user/preferences")
    }

    override suspend fun updateLanguagePreference(request: UpdateLanguageRequest): UpdateLanguageResponse {
        return patch("/user/preferences/language", request)
    }

    override suspend fun getTripById(id: String): TripDto {
        return get("/trips/$id")
    }

    override suspend fun getAllTrips(): List<TripDto> {
        return get("/trips")
    }

    override suspend fun getTripOverview(groupCode: String): TripOverviewDto {
        return get("/trips/$groupCode/today-overview")
    }

    override suspend fun createTrip(request: CreateTripDto): CreateTripDto {
        return post("/trips/new", request)
    }

    override suspend fun updateTripInfo(groupCode: String, request: UpdateTrip) {
        put<Unit, UpdateTripDto>("/trips/$groupCode/info", request.toDto())
    }

    override suspend fun joinTrip(groupCode: String) {
        post<Unit, Unit>("/trips/$groupCode/join")
    }

    override suspend fun deleteTrip(groupCode: String) {
        delete<Unit>("/trips/$groupCode")
    }

    override suspend fun leaveTrip(groupCode: String) {
        delete<Unit>("/trips/$groupCode/leave")
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

    override suspend fun getCurrentUserMembershipStatus(groupCode: String): MembershipStatusDto {
        return get("/trips/$groupCode/member/status")
    }

    override suspend fun deleteAccount(password: String) {
        deleteWithBody<Unit, DeleteAccountRequestDto>(
            "/auth/delete-account",
            DeleteAccountRequestDto(password)
        )
    }
}
