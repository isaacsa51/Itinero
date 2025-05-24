package com.serranoie.itinero.core.data.mappers

import com.serranoie.itinero.core.data.remote.dto.AuthResponse

data class AuthResult(val token: String, val userId: Int, val name: String)

fun AuthResponse.toDomain() = AuthResult(token, userId, name)
