package com.serranoie.itinero.core.data.mappers

import com.serranoie.itinero.core.data.remote.dto.auth.AuthResponse
import com.serranoie.itinero.core.domain.result.AuthResult

fun AuthResponse.toDomain() = AuthResult(token, userId, name, lastName)