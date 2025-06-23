/*
 - Copyright (c) 2025 Isaac Serrano.
 -
 - File: ItineroApi.kt
 - Project: Itinero
 - Module: Itinero.core.data.main
 -
 - This file belongs to the project: Itinero.
 - Last edited: 22 junio 2025
 */

package com.serranoie.itinero.core.data.remote.resources

import com.serranoie.itinero.core.data.remote.dto.AuthResponse
import com.serranoie.itinero.core.data.remote.dto.CreateTripDto
import com.serranoie.itinero.core.data.remote.dto.TripDto
import com.serranoie.itinero.core.data.remote.dto.TripMemberDto
import com.serranoie.itinero.core.domain.model.UpdateTrip

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
    suspend fun deleteTrip()
    suspend fun leaveTrip()

    // Member management endpoints
    suspend fun getAllMembers(groupCode: String): List<TripMemberDto>
    suspend fun acceptMember(groupCode: String, idMember: Int)
    suspend fun rejectMember(groupCode: String, idMember: Int)
    suspend fun removeMember(groupCode: String, idMember: Int)
    suspend fun makeOwner(groupCode: String, idMember: Int)
    suspend fun getCurrentUserMembershipStatus(groupCode: String): List<TripMemberDto>
    suspend fun leaveSpecificTrip(groupCode: String)
}
