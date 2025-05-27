package com.serranoie.itinero.core.data.mappers

import com.serranoie.itinero.core.data.remote.dto.AuthResponse
import com.serranoie.itinero.core.domain.result.AuthResult

fun AuthResponse.toDomain() = AuthResult(token, userId, name)