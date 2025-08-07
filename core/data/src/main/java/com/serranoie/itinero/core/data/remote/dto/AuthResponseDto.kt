package com.serranoie.itinero.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val userId: Int,
    val name: String,
    val lastName: String
)
