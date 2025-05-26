package com.serranoie.itinero.core.domain.result

data class AuthResult(
    val token: String,
    val userId: Int,
    val name: String
)
