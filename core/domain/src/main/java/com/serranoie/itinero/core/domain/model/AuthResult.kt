package com.serranoie.itinero.core.domain.model

data class AuthResult(
    val token: String,
    val userId: Int,
    val name: String
)
