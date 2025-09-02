package com.serranoie.itinero.core.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(val email: String, val password: String)
